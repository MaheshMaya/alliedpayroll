---
name: new-backend-module
description: Use when adding a Spring Modulith application module (a bounded context such as attendance, payroll, billing, or a channel module such as mobile-channel) to the single Allied Payroll backend, or when deciding whether something is a new module or part of an existing one. Covers module layout, hexagonal packages, the published API package, platform libraries, tenancy/security/i18n wiring, Flyway schema per module, Modulith events, OpenAPI, tests, and the batch profile. Replaces the earlier new-microservice skill (ADR 0003).
---

# New backend module

Read `docs/adr/0003-single-api-backend-web-and-mobile-clients.md`, then `docs/PROJECT_INITIATION.md` §5.2 (module catalogue) and §7 (repository layout). The backend is **one Spring Boot application** in `backend/`; a module is a Spring Modulith application module inside it, never a separate deployable. Don't create a module that duplicates a bounded context already listed; prefer adding a package to an existing module until the context has its own data ownership, lifecycle and team.

> These conventions are agreed but not yet scaffolded. Once modules exist, copy the structure of the most recent one and update this skill when it differs.

## Module kinds

| Kind | Examples | May contain |
|---|---|---|
| **Domain module** | `tenant`, `workforce`, `attendance`, `payroll`, `compliance`, `billing` | Business rules, aggregates, its own schema, REST resources, events |
| **Channel module** | `web-channel`, `mobile-channel`, `device-ingest`, `partner-api` | Shaping, sync protocols, auth handling; **no** business rules or data of its own (ArchUnit enforced) |
| **Platform library** | `backend/platform/common-*` | Cross-cutting code with no business meaning |

## Steps

1. **Location and name.** `backend/modules/{name}` (or `backend/channels/{name}` for a channel module), Gradle project `:backend:modules:{name}`. Base package `com.alliedpayroll.{name}`. The package is the Modulith module; annotate `package-info.java` with `@ApplicationModule` and `@NullMarked`.
2. **Build file.** Apply the convention plugin (`alliedpayroll.backend-module`). Depend on:
   - `backend:platform:common-core`
   - `common-tenancy`
   - `common-security`
   - `common-web`
   - `common-events`
   - `common-test` (test scope)
   - `common-import` only if the module has bulk uploads

   All versions come from `gradle/libs.versions.toml`. The module is registered in `backend/app` by dependency only; no code change in `app`.
3. **Package layout (hexagonal, inside the module):**
   ```
   com.alliedpayroll.{name}
   ├── api            # PUBLISHED: interfaces, DTOs and events other modules may use (the Modulith "named interface")
   ├── web            # @RestController, request/response DTOs, mappers, OpenAPI annotations
   ├── application    # use cases / application services, transactions, ports (interfaces)
   ├── domain         # aggregates, value objects, domain services, strategies — no Spring web, JPA or messaging imports
   └── infrastructure # JPA repositories, event listeners, external clients, config
   ```
   - Other modules import **only** `com.alliedpayroll.{name}.api`. The Modulith verification test (`ApplicationModules.of(BackendApplication.class).verify()`) fails the build otherwise.
   - The shared ArchUnit suite from `common-test` enforces the layer rules and keeps channel modules out of domain internals.
4. **Database.** One PostgreSQL database; the module owns schema `{name}` and its Flyway migrations under `db/migration/{name}/`, versioned `V{m}_{n}__{desc}.sql` with the module number from `docs/schema/README.md` §2. The module's design document in `docs/schema/{name}.md` holds the DDL for `V{m}_1`; write or update that document first, then lift the DDL. Cross-module joins are forbidden; read the other module's `api` instead. Every tenant-owned table follows `tenant-aware-entity` (tenant_id, `@TenantId`, RLS, composite index). The datasource user has **no BYPASSRLS**.
5. **Configuration.** Typed `@ConfigurationProperties` under `alliedpayroll.{name}.*`; messages in `classpath:i18n/{name}/messages` (UTF-8, fallback `en-IN`); nothing module-specific in `backend/app` `application.yml` beyond the profile defaults.
6. **API.**
   - Prefix `/api/v1/{resource}` with canonical, channel-neutral resource names (`headless-api`). Channel modules use `/api/v1/mobile/...`, `/auth/...`, `/ingest/...`, `/partner/v1/...`.
   - Lists support the standard filter, sort, page/cursor and search parameters plus the global context (`legalEntityId`, `branchId`, `period`, `clientId`, `siteId`).
   - Errors are `problem+json` with a stable `code` and localized `title`/`detail`.
   - The springdoc group for the module contributes to the single committed OpenAPI document in `api/openapi/`; the breaking-change check runs in CI.
7. **Events.**
   - Publish domain events from application services with `ApplicationEventPublisher`; Spring Modulith's **event publication registry** persists them in the same transaction (the outbox). Event types are records in the module's `api` package, named `{Aggregate}{PastTenseVerb}` (`WorkInformationApproved`), carrying `tenantId`, `occurredAt`, ids and minimal data.
   - Listeners in other modules are `@ApplicationModuleListener` (async, transactional, idempotent via a processed-event table keyed by publication id). Incomplete publications are resubmitted on startup and alerted on.
   - Externalise to Kafka only through `common-events` when ADR 0003's condition is met (Phase 9); never call Kafka directly from a module.
8. **Security.** Endpoints declare `@RequiresPermission("payroll:approve")` and data-scope filtering from `common-security`; tenant comes from `TenantContext`, never from the request. Add the module's permissions to the catalogue in the `identity` module and the channel policy (which channels may call what) in `common-security`.
9. **Industry profiles.** Read capabilities from tenant settings and feature flags (`tenantFeatures.isEnabled(Module.X)`); never branch on the profile name (`industry-profile-feature`).
10. **Batch.** Long work is a Spring Batch job in the module, launched through the shared job table and executed by the `backend-batch` deployment (`@Profile("batch")` for the launcher); the API returns `202` with a job resource.
11. **Tests.**
    - `@ApplicationModuleTest` for the module with Testcontainers Postgres: context loads, RLS and tenant-isolation test for every aggregate, migration test, event publication test.
    - Modulith verification and ArchUnit pass.
    - The mandatory slice tests in `testing-strategy` §2.
12. **Deployment.** Nothing new: the module ships inside the single image. Add the module's health indicators and metrics tags; update the Helm values only if the module needs a new secret or external service.
13. **Docs.** Update the module table in `docs/PROJECT_INITIATION.md` §5.2 and the Modulith documentation output (`Documenter`) committed under `docs/modules/`. Add an ADR if a new module boundary was decided. Add build and run commands to `CLAUDE.md` if they differ from the standard ones.
