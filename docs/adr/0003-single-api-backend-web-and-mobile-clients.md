# ADR 0003: One API backend, one web app, one mobile app

**Status:** Accepted · **Date:** 2026-11-03 · **Deciders:** Mahesh (product/engineering lead) · **Supersedes:** PROJECT_INITIATION §5.2 service decomposition (16 services) and the "modular monolith then split" recommendation in DEVELOPMENT_PLAN §1

## Context

The initiation document mapped the product onto 16 microservices behind an API gateway with two backends-for-frontends. The development plan then recommended starting with a modular monolith per bounded-context group and splitting services out phase by phase. Both put a lot of deployables, network hops and operational surface in front of a team of 10–12 that has to ship a correct payroll engine first. The user's direction is simpler: **one backend exposed as an API, one web front end, one mobile application.**

## Decision

The system has three codebases and four deployables.

| Deployable | What it is |
|---|---|
| `backend-api` | **One Spring Boot application** (`backend/`) that exposes the whole REST API (`/api/v1/...`) for every channel. Internally it is a set of **Spring Modulith application modules**, one per bounded context (tenant, identity, organization, workforce, customer-contract, attendance, payroll, compliance, billing, approval, documents, notification, audit, verification, reporting, device-ingest, inventory-projects) plus **channel modules** that only shape data (`web-channel`, `mobile-channel`, `partner-api`). One PostgreSQL database, one schema per module, RLS on every tenant-owned table. One OpenAPI document; one generated SDK per client. |
| `backend-batch` | The **same image** started with the `batch` profile and no inbound traffic. Runs Spring Batch jobs (payroll runs, imports, statutory files, reports) and scheduled work, so month-end load never slows the API pods. |
| `web` | **One React SPA** (`frontend`) for admins, payroll and HR staff, tablet supervisors and, later, the client portal as a role. Static hosting behind the same ingress. |
| `mobile` | **One Flutter app** (`mobile`) with employee, manager and kiosk modes, as already decided in MOBILE_AND_DEVICES. |

Consequences for the pieces that go away:

- **No API gateway service.** TLS, subdomain routing, WAF and coarse rate limiting come from the cloud load balancer / Kubernetes ingress. Tenant resolution, JWT validation, channel tagging and per-tenant rate limiting are a filter chain inside the backend (`common-tenancy`, `common-security`).
- **No web-bff / mobile-bff services.** The token-handler session for browsers lives in `web-channel` (`/auth/*`); bootstrap, delta sync and the offline punch queue live in `mobile-channel` (`/api/v1/mobile/...`); vendor device protocols in `device-ingest`; public API products and webhooks in `partner-api`. These modules call domain module APIs and own no business rules, enforced by ArchUnit.
- **Kafka is deferred.** Cross-module effects use Spring Modulith's event publication registry, which is a transactional outbox in PostgreSQL with asynchronous, idempotent listeners and replay of incomplete publications. Kafka is introduced only when an external consumer needs a stream (device ingest at scale, partner event feeds), expected in Phase 9, behind the same `common-events` abstraction.
- **No service-to-service calls**, so no internal JWT, mTLS or service discovery.

Module boundaries are verified in CI by the Modulith verification test (no cyclic dependencies, only published API packages are imported) and by ArchUnit (hexagonal layers; channel modules import no domain internals). A module becomes its own deployable only through a new ADR; because modules own their schema and communicate through published APIs and events, that move would not change API paths or event types.

## Consequences

- Good: one build, one deploy, one log stream, one database to back up; a change to a payroll rule is one PR in one repo; local development is `docker compose up` plus one process.
- Good: transactions span modules where correctness needs it (attendance lock plus payroll snapshot) without sagas.
- Good: the batch deployment gives the isolation microservices were meant to provide for the one path that needs it.
- Bad: a tangle is one lazy import away; the Modulith and ArchUnit tests are therefore mandatory CI gates, not advisory.
- Bad: the whole backend scales as one unit; profiles and HPA on `backend-api` plus the separate batch deployment cover the targets in PROJECT_INITIATION §5.6. Revisit via ADR if a single module dominates resource use.
- Bad: one JVM startup carries every module; keep startup under 20 s with lazy initialisation and AOT where it helps.

## Alternatives considered

- **Microservices from the start** (initiation document): operational cost and distributed-transaction complexity far ahead of need. Rejected.
- **Modular monolith first, split per phase** (development plan): the same code structure as this decision, but with planned extraction and the gateway/BFF services kept. Rejected because the extraction schedule itself was speculative and the extra deployables had no owner.
- **Separate repositories per codebase**: rejected for now; one repository keeps the OpenAPI contract, generated SDKs and vertical slices in one PR. Split later if the mobile team needs its own release cadence.

## Follow-ups

- DEVELOPMENT_PLAN §1 decision 2 closed; §4 phase rows, §5 and §7 rewritten in module terms.
- PROJECT_INITIATION §5.1–§5.4, §7, §8, §10 and §11 updated; MULTI_CHANNEL_API §2, §4, §9 updated; TEAM_SKILLS_MATRIX updated.
- Skill `new-microservice` replaced by `new-backend-module`; BFF and service wording removed from the other skills.
- ADR 0004 (no GraphQL) still to write in Phase 0.
