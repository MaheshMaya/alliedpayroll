# Demo tenants and users (development only)

Seeded by `docker compose up`: the tenants by the backend's `local` profile, the organisations and users by `deploy/keycloak/seed-keycloak.sh`. All names are fictional.

| Tenant host | Profile | Users (password `Welcome@123` unless `DEMO_USER_PASSWORD` is set) |
|---|---|---|
| `sentinel.localhost` | STAFFING (security and facility agency) | `admin.sentinel`, `payroll.sentinel`, `hr.sentinel`, `guard.sentinel` |
| `nimbus-it.localhost` | CORPORATE (IT company) | `admin.nimbus`, `payroll.nimbus`, `staff.nimbus` |

People invited from Settings → Users get the same demo password, because the local profile sets `alliedpayroll.identity.keycloak.initial-password` in place of the set-up email (there is no mail server locally). They sign in with their email address. Outside `local` the property is unset and Keycloak emails a link to verify the address and choose a password.

The backend reaches Keycloak's Admin API as the confidential client `alliedpayroll-backend` (secret `dev-backend-secret` locally, `KEYCLOAK_BACKEND_SECRET` elsewhere). Its service account holds only `manage-users`, `view-users`, `query-users` and the three organisation roles, and the client has the `kc-admin-api` scope, without which Keycloak 26.8 refuses its tokens on the Admin API.

A user belongs to exactly one organisation. Signing in to `nimbus-it.localhost` with a Sentinel user fails at Keycloak, and a Sentinel token on the Nimbus host gets `403` from the API; both are covered by tests.

Keycloak admin console: http://auth.localhost:8081 with `admin` / `admin`.
