---
name: secure-coding
description: Use whenever code touches authentication, authorization, tenant or data-scope checks, PII (Aadhaar, PAN, bank, UAN, face templates), file upload/download, Excel import, external providers, secrets, logging, or any endpoint reachable from mobile, kiosk, devices or partners. Applies the OWASP ASVS L2 / API Top 10 baseline, DPDP Act obligations and the project's PII encryption and masking rules across Spring Boot, React and Flutter.
---

# Secure coding

Baseline: **OWASP ASVS Level 2** and the **OWASP API Security Top 10**, plus India's **DPDP Act 2023** and the Aadhaar Act. The rules below are the ones that come up in this codebase every week. `tenant-aware-entity` covers the database side; this skill covers everything around it.

## 1. Authentication and sessions

- Web: OIDC Authorization Code + PKCE through the **`web-channel` token-handler endpoints** (`/auth/*`). Access tokens never reach the browser; the session cookie is `HttpOnly; Secure; SameSite=Lax`, rotated on login, invalidated on logout. CSRF token on state-changing calls from the browser session.
- Mobile: PKCE via system browser or OTP; refresh token in Keystore/Keychain only; rotating refresh tokens; device binding; `local_auth` unlocks the token, it never proves identity for attendance.
- Kiosk: device credential scoped `kiosk:punch` for one work location; revocable; no employee session.
- OTP: 6 digits, 5-minute expiry, 5 attempts then lockout with backoff, rate-limited per phone and per IP; never logged; DLT-registered template.
- Passwords (admin/SSO fallback only): Keycloak policies; no custom hashing anywhere in our code.
- Certificate pinning in the Flutter app; no secrets in the app bundle (use bootstrap for config).

## 2. Authorization — every endpoint, every time

- **Tenant** comes from the backend's tenant filter (subdomain, token claim, device binding). A tenant id in a path, query or body is validated against the context and otherwise rejected. Never trust it from the client.
- **Permission** check via `@RequiresPermission("module:action")` on every endpoint, including reads and counts. Missing annotation fails the ArchUnit rule.
- **Data scope** (legal entity, branch, client, site) is applied in the repository/query layer, not in the controller, so exports, counts and sub-resources can't leak. Object-level authorization: fetching `/employees/{id}` outside scope returns `404`, not `403` (don't confirm existence).
- **Channel policy**: the token's `channel` claim is checked for restricted actions (no payroll approval from kiosk; publishing may be web-only per tenant).
- Maker–checker: money-moving actions (payroll approve/publish, disbursement, invoice approve/cancel, write-off, rule override) go through the approval module; the maker cannot be the checker.
- Mass assignment: request DTOs are explicit records; never bind entities or accept `Map<String,Object>`.

## 3. PII and biometric data

| Data | Storage | Display | Lookup |
|---|---|---|---|
| Aadhaar | **Only in `verification.aadhaar_vault`**, encrypted under the tenant's Aadhaar data key (separate KMS key from the general PII key); other tables keep `aadhaar_last4` and `aadhaar_vault_id` | `XXXX XXXX 1234` always; reveal only through `AadhaarVault.reveal` with `employee:aadhaar_reveal` and a purpose, rate-limited and audited; never in exports | by vault id; duplicate check via the vault's `matches` |
| PAN, UAN, ESIC IP no., bank account | Envelope-encrypted (per-tenant data key in KMS); ciphertext + masked value + **blind index** (HMAC) | masked by default; reveal needs `pii:reveal`, is rate-limited and **audited** | blind index |
| Phone, email | Encrypted at rest; masked in lists | masked in lists, full in detail with permission | blind index |
| Face templates | Encrypted with tenant key; vendor template format versioned; raw enrolment image deleted after template creation unless policy says otherwise | never | — |
| Punch face crops | Object storage, 90-day retention, then purged by job | supervisor review only | — |
| Salary, payslips | Encrypted at rest (storage level); access by permission and scope | — | — |

- **Consent** (DPDP): recorded per employee, per purpose, with version, timestamp, language and channel, before face enrolment, location capture or KYC. Withdrawal triggers deletion of the dependent data and is audited.
- **Purpose limitation**: no analytics or ML on biometric data; face data is attendance-only.
- **Retention**: payroll and statutory records 8 years; punch images 90 days; candidate data of rejected applicants 6 months; configurable per tenant within legal bounds. Purge jobs are tested.
- Exports (Excel, PDF, API) carry masked PII unless the caller has `pii:reveal`, and every export is audited with its filter.
- **Logs, traces, events and webhooks never contain PII.** They carry ids. A log-scrubbing filter and a unit test (`LogsContainNoPiiTest` with regexes for PAN/Aadhaar/phone/IFSC) back this up.

## 4. Input handling

- Bean Validation / zod / Dart validators at every boundary; reject unknown fields on strict endpoints (sync, device ingest).
- Numbers that are money come as decimal strings; dates as ISO; reject anything else.
- **Excel/CSV import**: parse with POI in streaming mode; reject formulas (`=`, `+`, `-`, `@` prefixes are escaped on export and rejected on import), macros, external links; size and row limits per tenant; virus scan before parsing; the importing user's scope applies row by row.
- **File uploads**: presigned URLs only; content-type and magic-number check; size limits; AV scan before the file becomes visible; images re-encoded (strip EXIF, including GPS) before storage; stored under `{tenant}/{module}/{uuid}` — never the original filename as a key.
- **Downloads**: presigned, short-lived, scoped; no path parameters built from user input.
- SQL only through JPA/Criteria or named parameters; JSONB paths parameterised; no string concatenation into queries, `ORDER BY` columns from an allow-list.
- Formula language for salary components: **our sandboxed evaluator** with an allow-list of functions and variables, a step and time limit, no reflection, no SpEL/MVEL/JavaScript. Fuzz-tested.
- Server-driven forms: validation rules are evaluated server-side even though clients render them.

## 5. Outbound and integrations

- Every external call (KYC, penny-drop, IRP/GSP, SMS, face SDK server, banks) goes through a port with timeouts, retries with jitter, circuit breaker, and a provider-specific adapter. Provider credentials in the vault, rotated; never in `application.yml` or env files committed to git.
- Webhooks we send: HMAC-SHA256 signature over timestamp + body, replay window 5 minutes, ids only in payloads. Webhooks we receive (device push, GSP callbacks): verify signature or shared secret, IP allow-list where the vendor supports it, idempotent processing.
- SSRF: outbound URLs from tenant configuration (webhook targets, SSO metadata) are validated against a scheme/host policy and resolved to public IPs only.

## 6. Secrets, config and supply chain

- Secrets only from the vault/KMS via the platform's `SecretsProvider`; CI checks for committed secrets (gitleaks) on every PR.
- Dependencies pinned through the Gradle version catalog, `package-lock.json`, `pubspec.lock`; Dependabot/Renovate weekly; OWASP Dependency-Check and Trivy image scan in CI fail on high/critical.
- Docker images: distroless/Temurin JRE, non-root user, read-only filesystem, no shell in production images.
- Security headers from the backend (and repeated at the ingress): CSP (nonce-based, no `unsafe-inline`), HSTS, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`. CORS allow-list per tenant subdomain pattern.

## 7. Logging and audit

- Structured JSON logs with `tenantId`, `userId` (hashed for employees), `channel`, `traceId`; **no PII, no tokens, no request bodies**.
- Audit (not logs) is the record of who did what: every create/update/delete, every PII reveal, every export, every failed authorization, every login and OTP outcome. Append-only; retention per §3.
- Security events (lockout, repeated 403s, impossible travel for kiosks, device attestation failures) raise alerts through the platform's alerting, not just logs.

## 8. Client-side rules

- React: render untrusted strings as text (React escapes); `dangerouslySetInnerHTML` is banned by lint except in the one sanitised rich-text component. No tokens in `localStorage`. Masked PII is masked by the API, not by the UI.
- Flutter: `flutter_secure_storage` for anything secret; SQLCipher for Drift; screenshots disabled on payslip and KYC screens (`FLAG_SECURE`); root/jailbreak and mock-location signals sent with punches, policy decides; deep links validated.
- Kiosk: lock-task mode; no browser; remote wipe; face template cache wiped on de-provision; failed attestations disable punching until an admin re-enables.

## 9. Review checklist

- [ ] Endpoint has permission annotation, data-scope applied in the query, tenant never taken from the client
- [ ] New PII field: encrypted, masked, blind-indexed, consent purpose mapped, retention set, excluded from logs/events
- [ ] Input validated at the boundary; imports and uploads follow §4
- [ ] External call behind a port with timeout, retry, breaker; secret from the vault
- [ ] Audit record written for the action; no PII in logs
- [ ] Mobile/kiosk/device/partner mutation is idempotent and channel-policy checked
- [ ] Threat considered: what happens if the caller is another tenant, a scoped supervisor, a stolen kiosk, a replayed request?
