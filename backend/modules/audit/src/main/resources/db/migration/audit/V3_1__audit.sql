-- Lifted from docs/schema/audit.md; the design document is the source of truth.
SET ROLE migrate;

CREATE SCHEMA audit AUTHORIZATION migrate;

CREATE TABLE audit.audit_event (
  id              UUID NOT NULL,
  tenant_id       UUID NOT NULL,
  occurred_at     TIMESTAMPTZ NOT NULL,
  action          VARCHAR(20) NOT NULL CHECK (action IN (
                    'CREATE','UPDATE','DELETE','STATE_CHANGE',
                    'LOGIN','LOGIN_FAILED','LOGOUT','OTP_SENT','OTP_FAILED',
                    'AUTHZ_DENIED','PII_REVEAL','EXPORT','IMPORT_COMMIT','APPROVE','REJECT')),
  entity_type     VARCHAR(60) NULL,                         -- EMPLOYEE, PAYROLL_RUN, USER, ... null for pure security events
  entity_id       UUID NULL,
  entity_label    VARCHAR(150) NULL,                        -- employee code or document number for lists, never PII
  actor_type      VARCHAR(12) NOT NULL CHECK (actor_type IN ('USER','SYSTEM','DEVICE','PARTNER')),
  actor_id        UUID NOT NULL,                            -- app_user, system actor, device or partner client id
  channel         VARCHAR(12) NOT NULL,
  device_id       UUID NULL,
  app_version     VARCHAR(20) NULL,
  client_ip       INET NULL,
  trace_id        VARCHAR(64) NULL,
  request_id      VARCHAR(64) NULL,
  legal_entity_id UUID NULL,                                -- data-scope columns copied from the entity
  branch_id       UUID NULL,
  client_id       UUID NULL,
  site_id         UUID NULL,
  changed_fields  TEXT[] NULL,
  old_values      JSONB NULL,                               -- masked
  new_values      JSONB NULL,                               -- masked
  reason_key      VARCHAR(120) NULL,                        -- message key for denied/failed outcomes
  reason_params   JSONB NULL,
  note            VARCHAR(500) NULL,                        -- user-entered reason (approvals, reveals)
  PRIMARY KEY (id, occurred_at)
) PARTITION BY RANGE (occurred_at);

CREATE INDEX ix_audit_event_entity ON audit.audit_event (tenant_id, entity_type, entity_id, occurred_at DESC);
CREATE INDEX ix_audit_event_actor  ON audit.audit_event (tenant_id, actor_id, occurred_at DESC);
CREATE INDEX ix_audit_event_tenant_time ON audit.audit_event (tenant_id, occurred_at DESC);
CREATE INDEX ix_audit_event_action ON audit.audit_event (tenant_id, action, occurred_at DESC)
  WHERE action IN ('LOGIN_FAILED','AUTHZ_DENIED','PII_REVEAL','EXPORT');

SELECT platform.enable_tenant_rls('audit.audit_event');

-- Append-only for the application role, on the parent and on every partition to come.
GRANT USAGE ON SCHEMA audit TO app, readonly;
GRANT SELECT, INSERT ON audit.audit_event TO app;
GRANT SELECT ON audit.audit_event TO readonly;
ALTER DEFAULT PRIVILEGES FOR ROLE migrate IN SCHEMA audit GRANT SELECT, INSERT ON TABLES TO app;
ALTER DEFAULT PRIVILEGES FOR ROLE migrate IN SCHEMA audit GRANT SELECT ON TABLES TO readonly;

-- First partitions; the batch deployment calls audit.ensure_partition for the months ahead.
CREATE TABLE audit.audit_event_2026_11 PARTITION OF audit.audit_event
  FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE audit.audit_event_2026_12 PARTITION OF audit.audit_event
  FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');
CREATE TABLE audit.audit_event_default PARTITION OF audit.audit_event DEFAULT;

-- Partition DDL needs the owner; the application role gets this one door, nothing else.
CREATE FUNCTION audit.ensure_partition(month_start DATE) RETURNS TEXT
  LANGUAGE plpgsql SECURITY DEFINER SET search_path = audit, pg_temp AS $$
DECLARE
  first_day DATE := date_trunc('month', month_start)::date;
  partition_name TEXT := format('audit_event_%s', to_char(first_day, 'YYYY_MM'));
BEGIN
  IF to_regclass(format('audit.%I', partition_name)) IS NULL THEN
    EXECUTE format(
      'CREATE TABLE audit.%I PARTITION OF audit.audit_event FOR VALUES FROM (%L) TO (%L)',
      partition_name, first_day, (first_day + INTERVAL '1 month')::date);
  END IF;
  RETURN partition_name;
END $$;
REVOKE ALL ON FUNCTION audit.ensure_partition(DATE) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION audit.ensure_partition(DATE) TO app;
