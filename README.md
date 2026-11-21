# Allied Payroll

Payroll and workforce management for Indian staffing, facility-management and field-service agencies. One API backend (Spring Boot 4, Java 25, Spring Modulith), one React web app, one Flutter app.

## Layout

| Folder | What |
|---|---|
| `backend/` | The single Spring Boot application: platform libraries, one Modulith module per bounded context, channel modules. Gradle Kotlin DSL. |
| `frontend/` | React 19 + TypeScript + Vite web app for admins, payroll and HR staff. |
| `mobile/` | Flutter app: employee, manager and kiosk modes. |
| `design-tokens/` | DTCG tokens (`tokens.json`) and the Style Dictionary build that feeds web and mobile themes. |
| `docs/` | Requirements, architecture decisions (`docs/adr`), schema design (`docs/schema`), plans. |
| `docker-compose.yml` | The whole local stack: PostgreSQL, Keycloak, Redis, backend-api, backend-batch, web. |

## Prerequisites

- JDK 25 (Temurin), Node 22 with pnpm 12, Docker Compose (Rancher Desktop with the moby engine works).
- Flutter stable for `mobile/`.

## Run everything

```sh
docker compose up --build
```

Web: http://localhost:5173 · API: http://localhost:8088 · Keycloak: http://localhost:8081 (admin / see `.env.example`).

Demo tenants `sentinel` (staffing) and `nimbus-it` (IT) are seeded with one user per persona; credentials are in `backend/dev/seed/README.md`.

## Develop

```sh
pnpm install                          # hooks, tokens, frontend
docker compose up postgres keycloak redis
scripts/gradle test                   # backend checks in the toolchain container
pnpm web dev                          # web on :5173
```

Checks that must be green before a commit (the pre-commit hook runs the fast ones):

```sh
scripts/gradle spotlessCheck compileJava compileTestJava test
pnpm web typecheck && pnpm web lint && pnpm web test -- --run
pnpm tokens:build
```

Conventions: Conventional Commits, Google Java Style, `strictTypeChecked` TypeScript, Effective Dart. See `CLAUDE.md` and `docs/adr/0002-coding-conventions.md`.
