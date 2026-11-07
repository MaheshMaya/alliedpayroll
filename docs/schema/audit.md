# Schema: audit (Phase 1)

Owner: the `audit` module. Migration namespace `V3_n`. Requirements: [BACKEND_REQUIREMENTS.md](../BACKEND_REQUIREMENTS.md) §4.15 and §7; the `secure-coding` and `tenant-aware-entity` skills.

## 1. Purpose

One immutable, append-only trail of every create, update and delete on tenant data, plus the security events that are not data changes: sign-ins, failures, permission denials, PII reveals, exports. Written in the same transaction as the change by the auditing listener in `common-core`; queried through the API within the caller's data scope; retained at least eight years for payroll and invoice-related rows.

## 2. Table

Partitioned by month on `occurred_at`. The primary key includes the partition column, as PostgreSQL requires. Old and new values are stored masked: the auditing listener applies the same masking the API uses, so a PAN never appears in the trail in full.

```sql
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

-- Append-only for the application role.
GRANT SELECT, INSERT ON audit.audit_event TO app;
REVOKE UPDATE, DELETE, TRUNCATE ON audit.audit_event FROM app;

-- First partitions; a backend-batch job creates the next three months ahead.
CREATE TABLE audit.audit_event_2026_11 PARTITION OF audit.audit_event
  FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE audit.audit_event_2026_12 PARTITION OF audit.audit_event
  FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');
CREATE TABLE audit.audit_event_default PARTITION OF audit.audit_event DEFAULT;
```

The `DEFAULT` partition exists so an insert never fails if the partition job is late; an alert fires when it has any rows.

## 3. Rules

- **Same transaction.** The listener writes the row before commit; if the audit insert fails, the business change fails.
- **Masked values only.** `old_values` and `new_values` carry the masked representation (`XXXX1234`), the blind index is never stored, encrypted columns are omitted. The diff is field-level (`changed_fields`), not a full row copy, for updates.
- **No PII in `entity_label` or `note`.** Labels are codes and numbers. Free-text reasons are validated for length only, so users are told not to type PII there, and reveals require a note.
- **Security events** (`LOGIN*`, `AUTHZ_DENIED`, `PII_REVEAL`, `EXPORT`) are written by `common-security` and the reveal and export endpoints, not by the entity listener.
- **Read access** goes through the API with the caller's data scope: an audit row is visible when the caller may see the entity it belongs to (the scope columns make that a predicate, not a join). Audit of identity changes needs `audit:security_read`.
- **Retention** by partition: partitions older than the tenant's retention (minimum 8 years for anything payroll, invoice, statutory; 2 years for security events, which live in the same table and are filtered on purge) are detached and archived to object storage as Parquet by a batch job before being dropped. Nothing is ever `DELETE`d row by row.

## 4. Events

None. The audit module consumes nothing from the registry either; it is written synchronously by the listener. A later read model for "recent activity" cards is a reporting concern.

## 5. Open items

- Whether `client_ip` is kept for the full retention or nulled after 90 days (DPDP minimisation). Proposed: nulled after 90 days by the same batch job that manages partitions.
- Parquet archive format and the restore path need a short design in Phase 9 with the reporting module.
