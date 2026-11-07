-- Lifted from docs/schema/platform.md §3.1 to §3.5 plus Spring Modulith's event registry tables;
-- roles and login users come from db/init/01-roles.sql, run once by the database bootstrap.
SET ROLE migrate;


CREATE EXTENSION IF NOT EXISTS btree_gist;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS ltree;

CREATE SCHEMA platform AUTHORIZATION migrate;
CREATE SCHEMA batch AUTHORIZATION migrate;

-- Tenant of the current transaction, or NULL when none was set (fail closed).
CREATE FUNCTION platform.current_tenant() RETURNS uuid
  LANGUAGE sql STABLE PARALLEL SAFE AS
$$ SELECT NULLIF(current_setting('app.tenant_id', true), '')::uuid $$;

-- Applies the standard isolation policy to a tenant-owned table.
CREATE FUNCTION platform.enable_tenant_rls(tbl regclass) RETURNS void
  LANGUAGE plpgsql AS $$
BEGIN
  EXECUTE format('ALTER TABLE %s ENABLE ROW LEVEL SECURITY', tbl);
  EXECUTE format('ALTER TABLE %s FORCE ROW LEVEL SECURITY', tbl);
  EXECUTE format(
    'CREATE POLICY tenant_isolation ON %s
       USING (tenant_id = platform.current_tenant())
       WITH CHECK (tenant_id = platform.current_tenant())', tbl);
END $$;

-- Fixed actor id for writes made by jobs and event listeners.
CREATE FUNCTION platform.system_actor() RETURNS uuid
  LANGUAGE sql IMMUTABLE PARALLEL SAFE AS
$$ SELECT '00000000-0000-0000-0000-000000000001'::uuid $$;
CREATE TABLE platform.processed_event (
  listener_id     VARCHAR(200) NOT NULL,
  publication_id  UUID NOT NULL,
  processed_at    TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (listener_id, publication_id)
);
CREATE TABLE platform.job (
  id                      UUID PRIMARY KEY,
  tenant_id               UUID NOT NULL,
  job_type                VARCHAR(60) NOT NULL,          -- IMPORT_EMPLOYEES, PAYROLL_RUN, EXPORT, STATUTORY_FILE, ...
  status                  VARCHAR(20) NOT NULL DEFAULT 'QUEUED'
                          CHECK (status IN ('QUEUED','RUNNING','SUCCEEDED','FAILED','CANCELLED')),
  requested_by            UUID NOT NULL,
  channel                 VARCHAR(20) NOT NULL,          -- WEB, MOBILE, KIOSK, DEVICE, PARTNER, SYSTEM
  idempotency_key         VARCHAR(128) NULL,
  legal_entity_id         UUID NULL,                     -- data-scope columns so "my jobs" lists filter correctly
  branch_id               UUID NULL,
  client_id               UUID NULL,
  params                  JSONB NOT NULL DEFAULT '{}',
  progress_pct            SMALLINT NOT NULL DEFAULT 0 CHECK (progress_pct BETWEEN 0 AND 100),
  progress_message_key    VARCHAR(120) NULL,
  progress_params         JSONB NOT NULL DEFAULT '{}',
  result                  JSONB NOT NULL DEFAULT '{}',   -- artefact ids, counts, links
  error_code              VARCHAR(80) NULL,
  error_params            JSONB NULL,
  batch_job_execution_id  BIGINT NULL,
  queued_at               TIMESTAMPTZ NOT NULL,
  started_at              TIMESTAMPTZ NULL,
  finished_at             TIMESTAMPTZ NULL,
  version                 BIGINT NOT NULL DEFAULT 0,
  created_at              TIMESTAMPTZ NOT NULL, created_by UUID NOT NULL,
  updated_at              TIMESTAMPTZ NOT NULL, updated_by UUID NOT NULL,
  CONSTRAINT uq_job_idempotency UNIQUE (tenant_id, idempotency_key)
);
CREATE INDEX ix_job_tenant_status_queued ON platform.job (tenant_id, status, queued_at DESC);
CREATE INDEX ix_job_tenant_requested_by ON platform.job (tenant_id, requested_by, queued_at DESC);
CREATE INDEX ix_job_status_queued ON platform.job (status, queued_at) WHERE status IN ('QUEUED','RUNNING');
SELECT platform.enable_tenant_rls('platform.job');
CREATE TABLE platform.import_batch (
  id                    UUID PRIMARY KEY,
  tenant_id             UUID NOT NULL,
  job_id                UUID NULL REFERENCES platform.job (id),
  entity_type           VARCHAR(60) NOT NULL,            -- EMPLOYEE, WORK_LOCATION, ATTENDANCE_DAYWISE, ...
  mode                  VARCHAR(10) NOT NULL CHECK (mode IN ('CREATE','UPDATE')),
  template_version      VARCHAR(20) NOT NULL,
  status                VARCHAR(20) NOT NULL DEFAULT 'UPLOADED'
                        CHECK (status IN ('UPLOADED','VALIDATING','VALIDATED','HAS_ERRORS',
                                          'COMMITTING','COMMITTED','FAILED','DISCARDED')),
  source_artefact_id    UUID NOT NULL,                   -- the uploaded file
  error_artefact_id     UUID NULL,                       -- the error file, when produced
  file_name             VARCHAR(255) NOT NULL,
  file_sha256           BYTEA NOT NULL,
  uploaded_by           UUID NOT NULL,
  locale                VARCHAR(16) NOT NULL,            -- headers and error messages in this locale
  legal_entity_id       UUID NULL,
  branch_id             UUID NULL,
  total_rows            INTEGER NOT NULL DEFAULT 0,
  valid_rows            INTEGER NOT NULL DEFAULT 0,
  error_rows            INTEGER NOT NULL DEFAULT 0,
  created_count         INTEGER NOT NULL DEFAULT 0,
  updated_count         INTEGER NOT NULL DEFAULT 0,
  committed_at          TIMESTAMPTZ NULL,
  version               BIGINT NOT NULL DEFAULT 0,
  created_at            TIMESTAMPTZ NOT NULL, created_by UUID NOT NULL,
  updated_at            TIMESTAMPTZ NOT NULL, updated_by UUID NOT NULL
);
CREATE INDEX ix_import_batch_tenant_entity ON platform.import_batch (tenant_id, entity_type, created_at DESC);
SELECT platform.enable_tenant_rls('platform.import_batch');

CREATE TABLE platform.import_row (
  tenant_id         UUID NOT NULL,
  batch_id          UUID NOT NULL REFERENCES platform.import_batch (id) ON DELETE CASCADE,
  row_no            INTEGER NOT NULL,                    -- 1-based sheet row, as the user sees it
  raw               JSONB NOT NULL,                      -- cell values by canonical key, as uploaded
  normalised        JSONB NULL,                          -- parsed, trimmed, typed; null until validated
  status            VARCHAR(12) NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','VALID','ERROR','COMMITTED','SKIPPED')),
  errors            JSONB NOT NULL DEFAULT '[]',         -- [{"column":"pan","code":"validation.pan.format","params":{}}]
  suggestion        JSONB NULL,                          -- "use suggestion" fix the UI can apply
  target_entity_id  UUID NULL,                           -- the created or updated row after commit
  PRIMARY KEY (batch_id, row_no)
);
CREATE INDEX ix_import_row_batch_status ON platform.import_row (batch_id, status);
SELECT platform.enable_tenant_rls('platform.import_row');
CREATE TABLE platform.artefact (
  id                UUID PRIMARY KEY,
  tenant_id         UUID NOT NULL,
  kind              VARCHAR(40) NOT NULL,                -- UPLOAD, IMPORT_ERROR_FILE, EXPORT, PDF, STATUTORY_FILE, BANK_FILE, PHOTO
  object_key        VARCHAR(512) NOT NULL,               -- {tenant}/{kind}/{yyyy}/{mm}/{id}.{ext}
  content_type      VARCHAR(120) NOT NULL,
  size_bytes        BIGINT NOT NULL,
  sha256            BYTEA NOT NULL,
  file_name         VARCHAR(255) NOT NULL,
  produced_by_job_id UUID NULL REFERENCES platform.job (id),
  owner_type        VARCHAR(40) NULL,                    -- EMPLOYEE, INVOICE, PAYROLL_RUN, ... (polymorphic, id only)
  owner_id          UUID NULL,
  av_status         VARCHAR(12) NOT NULL DEFAULT 'PENDING' CHECK (av_status IN ('PENDING','CLEAN','INFECTED','SKIPPED')),
  retention_until   DATE NULL,
  version           BIGINT NOT NULL DEFAULT 0,
  created_at        TIMESTAMPTZ NOT NULL, created_by UUID NOT NULL,
  updated_at        TIMESTAMPTZ NOT NULL, updated_by UUID NOT NULL,
  deleted_at        TIMESTAMPTZ NULL,
  CONSTRAINT uq_artefact_object_key UNIQUE (tenant_id, object_key)
);
CREATE INDEX ix_artefact_tenant_owner ON platform.artefact (tenant_id, owner_type, owner_id);
CREATE INDEX ix_artefact_retention ON platform.artefact (retention_until) WHERE deleted_at IS NULL;
SELECT platform.enable_tenant_rls('platform.artefact');

-- Spring Modulith event publication registry (schema v2), owned here so the application role needs no DDL.
CREATE TABLE platform.event_publication (
  id                     UUID NOT NULL,
  listener_id            TEXT NOT NULL,
  event_type             TEXT NOT NULL,
  serialized_event       TEXT NOT NULL,
  publication_date       TIMESTAMPTZ NOT NULL,
  completion_date        TIMESTAMPTZ NULL,
  status                 TEXT NULL,
  completion_attempts    INT NULL,
  last_resubmission_date TIMESTAMPTZ NULL,
  PRIMARY KEY (id)
);
CREATE INDEX event_publication_serialized_event_hash_idx ON platform.event_publication USING hash (serialized_event);
CREATE INDEX event_publication_by_completion_date_idx ON platform.event_publication (completion_date);

CREATE TABLE platform.event_publication_archive (
  id                     UUID NOT NULL,
  listener_id            TEXT NOT NULL,
  event_type             TEXT NOT NULL,
  serialized_event       TEXT NOT NULL,
  publication_date       TIMESTAMPTZ NOT NULL,
  completion_date        TIMESTAMPTZ NULL,
  status                 TEXT NULL,
  completion_attempts    INT NULL,
  last_resubmission_date TIMESTAMPTZ NULL,
  PRIMARY KEY (id)
);
CREATE INDEX event_publication_archive_serialized_event_hash_idx ON platform.event_publication_archive USING hash (serialized_event);
CREATE INDEX event_publication_archive_by_completion_date_idx ON platform.event_publication_archive (completion_date);

GRANT USAGE ON SCHEMA platform TO app, readonly;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA platform TO app;
GRANT SELECT ON ALL TABLES IN SCHEMA platform TO readonly;
ALTER DEFAULT PRIVILEGES FOR ROLE migrate IN SCHEMA platform GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app;
ALTER DEFAULT PRIVILEGES FOR ROLE migrate IN SCHEMA platform GRANT SELECT ON TABLES TO readonly;
GRANT USAGE ON SCHEMA batch TO app;
