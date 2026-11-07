---
name: tenant-aware-entity
description: Use when adding or changing any database table, Flyway migration, JPA entity, or repository in an Allied Payroll service. Enforces multi-tenant isolation (tenant_id + Hibernate @TenantId + PostgreSQL RLS), data scoping, effective dating, auditing, PII encryption and i18n columns.
---

# Tenant-aware entity

Every tenant-owned row in Allied Payroll must be isolated by tenant at **three layers**: the application (Hibernate), the database (RLS), and the tests. Background: `docs/PROJECT_INITIATION.md` §5.3 and `docs/INDUSTRY_PROFILES_AND_STRUCTURE.md`. The full conventions and the per-module DDL are in `docs/schema/README.md` and `docs/schema/{module}.md`; this skill is the checklist, those documents are the design.

> These conventions are proposed. If `backend/platform/common-tenancy` already exists, follow its actual base classes and helpers, and update this skill when they differ.

## Checklist

1. **Decide ownership.**
   - Tenant-owned is the default.
   - Platform-owned is for statutory rule packs, holiday masters and similar. These have **no** `tenant_id`, live in the `compliance` or `tenant` module, and are read-only to tenants.
2. **Flyway migration** (`src/main/resources/db/migration/V{n}__{desc}.sql`):
   ```sql
   CREATE TABLE designation (
     id            UUID PRIMARY KEY,
     tenant_id     UUID NOT NULL,
     code          VARCHAR(30) NOT NULL,
     name          VARCHAR(150) NOT NULL,
     name_i18n     JSONB NOT NULL DEFAULT '{}'::jsonb,   -- user-visible names only
     status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
     version       BIGINT NOT NULL DEFAULT 0,
     created_at    TIMESTAMPTZ NOT NULL, created_by UUID NOT NULL,
     updated_at    TIMESTAMPTZ NOT NULL, updated_by UUID NOT NULL,
     CONSTRAINT uq_designation_code UNIQUE (tenant_id, code)
   );
   CREATE INDEX ix_designation_tenant_status ON designation (tenant_id, status);
   SELECT platform.enable_tenant_rls('organization.designation');  -- enables + forces RLS and creates the tenant_isolation policy
   ```
   - Migration files are `V{m}_{n}__{desc}.sql`; `m` is the module number in `docs/schema/README.md` §2.
   - `tenant_id` is the **first column of every unique constraint and composite index**.
   - Foreign keys point at rows of the same tenant. Where practical, include `tenant_id` in composite FKs.
   - Use `NUMERIC(15,2)` for money. Never use `float` or `double`.
   - Use `DATE` for business dates and `TIMESTAMPTZ` for instants.
   - Partition high-volume tables (paysheet lines, punches, audit) by period, not by tenant.
3. **Entity:**
   - Extend the shared tenant base entity, or declare `@TenantId private UUID tenantId;`.
   - Add `@Version` for optimistic locking.
   - Add audit fields filled by the auditing listener.
   - Never accept `tenantId` from request bodies or path parameters. It always comes from `TenantContext`.
4. **Effective-dated data:** salary structure, deployment, work-location assignment, statutory profile, rate cards and holiday-calendar versions.
   - Use `effective_from DATE NOT NULL, effective_to DATE NULL`, plus an exclusion constraint or service-level check so periods don't overlap.
   - Query "as of date D". Don't update history in place.
5. **Data scope.** If the entity is scoped by legal entity, branch, client or site, add those columns. Make list queries go through the data-scope filter, so users only see the branches and clients they're allowed to.
6. **PII:**
   - PAN, bank account, UAN and phone go through the encrypted-field converter.
   - Store a `*_masked` display column and a `*_hash` blind index for lookups.
   - Aadhaar never goes through the converter and never lands in a module table: store it through the verification module's `AadhaarVault` port and keep `aadhaar_vault_id UUID` plus `aadhaar_last4 CHAR(4)` on the entity (`docs/schema/verification.md`).
7. **Translatable names:** add `name_i18n JSONB` for any master shown to end users (see the `i18n-text` skill).
8. **Lock-after-use masters:** if the master is referenced by transactions, block edit and delete once it's referenced (`in_use` check). Soft-delete only.
9. **Events.** If other services need the change, publish a domain event through the outbox in the same transaction. The event carries `tenantId`.
10. **Tests (required):**
    - A repository integration test with Testcontainers Postgres **running as a non-superuser role**, so RLS is actually applied.
    - A **cross-tenant test**: create data as tenant A, then read, update and delete it as tenant B. B must get nothing, or 404 or 403.
    - Unique-constraint behaviour per tenant: the same `code` is allowed in two tenants.

## Don'ts
- No `@Query` native SQL that bypasses the tenant filter without an explicit `tenant_id = :tenantId` predicate **and** a reviewer comment explaining why.
- No connection-pool user with `BYPASSRLS` in application code paths.
- No industry-specific columns such as `security_post_name`. Use the generic structure (`site_sub_unit`) or custom fields.
