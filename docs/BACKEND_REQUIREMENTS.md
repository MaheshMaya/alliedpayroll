# Backend requirements: the headless API

Companion to [PROJECT_INITIATION.md](PROJECT_INITIATION.md) (domain and business scope) and [ADR 0003](adr/0003-single-api-backend-web-and-mobile-clients.md) (one backend, one web app, one mobile app). This document is the **requirements specification for the backend alone**. Where it and an older section of another document differ, this one wins for the backend.

**Status:** approved direction (2026-11-04). Open items are marked *TBD* and listed in §12.

---

## 1. Definition

The backend is a **headless API**. It is one Spring Boot application (`backend/`) that exposes the entire functionality of Allied Payroll as versioned, documented, channel-neutral HTTP APIs, and nothing else.

| The backend **is** | The backend **is not** |
|---|---|
| The only place business rules, calculations, statutory logic, validation, authorization and data live | A web server for pages: it renders **no HTML, no templates, no views, no redirects to screens** |
| A JSON API over HTTPS (`application/json`, `application/problem+json`) plus file artefacts it produces (PDF, XLSX, CSV, bank and statutory files) | A backend-for-frontend: it has **no screen-specific endpoints** and no knowledge of how a client lays out data |
| Contract-first: the committed OpenAPI document in `api/openapi/` is the product; the generated SDKs are how clients consume it | A place for client state: no server-side UI sessions beyond the auth token handler, no per-screen preferences other than the generic user-preferences resource |
| Deployed as `backend-api` (serves requests) and `backend-batch` (same image, runs jobs) | Several services: no gateway, no BFF, no microservices, no service-to-service calls |

Every consumer gets the same API: the React web app, the Flutter app in employee, manager and kiosk modes, biometric terminals, the client portal, partner systems and the platform operator console. A capability that a client cannot reach through the documented API does not exist.

---

## 2. Consumers and what they may expect

| Consumer | Authenticates as | Uses | Backend obligations |
|---|---|---|---|
| Web app (admin, HR, payroll, accounts, tablet supervisors, client portal) | User via OIDC + PKCE through the auth endpoints; browser holds an HttpOnly session cookie | Canonical resources, count/summary endpoints, jobs, exports | CSRF protection on the cookie session; `ETag`/`If-Match`; localized errors |
| Flutter app, employee and manager modes | User via OTP or OIDC + PKCE; device-bound refresh token | Canonical resources plus `/api/v1/mobile/*` bootstrap and sync | Compact payloads via `fields=`; delta sync with tombstones; idempotent batch punch sync; app-version policy |
| Flutter app, kiosk mode | Device credential bound to tenant and work location (`kiosk:punch` scope) | `/api/v1/mobile/*` sync only | No employee session; roster and (if enabled) encrypted face templates by delta sync; remote disable |
| Biometric / access-control terminals | Device serial plus HMAC or shared secret, IP allow-list where possible | `/ingest/{vendor}/*` | Vendor protocol adapters normalise to the standard punch command |
| Partner systems (ERP, accounting, HRMS, BI) | OAuth2 client credentials or scoped API key, per API product | `/partner/v1/*` and webhooks | Quotas, scopes, key rotation, signed webhooks, audit of every call |
| Platform operator console | Operator user with platform-level role | `/api/v1/platform/*` | Cross-tenant actions are explicit, audited and never reachable from tenant tokens |

All consumers share tenant resolution, identity, permissions, data scope, i18n, audit and rate limiting. The backend never trusts a tenant id, user id or channel supplied in a payload.

---

## 3. API requirements

### 3.1 Contract and versioning
1. **OpenAPI 3.1, contract first.** Every endpoint is specified and reviewed before implementation. The committed document in `api/openapi/` is generated from code in CI and diffed; an unreviewed change fails the build.
2. **Generated SDKs** (TypeScript for web, Dart for the Flutter app, a published partner SDK) are built in CI from the committed document. Hand-written clients are not supported.
3. **Versioning:** `/api/v1` for canonical resources; `/partner/v1` for public API products. Additive changes stay within a major version; breaking changes create `v2` and the previous version runs for at least 12 months with `Deprecation` and `Sunset` headers. Mobile clients are protected by the `min_supported_version` policy in bootstrap.
4. **Resources are canonical and channel-neutral**: `/employees`, `/sites`, `/attendance-sheets`, `/payroll-runs/{id}/paysheets`, `/invoices`. Industry terminology (guard, technician) never appears in paths or field names; it is applied by clients through terminology keys.

### 3.2 Request and response conventions
5. **Global context** on every list and summary endpoint: `legalEntityId`, `branchId`, `period` (year-month), `clientId`, `siteId` as query parameters, validated against the caller's data scope.
6. **List contract:** `page`/`size` for small masters, `cursor` for anything that can exceed 10k rows (employees, punches, paysheets, audit), `sort`, `q`, `filter[...]`, `fields=` projection, and a `summary` endpoint per listable resource that returns status counts for cards.
7. **Errors** are `application/problem+json` with a stable machine `code`, localized `title` and `detail` (from `Accept-Language`), `params`, `traceId` and field-level `errors[]`. Authorization failures on resources the caller cannot see return `404`, not `403`.
8. **Idempotency:** every mutating call from mobile, kiosk, devices and partners carries an `Idempotency-Key`; the backend stores the response for 24 to 72 hours and answers a replay with the stored result, or `422` if the body differs. Mandatory for punches, payments, imports and payroll runs.
9. **Concurrency:** `ETag` on every entity response; `If-Match` required on updates (optimistic locking via `@Version`); `412` on conflict. `If-None-Match` honoured on bootstrap and reference data.
10. **Money and dates:** amounts are decimal strings (`"18500.00"`) with explicit currency; business dates are ISO `date`, instants are `date-time` with offset. The backend never formats for display.
11. **Files:** uploads and downloads use **presigned object-storage URLs** issued by the backend after an authorization check. Files never stream through the application process. The backend produces PDFs (payslips, letters, invoices, registers), XLSX/CSV exports, bank transfer files and statutory files as stored artefacts referenced from job results.
12. **Long-running work** (payroll run, import, statutory file, report, bulk payslip) returns `202 Accepted` with a `job` resource: status, progress, result link, error file. Clients poll with backoff or receive push or webhook on completion.
13. **Server-driven forms:** custom fields, tenant-configurable required flags, onboarding forms and site-visit forms are exposed as **form metadata** (field key, type, label key, validation, options, visibility rules). The backend validates submissions against the same metadata.
14. **Bootstrap:** `GET /api/v1/bootstrap` (web) and `GET /api/v1/mobile/bootstrap` return tenant branding, enabled modules and flags, the caller's permissions and data scope, terminology and i18n bundle versions, channel settings (punch windows, geofence, face-recognition on or off) and the minimum app version. Cached by ETag.
15. **i18n:** `Accept-Language` on every call; message keys everywhere; `*_i18n` master-data names returned with locale fallback already applied; notifications in the recipient's locale; PDFs in English by default. See [LOCALIZATION_I18N.md](LOCALIZATION_I18N.md).
16. **Search:** one `/search` endpoint returns typed entity hits (employee, client, site, invoice, payroll run) within the caller's scope, for the command palette and mobile search.
17. **Preferences:** one generic `/me/preferences` resource stores per-user, per-channel settings as a typed JSON document. The backend does not interpret UI preferences.

### 3.3 Channel endpoints (the only non-canonical surface)
The channel modules exist for **protocol**, not for screens.

| Module | Endpoints | Allowed to do | Not allowed to do |
|---|---|---|---|
| `web-channel` | `/auth/login`, `/auth/callback`, `/auth/logout`, `/auth/session`, `/auth/csrf` | OIDC token-handler pattern: hold tokens server-side, issue the session cookie, refresh, CSRF | Serve pages, aggregate screens, hold UI state |
| `mobile-channel` | `/api/v1/mobile/bootstrap`, `/api/v1/mobile/sync/punches`, `/api/v1/mobile/sync/{dataset}`, `/api/v1/mobile/devices/*` | Bootstrap, delta sync with cursors and tombstones, batched idempotent punch sync with clock-offset check, device registration and attestation, app-version policy | Compute anything attendance or payroll related; it hands punches to the attendance module |
| `device-ingest` | `/ingest/{vendor}/*` | Vendor protocol adapters (ZKTeco/eSSL ADMS first, then Matrix COSEC) mapping device user ids to employees and emitting the standard punch command | Store punches itself |
| `partner-api` | `/partner/v1/*`, webhook delivery | API products with scopes and quotas, API keys and client credentials, webhook subscriptions, HMAC-signed delivery with retries, dead-letter and delivery log | Expose anything the canonical API does not |

Any proposal for a "screen endpoint" is answered with a canonical resource change, a `summary` endpoint or a `fields=` projection.

---

## 4. Functional requirements by module

Each module is a Spring Modulith application module owning its schema, its REST resources and its events. State machines are enforced by the aggregate, not by clients. Phase numbers refer to [DEVELOPMENT_PLAN.md](DEVELOPMENT_PLAN.md).

### 4.1 Tenant (Phase 0–1)
- Provision a tenant from an **industry profile template** (staffing, project/field-service, corporate): modules on or off, structure levels and labels, default masters, default strategies, terminology overrides, number series. Re-applying a newer template version is an explicit, previewed migration.
- Typed, versioned **tenant settings** by module (Appendix A of the initiation document), **feature flags**, **branding** (logo, colours for white-label), subscription plan and usage metering (Phase 10), **number series** per entity and financial year (`{PREFIX}{FY}/{MM}/{SEQ}`).
- Platform operator API for tenant lifecycle (create, suspend, resume, move to dedicated database, delete per retention).
- Events: `TenantProvisioned`, `TenantSettingsChanged`, `ModuleToggled`.

### 4.2 Identity (Phase 1)
- Users (admin, manager, client contact, employee, service account), **roles from a permission catalogue**, **data scopes** (legal entities, branches, clients, sites), Keycloak synchronisation, SSO (SAML/OIDC) for corporate tenants.
- **Mobile OTP login** (DLT-registered SMS), PKCE for office staff, device-bound rotating refresh tokens, "logout everywhere".
- **Device registry** for app installs and kiosks: platform, app version, push token, bound user or location, attestation result, remote disable; **kiosk provisioning** by one-time enrolment QR issuing a device credential bound to tenant and work location.
- **Channel policy**: which channels may perform which permissions (kiosk may only punch; payroll publish may be web-only per tenant).
- Partner API clients and keys with scopes and quotas.
- Events: `UserInvited`, `RoleChanged`, `DeviceRegistered`, `DeviceDisabled`.

### 4.3 Organization (Phase 2)
- Legal entities (CIN, PAN, TAN, PF code, ESIC code, PSARA licence), zones, branches, **work locations with state and holiday calendar**, GST registrations (per state, e-invoice flag, LUT), ESIC sub-codes, PT and LWF registrations, company bank accounts, departments, cost centres, pay groups, designations with short codes.
- **Location-rule resolution:** for any employee on any date, resolve the applicable state, PT and LWF jurisdiction, minimum-wage schedule, Shops & Establishments limits and holiday list from the work location ([INDUSTRY_PROFILES_AND_STRUCTURE.md](INDUSTRY_PROFILES_AND_STRUCTURE.md) §6).
- Lock-after-use masters (soft delete with referential checks).
- Events: `WorkLocationChanged`, `HolidayCalendarChanged`.

### 4.4 Customer and contract (Phase 3, module flag `CUSTOMERS`)
- Customer organizations, client sites (each a work location), client groups, posts/sub-units, contacts, reporting officers, CLRA licences with headcount caps, client GST details, client documents with expiry.
- **Contracts** with the status machine Draft → Active → Expiring → Expired | Renewed (cloned), **rate cards** (designation × wage break-up × bill rate × posts), shifts and duty hours, billing terms (service charge %, statutory reimbursement, GST treatment, RCM flag), pay period, employee contracts and mini contracts.
- **Deployments** (employee ↔ site ↔ post ↔ designation ↔ shift, effective-dated) validated against the CLRA cap and overlapping postings; relievers.
- Margin preview (bill versus wage) as a computed resource.
- Events: `ContractActivated`, `ContractExpiring`, `EmployeeDeployed`, `DeploymentEnded`.

### 4.5 Workforce (Phase 2, extended Phase 7)
- Employee master for employees and contractors (employment type, pay frequency weekly / bi-weekly / monthly), designation, work location, pay group, **statutory profile** (PF/ESIC/PT/LWF applicability, UAN, PF member number, ESIC IP number, PAN), bank accounts with verification status, family and nominees, KYC records, custom fields, documents, photos, face enrolment consent.
- **Effective-dated history** for salary structure, deployment, statutory profile and work location; every change is versioned and queryable "as of" a date.
- **Field-level PII encryption** with masked display and blind indexes for search; Aadhaar stored only in the verification module's encrypted vault (per-tenant key in the KMS) and referenced by id; reveal requires a permission and a purpose, and is audited.
- Candidate pipeline (Applied → KYC done → Bank verified → Letters acknowledged → Approved → Converted), joining fee with instalments, police verification and PSARA training records with expiry, warnings, termination with full-and-final hook, reactivation.
- Events: `EmployeeOnboarded`, `StatutoryProfileChanged`, `EmployeeTerminated`, `CandidateConverted`, `BankAccountVerified`.

### 4.6 Verification (Aadhaar vault Phase 2, rest Phase 7)
- Aadhaar data vault: the only store of Aadhaar numbers, one row per subject, encrypted under a dedicated per-tenant key; `store`, `reveal` (permission, purpose, rate limit, audit), `matches` and `delete` through one port; bulk reveal only for statutory files generated by batch.
- Provider-agnostic ports with adapters: Aadhaar e-KYC (licensed provider), bank penny-drop, PAN, GSTIN, face enrolment and server-side face match (`FaceEngine`).
- Verification log with provider, request, result, cost and retention; retries with backoff; manual fallback paths.

### 4.7 Attendance (Phase 4)
- **Punches** from every source (self app, face, QR, kiosk, supervisor, device, upload, manual) with geo, accuracy, face score, device and channel; duplicate, geofence, punch-window and clock-skew checks; results `ACCEPTED | FLAGGED(reason) | REJECTED(reason)`.
- **Attendance modes** as strategies chosen by configuration: `SHIFT_DUTY`, `DAILY_CHECKIN`, `TIMESHEET` (Phase 10).
- Attendance sheets per contract or pay group per month; entries per employee per date per shift; **work information** (duties, OT duties and hours, reliever, leave, LOP, additional duties); leave types, balances and policies; holidays from the work location calendar.
- State machine Draft → Submitted → Approved (locked) → Reopened (with permission, only while payroll is not approved). Corrections carry a reason and are audited.
- Face verification policy per tenant: on-device match with sampled server re-verification, consent record, template retention and withdrawal (DPDP).
- Bulk attendance import (date-wise and type-wise) through the import framework.
- Events: `PunchRecorded`, `PunchFlagged`, `AttendanceSheetApproved`, `WorkInformationApproved`, `AttendanceSheetReopened`.

### 4.8 Payroll (Phase 5, extended Phase 8)
- **Salary components** with category (`EARNING`, `DEDUCTION`, `BENEFIT`, `EMPLOYER_COST`) and flags (`is_statutory`, `taxable`, `part_of_pf_wages`, `part_of_esi_wages`, `part_of_bonus_wages`, `part_of_gross`, `prorate`, `rounding`); value sources `FIXED | FORMULA | RATE_CARD | INPUT | STATUTORY`.
- **Sandboxed formula language**: whitelisted functions and variables, dependency-graph ordering, no reflection, no SpEL or JavaScript. Formulas are validated on save and evaluated identically in preview and runs.
- Effective-dated **salary structures**; **pay bases** (`MONTHLY_FIXED` with configurable divisor, `PER_DUTY`, `DAILY`, `HOURLY`); `ProrationDivisor`, `OvertimeRule`, rounding per category or on total.
- **Payroll runs** per `PayrollGrouping` (client, site, pay group) per period: **frozen input snapshot** (approved work information, structure or rate card, statutory profile, active rule sets, loans and instalments, inventory deductions, holds, arrears), compute, paysheets and lines with basis and inputs, salary holds, maker–checker approval through the approval module, publish, payslip PDF, bank transfer files (format per bank), disbursement batches and payment status.
- State machine Draft (computed) → In approval → Approved → Published → Paid / Partially paid; corrections after approval only through **arrears or a revision run**.
- **Minimum-wage guard** flags or blocks paysheets below the applicable minimum wage.
- **Explainability:** every paysheet line exposes its basis, rule version and inputs through the API.
- Arrears engine (minimum-wage and VDA revisions, back-dated structure changes) producing arrear groups; benefits (bonus monthly or annual, gratuity accrual as employer cost, leave encashment); revision runs.
- Runs are resumable Spring Batch jobs on `backend-batch`, partitioned by employee range, locked per (tenant, group, period), idempotent on re-run.
- Events: `PayrollComputed`, `PayrollApproved`, `PayrollPublished`, `SalaryPaid`, `ArrearGroupCreated`.

### 4.9 Compliance (Phase 5, extended Phase 8)
- **Effective-dated, jurisdiction-keyed rule sets** maintained at platform level and overridable per tenant: EPF (EPS cap, EDLI, admin charges, ceiling and actual-wage option, VPF), ESIC (contribution periods, thresholds, sub-codes), Professional Tax (state slabs and frequencies), LWF, minimum wages (state × zone × skill with VDA revisions), bonus, gratuity, leave encashment, TDS (regimes, declarations, proofs, previous employer, perquisites), CLRA, PSARA, Labour Code wage-definition add-back.
- Calculators exposed to the payroll module through the compliance `api` package; payroll never re-implements a statutory rule.
- Statutory outputs: ECR, ESIC monthly file, PT and LWF returns, 24Q and Form 16 (Income-tax Act 2025 schedule), challans and filing status, wage register, muster roll, combined muster roll, statutory registers.
- Rule packs are versioned data in `compliance.statutory_rule_set`, keyed by rule type, jurisdiction and variant, validated against a versioned JSON schema per rule type; a published version is never edited, a change is a new version that supersedes it. Drafted, simulated and published (maker–checker, `compliance:rule_pack_publish`) in the platform admin screen; the database is the source of truth and `rule-packs/{type}/{jurisdiction}/{effective_from}[.variant].json` holds the seeds and golden fixtures ([schema/compliance.md](schema/compliance.md) §6).
- Income tax: one `INCOME_TAX` pack per tax year per regime (`NEW`, `OLD`) holding slabs by age band, rebate and surcharge with marginal relief, cess, standard deduction, deduction and exemption limits keyed by a platform catalogue of deduction types (`tax_deduction_type`, with the legal section per Act so the 2025 Act renumbering is data); a pure calculator with explanation lines; monthly TDS as a cumulative projection so a mid-year amendment is a new pack version and the remaining months absorb it; regime comparison; employee tax profile per year (default new regime, opt-in old, locked after the tenant's cut-off), declarations with proof verification, previous-employer income, perquisites; year roll-over job on the first pack of a new year.
- Tenant overrides are limited to the JSON pointers the rule schema marks overridable (declaration cut-off, proof deadline, regime-switch policy, perquisites provided, PF-on-actual-wages, ESIC re-check policy); slabs, rates, ceilings and limits are never overridable.
- Events: `RulePackPublished`, `TaxDeclarationSubmitted`, `TaxDeclarationVerified`, `RegimeChanged`, `ChallanDue`, `ChallanPaid`, `ReturnGenerated`, `ReturnFiled`.

### 4.10 Billing (Phase 6, module flag `BILLING`)
- Invoices generated from approved attendance and rate cards (`MANPOWER_FROM_ATTENDANCE`), and later `WORK_ORDER`, `AMC_PERIODIC`, `TIME_AND_MATERIAL`; items, adjustments, service charge, statutory reimbursement.
- GST computation (CGST+SGST versus IGST by place of supply versus supplier GSTIN state, RCM, LUT, HSN/SAC per line), **e-invoice IRN** through a GSP adapter, e-way bill (Phase 10).
- Credit notes, receipts, write-offs, withholdings, payment status; invoice PDF with attendance register annex; **payroll-versus-invoice P&L** per site.
- State machine Draft → Approved → IRN generated → Sent; payment Pending → Partially paid → Paid.
- Events: `InvoiceApproved`, `EInvoiceGenerated`, `ReceiptRecorded`, `CreditNoteIssued`.

### 4.11 Approval (Phase 5)
- Flow definitions per resource type with levels and approver rules (role, data scope, amount thresholds); approval requests with Pending (level n) → Approved / Rejected / Returned; "my approvals" inbox; maker–checker enforced for payroll approval and publish, salary disbursement, invoice approval and cancellation, write-offs and statutory rule overrides.
- Events: `ApprovalRequested`, `ApprovalDecided`.

### 4.12 Documents (Phase 7)
- Document types with metadata flags and expiry rules; documents with polymorphic owners (employee, client, contract, legal entity); expiry alerts; letter templates and letters (generate → publish → acknowledge); PDF rendering behind a `PdfRenderer` port (OpenHTMLtoPDF, English by default); presigned storage with per-tenant prefixes and server-side encryption.
- Events: `DocumentExpiring`, `LetterPublished`, `LetterAcknowledged`.

### 4.13 Notification (Phase 9, plumbing from Phase 4)
- Email, SMS (DLT-registered templates), WhatsApp, push (FCM/APNs); per-tenant templates; recipient-locale rendering; payloads carry ids only; delivery log; in-app notification feed consumed by clients through the API; subscription and compliance banners.
- Events consumed from every module; emits `NotificationDelivered`, `NotificationFailed`.

### 4.14 Reporting (Phase 9, counts from Phase 1)
- Count and summary endpoints backed by maintained summary tables from Phase 1 (organization, client, employee active/inactive counts, invoice and paysheet totals).
- Read models fed by module events; async report jobs on `backend-batch` producing XLSX/PDF artefacts; saved filters and column sets; the standard reports in the initiation document Appendix B; queries on the read replica.

### 4.15 Audit (Phase 1)
- Immutable, append-only trail of every create, update and delete on tenant data: who, what, old and new values (PII masked), when, IP, channel, device, app version, trace id. Queryable through the API within data scope; retention at least 8 years for payroll and invoice snapshots.

### 4.16 Field ops and inventory-projects (Phases 9–10, module flags)
- Field ops: site-visit forms (server-driven), geo-tagged responses with photos, incidents, grievances, service requests.
- Inventory: items, warehouses and site stores, stock ledger, transfers, issue to employee (uniform recovery → payroll deduction) or to work order, tool custody, reorder levels, e-way bill. Purchase: vendors, PO, GRN, bills. Projects: work orders, AMC, timesheet attendance mode, job costing, Zoho Books and Tally connectors through the partner API.

---

## 5. Cross-cutting requirements

1. **Multi-tenancy.** Tenant resolved by the backend's tenant filter from subdomain, token claim or device binding, checked against the JWT; `tenant_id` on every tenant-owned table, Hibernate `@TenantId` discriminator, PostgreSQL row-level security with `SET app.tenant_id` per transaction, composite indexes starting with `tenant_id`, per-tenant object-storage prefixes; a routing `DataSource` so a tenant can move to a dedicated database without code change. Automated isolation tests at API, database and storage levels for every aggregate.
2. **Data scope.** Every query also applies the caller's allowed legal entities, branches, clients and sites. Enforced in the query layer, never in clients.
3. **Industry variation** only through module flags, strategies selected by configuration, structure labels and terminology keys; `IndustryProfile` is referenced only by the tenant module and the template loader (ArchUnit).
4. **Effective dating** as a first-class type (`EffectiveRange`) on salary structures, deployments, statutory profiles, work locations, rule sets, rate cards.
5. **Import and export framework:** template from field metadata including custom fields, row-level validation with an error file, Create and Update modes, asynchronous commit with progress, Excel formulas and macros rejected, localized headers with a canonical key row; large exports as jobs.
6. **Custom fields** per entity as typed metadata with validation; **configurable required flags** per field per tenant.
7. **Number series**, **lock-after-use masters**, **document expiry alerts**, **approval engine** and **letters** are platform features available to every module, not re-implemented per module.
8. **Domain events** are published through the Spring Modulith event publication registry in the same transaction as the change; listeners are asynchronous and idempotent; incomplete publications are resubmitted and alerted on. External streams (Kafka) only from Phase 9 behind `common-events`.
9. **Clock and ids are injected** so payroll snapshots are reproducible; all money is `BigDecimal` scale 2 stored as `NUMERIC(15,2)`.

---

## 6. Integrations (all behind ports and adapters)

| Integration | Direction | Requirement |
|---|---|---|
| Keycloak | Backend ↔ IdP | One realm; tenant as organisation claim; user and role sync; OTP flow; SSO federation for corporate tenants |
| KYC, penny-drop, PAN, GSTIN providers | Outbound | Adapter per provider, verification log, retries, manual fallback, provider redundancy |
| GST IRP / GSP | Outbound | IRN generation within the reporting time limit, cancellation, sandbox and production configs |
| Banks | Outbound files, later APIs | Salary file formats per bank as strategies; payout API adapters later |
| SMS / WhatsApp / email / push | Outbound | DLT template ids, recipient locale, delivery status, provider failover |
| Face-match provider | Outbound | `FaceEngine` port; enrolment, 1:1 and 1:N match, liveness result ingestion, template lifecycle |
| Biometric terminals | Inbound | Vendor adapters in `device-ingest`; device registry binding; standard punch command |
| Partner systems, Zoho Books, Tally | Both | Public API products and webhooks only; no private paths |
| Object storage | Outbound | S3-compatible, presigned URLs, per-tenant prefixes, SSE, AV scan on upload |

Every outbound call has timeouts, Resilience4j retry and circuit breaker, and is traced with the tenant and provider as attributes.

---

## 7. Security and privacy

- OWASP ASVS Level 2 and OWASP API Security Top 10 baseline ([secure-coding](../.claude/skills/secure-coding/SKILL.md)).
- Authentication per channel as in §2; tokens carry `tenant`, `sub`, `channel` and scopes; channel policy enforced server-side.
- Authorization on every endpoint: permission annotation plus data scope in the query layer; maker–checker for the money-moving actions in §4.11; `404` for out-of-scope resources.
- PII: field-level envelope encryption (KMS), masking in all lists, blind indexes for search, audited reveal, Aadhaar held only in the encrypted Aadhaar vault with a key reserved for it, face templates encrypted and deletable on consent withdrawal, 90-day retention for punch photos, DPDP consent and purpose records, retention schedule per record type, breach process.
- Input: Bean Validation at the boundary, size limits, upload AV scan and type sniffing, Excel formula rejection, sandboxed formula evaluator, SQL only through parameters.
- Secrets in a vault, never in images or config files; dependency and image scanning in CI; signed images.
- Logs are structured, correlated by trace id, and contain no PII.
- Rate limiting per tenant, channel and client in the backend (Redis-backed), with coarse limits at the ingress; burst allowance for kiosk sync at shift change.

---

## 8. Non-functional requirements

| Attribute | Requirement |
|---|---|
| Scale | 1,000 tenants; 500k employees; 50k employees in the largest tenant |
| Payroll run | 10,000 paysheets in under 5 minutes on `backend-batch`; resumable and safely repeatable |
| API latency | p95 < 300 ms for list and detail; p95 < 2 s for kiosk punch sync |
| Availability | 99.9% monthly for `backend-api`; RPO 15 min, RTO 4 h; zero-downtime deploys and online migrations |
| Month-end | Attendance locks, runs, bank files, invoices and payslip downloads for all tenants in the same 72 hours without degrading p95; per-tenant job fairness so one large tenant cannot starve others |
| Batch isolation | All work over ~2 s runs as a job on `backend-batch`; API pods never run batch steps |
| Observability | Micrometer metrics per endpoint, tenant tier, job and provider; OpenTelemetry traces on every request and job with `tenantId`, `channel`, `appVersion`, hashed `deviceId`; SLOs with error budgets per channel; nightly month-end load test against staging with a 50k-employee synthetic tenant |
| Data residency | All data and backups in an Indian cloud region |
| Audit and retention | 100% of tenant-data mutations audited; payroll and invoice snapshots kept 8 years; retention purges as scheduled jobs |
| Startup | `backend-api` ready in under 20 s; health, readiness and liveness endpoints |

---

## 9. Deployment and operations

- **Artefacts:** one container image for the backend, built by CI from `main`, deployed as `backend-api` (horizontally scaled, HPA) and `backend-batch` (profile `batch`, no ingress, scaled by queue depth); Helm charts in `deploy/`.
- **Environments:** `dev` (every merge), `staging` (nightly and on demand; load tests), `prod` (tagged calendar releases). Local development is `docker compose up` (PostgreSQL, Redis, Keycloak, MinIO) plus the application.
- **Configuration** through typed properties and environment; secrets from the vault; feature flags from the tenant module; no environment-specific code paths.
- **Database:** PostgreSQL 16+, one database, one schema per module, Flyway migrations per module, partitioned large tables (punches, attendance entries, paysheet lines, audit, event publications), read replica for reporting, backups meeting RPO/RTO, restore tested quarterly.
- **Runbooks** for payroll-run failure and resume, stuck event publications, provider outage fallback, tenant move to dedicated database, key rotation.

---

## 10. Engineering constraints

- Stack per [ADR 0001](adr/0001-spring-boot-4-java-25.md): Spring Boot 4.x on Java 25, Spring Modulith, Hibernate 7, Jackson 3, JSpecify null-safety, Spring Batch, springdoc.
- Conventions per [ADR 0002](adr/0002-coding-conventions.md) and the `solid-clean-code` skill: Google Java Style, Error Prone and NullAway, ArchUnit and Modulith verification as blocking CI gates.
- Module structure per the `new-backend-module` skill: `api` (published) · `web` · `application` · `domain` · `infrastructure`; a module imports only another module's `api`; channel modules contain no business rules.
- Tests per the `testing-strategy` skill: unit for the calculation core, `@ApplicationModuleTest` with Testcontainers per module, the nine mandatory slice tests, payroll **golden files** owned by the domain expert under `backend/modules/payroll/src/test/resources/golden/`, OpenAPI contract and breaking-change checks, nightly load test.
- Every slice follows the `engineering-workflow` definition of done; API changes are reviewed on the contract before code.

---

## 11. Explicit non-requirements

The backend will **not**:
- render HTML, server-side templates, emails as pages, or any UI;
- expose endpoints shaped for a particular screen or client;
- hold user interface state (open panels, column widths, drafts) beyond the generic preferences document;
- format money, dates or names for display, or translate industry terminology in responses;
- perform client-side concerns such as offline queue storage, face capture or camera handling;
- run as more than one application, call itself over the network, or depend on a message broker before Phase 9;
- support GraphQL in the initial releases (ADR 0004 to record).

---

## 12. Acceptance for the MVP (end of Phase 5) and open items

**Acceptance.** Through the API alone, with the generated SDKs and no manual database access:
1. Two reference tenants (a staffing agency with 3 sites in 2 states and 300 employees; an IT company with offices in 3 states) are provisioned from profiles, loaded by bulk import, and cannot read each other's data (isolation tests at API, RLS and storage level).
2. Attendance for a month is captured from app, kiosk, supervisor and upload sources, approved and locked.
3. A monthly payroll run for each tenant reconciles to the rupee against the domain expert's reference workbook, including PF, ESIC, PT and LWF resolved from work location; 10,000 paysheets compute in under 5 minutes; every line is explainable; a correction after approval is only possible through arrears or a revision run.
4. Payslip PDFs, bank transfer files, ECR, ESIC, PT and LWF files, wage register and muster roll are produced as job artefacts.
5. Every mutation is audited; every error is a localized problem; the OpenAPI document and SDKs are published with no breaking-change violations.

**Open items (TBD).** Pilot tenant and parallel-run window; face-SDK and kiosk PoC outcome; cloud provider and region; KYC, penny-drop, GSP and messaging providers; pricing model and the usage metering it needs; whether dedicated databases are offered at launch; GPS field tracking scope.
