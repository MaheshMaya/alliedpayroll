# Foundation plan: walking skeleton proven by "sign in and see Home"

Approved 2026-11-07. Phase 0 of [DEVELOPMENT_PLAN.md](../../docs/DEVELOPMENT_PLAN.md), cut as one vertical slice per the `vertical-slice` skill.

## Decisions taken

| Topic | Decision |
|---|---|
| Layout | `backend/` (Gradle), `frontend/` (React web), `mobile/` (Flutter, later slice), `design-tokens/` at the root; docker-compose at the root runs everything |
| Toolchain | Temurin JDK 25.0.5 in `%USERPROFILE%\.jdks` (user space), Gradle 9.9.0 wrapper, Node 22 + pnpm 12 (user-level npm global), Rancher Desktop with moby for Docker Compose |
| Versions | Spring Boot 4.1.2, Spring Modulith 2.1.2, Flyway 13.12.1, Testcontainers 2.0.5, Error Prone 2.52.0, NullAway 0.14.3, Spotless 8.11.0, ArchUnit 1.5.1, springdoc 3.2.0, Keycloak 26.8.2, PostgreSQL 17, Redis 8; React 19.3, Vite 8, TypeScript 5.9 (typescript-eslint peer range stops below 6), Tailwind 4, TanStack Router/Query/Table, Vitest 5, Storybook 10, Playwright 1.65, Style Dictionary 5, react-intl 12 |
| Git | Local repository on `main`, Conventional Commits, lefthook; remote added when the organisation exists |
| Mobile | Not in this slice; Flutter's git TLS issue is fixed first |

## Steps and their verification

| # | Step | Verified by |
|---|---|---|
| 1 | Repo hygiene: `.gitignore`, `.editorconfig`, README, pnpm workspace, lefthook + commitlint | first commit passes the hooks |
| 2 | Backend build: `build-logic` convention plugins, version catalog, `app` + `platform/common-*` + `modules/tenant`, `web-channel`, `mobile-channel`; Spotless, Error Prone + NullAway (JSpecify), Checkstyle; JUnit + Testcontainers; Modulith and ArchUnit tests | `./gradlew spotlessCheck compileJava compileTestJava test` green |
| 3 | Local stack: root `docker-compose.yml` with PostgreSQL 17 (roles from `docs/schema/platform.md`), Keycloak 26.8 with realm import (organisations, PKCE client), Redis; `backend/Dockerfile`, `frontend/Dockerfile`; seeded demo tenants `sentinel` and `nimbus-it` | `docker compose up` brings the stack up healthy |
| 4 | Flyway `V0_1` (platform) and `V1_1` (tenant) verbatim from the schema docs; tenancy filter, RLS hook, `GET /api/v1/bootstrap`; `web-channel` `/auth/login`, `/auth/callback`, `/auth/logout` (Authorization Code + PKCE, session cookie, CSRF) | integration tests: 401 unauthenticated, cross-tenant isolation, RLS as the `app` role |
| 5 | Frontend build: Vite + React + TS strict, typescript-eslint `strictTypeChecked`, Prettier, Tailwind 4 + shadcn/ui, TanStack, react-intl with pseudo-locale, Vitest, Storybook, Playwright; Style Dictionary from `design-tokens/tokens.json` with a staleness check; OpenAPI → TS SDK | `pnpm typecheck lint test` green; token build reproducible |
| 6 | App Shell v2 from Figma (left nav, utility rail, context bar) and Home reading `/bootstrap` | Playwright signs in as a seeded user through Keycloak and sees the tenant name |
| 7 | Gates: `verify.sh` runs Gradle and pnpm rung 1; CLAUDE.md build/lint/test commands; antislop Delivery Gate | Stop hook green; slice closed per `vertical-slice` §5 |

## Out of scope

Mobile scaffold, CI on GitHub Actions and Helm charts (no remote yet), any business module beyond `tenant`.

## Outcome (2026-11-07)

Every step done. Backend: 14 tests across 5 suites (Modulith boundaries, ArchUnit, bootstrap isolation with 401/403, RLS as the app role, OpenAPI export). Web: typecheck, lint, format and 16 unit tests green; Storybook builds. Stack: `docker compose up --build` runs postgres, redis, keycloak with seed, backend-api, backend-batch, web. Proven in a browser through the real Keycloak login (PKCE, `organization:<slug>` scope): Sentinel admin sees Home on `sentinel.localhost`; the same account on `nimbus-it.localhost` gets 403 from the API and the not-a-member screen; sign-out ends both sessions. Screenshot: `2026-11-07-home-signed-in.png`.

Deviations: API on port 8088 (Rancher Desktop holds 8080); SDK generated into `api/sdk/typescript` (own compiler settings) instead of inside the web app; Flyway history table in schema `flyway`; Playwright login spec written but not executed here because browsers are not installed. Mobile scaffold, CI, Helm and the Redis session store are the next slices.
