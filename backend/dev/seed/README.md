# Demo tenants and users (development only)

Seeded by `docker compose up`: the tenants by the backend's `local` profile, the organisations and users by `deploy/keycloak/seed-keycloak.sh`. All names are fictional.

| Tenant host | Profile | Users (password `Welcome@123` unless `DEMO_USER_PASSWORD` is set) |
|---|---|---|
| `sentinel.localhost` | STAFFING (security and facility agency) | `admin.sentinel`, `payroll.sentinel`, `hr.sentinel`, `guard.sentinel` |
| `nimbus-it.localhost` | CORPORATE (IT company) | `admin.nimbus`, `payroll.nimbus`, `staff.nimbus` |

A user belongs to exactly one organisation. Signing in to `nimbus-it.localhost` with a Sentinel user fails at Keycloak, and a Sentinel token on the Nimbus host gets `403` from the API; both are covered by tests.

Keycloak admin console: http://auth.localhost:8081 with `admin` / `admin`.
