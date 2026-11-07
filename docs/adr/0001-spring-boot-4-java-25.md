# ADR 0001: Spring Boot 4.x on Java 25 LTS

**Status:** Accepted · **Date:** 2026-11-03 · **Deciders:** Mahesh (product/engineering lead)

## Context

[PROJECT_INITIATION.md](../PROJECT_INITIATION.md) §5.1 proposed Java 21 and Spring Boot 3.x, written before Spring Boot 4.0 went GA (November 2025). By kickoff the 4.x line has two released minors (4.0, 4.1), Spring Boot 3.x enters its final year of open-source support, and Java 25 is the current LTS. The backend will be maintained for years and audited for payroll correctness, so starting on a line that forces a major upgrade within the first year is a cost with no upside.

Spring Boot 4 brings Spring Framework 7, Hibernate 7, Jakarta EE 11, Jackson 3, JSpecify null-safety across the portfolio, and modular starters. Those changes are breaking relative to 3.x, which is a reason to adopt them before any domain code exists, not after.

## Decision

All backend services and platform libraries use **Spring Boot 4.x on Java 25 LTS**. The exact patch versions are pinned in the Gradle version catalog at Phase 0 kickoff and bumped on the normal dependency cadence. Phase 0 proves multi-tenancy (Hibernate 7 `@TenantId` plus PostgreSQL RLS), Jackson 3 serialization and JSpecify null checking on the real versions before Phase 1 starts.

## Consequences

- Good: no major framework migration during the pilot year; JSpecify + NullAway give compile-time null safety from day one; virtual threads and records are mature on Java 25.
- Good: the Phase 0 walking skeleton is the proving ground, so surprises surface before business code exists.
- Bad: some third-party libraries lag Boot 4 / Jackson 3 (check each dependency for `tools.jackson` support before adopting it); community examples still mostly target Boot 3.
- Bad: team members current on Boot 3 need the Boot 4 migration notes read before Phase 0 (TEAM_SKILLS_MATRIX §1.2 updated).

## Alternatives considered

- **Spring Boot 3.5 on Java 21** (the original proposal): lower risk on the day, guaranteed major upgrade within 12 months. Rejected.
- **Quarkus or Micronaut**: smaller ecosystem for Spring Batch, Spring Security, Modulith and the team's existing skills. Rejected.

## Follow-ups

- [docs/DEVELOPMENT_PLAN.md](../DEVELOPMENT_PLAN.md) §1 decision 1 closed; PROJECT_INITIATION §5.1 and TEAM_SKILLS_MATRIX §1.2 updated.
- ADR 0002 fixes the coding conventions and toolchain that go with this stack.
