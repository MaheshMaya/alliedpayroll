# Development plan: vertical slices, phase by phase

Companion to [PROJECT_INITIATION.md](PROJECT_INITIATION.md) and [BACKEND_REQUIREMENTS.md](BACKEND_REQUIREMENTS.md). Those documents say *what* we build and *why*. This one says *in what order* and *who builds what in each phase*.

**Status: proposal for approval.** Nothing in it has started. §1 lists the decisions it needs; decisions 1 (Spring Boot 4 / Java 25, [adr/0001-spring-boot-4-java-25.md](adr/0001-spring-boot-4-java-25.md)) and 2 (one API backend, one web app, one mobile app, [adr/0003-single-api-backend-web-and-mobile-clients.md](adr/0003-single-api-backend-web-and-mobile-clients.md)) were approved on 2026-11-03. Coding conventions are fixed by [adr/0002-coding-conventions.md](adr/0002-coding-conventions.md).

## How this plan works

Every phase is a **vertical slice**: one business capability, delivered across **all four layers at once** and demonstrable at the end of the phase.

| Layer | What each phase delivers |
|---|---|
| **Backend** (one Spring Boot application) | The backend modules for the slice, REST API with OpenAPI contract, schema migrations, events, tests, deployed to the dev cluster as `backend-api` and `backend-batch` |
| **Web** (React) | The screens for admins, payroll staff and (responsive) tablet supervisors |
| **Mobile and kiosk** (Flutter, one app) | The employee, supervisor and kiosk screens for the same capability |
| **Shared** | Generated SDKs, i18n bundles (en, hi, ta at least), Figma screens marked Ready for dev, dev annotations, test data |

A phase is not done until a product owner can sign in on web, phone and tablet and carry out the capability end to end. Backend work for the *next* phase can start early (§8), but a phase is never closed with "backend done, UI later".

Phases are sized in **two-week sprints**. The estimates assume the team in [TEAM_SKILLS_MATRIX.md](TEAM_SKILLS_MATRIX.md) §2 is in place by the end of Phase 1.

---

## 1. Decisions this plan needs

| # | Decision | Recommendation | Why it matters now |
|---|---|---|---|
| 1 | **Spring Boot line** | **Approved 2026-11-03 (ADR 0001):** **Spring Boot 4.x** (Spring Framework 7, Hibernate 7, Jakarta EE 11, Jackson 3, JSpecify) on **Java 25 LTS**. Pin the exact patch at kickoff. | Closed |
| 2 | **Deployables** | **Approved 2026-11-03 (ADR 0003):** **one API backend** (single Spring Boot application of Spring Modulith modules, deployed as `backend-api` plus a `backend-batch` instance of the same image), **one React web app**, **one Flutter app**. No gateway, BFF or microservices; Kafka deferred to Phase 9. | Closed |
| 3 | **Pilot tenant** | A **security/facility staffing agency** (as the initiation document proposes), with an IT tenant as the second reconciliation case | Decides which Figma screens and golden tests come first |
| 4 | **Face SDK and kiosk PoC timing** | Run the 4-week PoC in [MOBILE_AND_DEVICES.md](MOBILE_AND_DEVICES.md) §4 **during Phases 2–3**, so the result is in hand before Phase 4 (Attendance) starts | Phase 4 cannot ship face check-in without it |
| 5 | **Cloud and managed services** | Pick the provider and region (initiation document §11.4) before Phase 0 ends | Phase 0 builds the environments |
| 6 | **KYC, penny-drop, SMS/WhatsApp, e-invoice providers** | Choose by the end of Phase 5 | Needed in Phases 6 and 7 |

Front-end stack versions to pin at kickoff: React 19.x, TypeScript 5.x, Vite 8.x, TanStack Query/Router/Table, shadcn/ui on our tokens; Flutter 3.4x stable with Riverpod, go_router, Drift, dart-dio generated SDK. Verify the current versions on the day; do not take them from this document.

---

## 2. Phase map

| Phase | Slice | Sprints | Cumulative | Milestone |
|---|---|---|---|---|
| 0 | Walking skeleton | 2 | 4 wks | "Hello tenant" on web and phone from a clean checkout |
| 1 | Sign in and the app shell | 2 | 8 wks | A real tenant signs in everywhere, sees Home in their language |
| 2 | Company and people | 3 | 14 wks | Company set up, 300 employees imported, visible on phone |
| 3 | Clients, sites and contracts | 2 | 18 wks | Staffing structure live; kiosk provisioned at a site |
| 4 | Attendance | 4 | 26 wks | Face check-in, supervisor approvals, locked month |
| 5 | Payroll and statutory core | 5 | 36 wks | **MVP: a compliant monthly payroll, reconciled to the rupee** |
| 6 | Client billing | 3 | 42 wks | Invoice from attendance with GST and IRN; site P&L |
| 7 | Digital onboarding and documents | 3 | 48 wks | Candidate → KYC → letters → employee, from the phone |
| 8 | Compliance depth | 3 | 54 wks | Arrears, bonus, gratuity, TDS, returns and registers |
| 9 | Operations and ecosystem | 3 | 60 wks | Notifications, field ops, device adapters, partner API, client portal |
| 10 | Inventory, purchase and projects | 4 | 68 wks | Field-service profile complete; Zoho/Tally; subscription billing |

**Pilot go-live** is at the end of Phase 5, with a parallel run against the incumbent system through Phase 6. Phases 6–10 can reorder once the pilot tells us what hurts most.

How this maps to the initiation roadmap: Phases 0–5 are its "Phase 0 + Phase 1 MVP"; Phases 6–8 are its "Phase 2"; Phases 9–10 are its "Phase 3". The mobile app arrives in Phase 1 here instead of Phase 2 there, because every slice has to be demonstrable on the phone.

---

## 3. Definition of done for every slice

**The unit of delivery is one functionality, not one phase (decided 2026-11-04).** A phase is a set of functionality slices. Each slice is the backend API and the web screen for one capability a user can name, built and verified together, and it is not done while only the backend is merged or the screen runs against mocks. Mobile and kiosk screens belong to a slice only where the phase gives that functionality one. Every slice is proven end to end through the **real tenant login**: the Playwright flow signs in through `/auth/login` (OIDC Authorization Code + PKCE via `web-channel`) as a seeded tenant user on the tenant subdomain; there is no test bypass of authentication. The recipe is the `vertical-slice` skill.

Applied to each slice and, in aggregate, to each phase, no exceptions:

**Backend**
- OpenAPI 3.1 contract reviewed *before* implementation; SDKs regenerated (TypeScript, Dart, Java).
- Every tenant-owned table has `tenant_id`, Hibernate `@TenantId`, RLS policy and a composite index starting with `tenant_id` (`tenant-aware-entity` skill).
- Tenant-isolation tests pass at API, database and file-storage level.
- Audit records for every create/update/delete; domain events (Modulith event publication registry) for every state change listed in the phase.
- Problem+json errors with message keys; `Accept-Language` honoured.
- Spring Modulith verification and ArchUnit rules pass (module boundaries, hexagonal layers, no business logic in channel modules).
- Deployed to `dev` by CI; Testcontainers suite green.

**Web**
- Built from the Figma screens for the slice, using the design-system components and tokens only.
- Desktop (1440) and tablet (1024) layouts; keyboard shortcuts from the App Shell; `ui-review` run and no Blocker or Major left.
- Storybook stories for new components; Playwright flow test for the slice's main task, signed in through the real login as a seeded tenant user; `403`, data-scope and tenant-isolation tests for every new endpoint.

**Mobile and kiosk**
- Employee, supervisor and kiosk screens as the slice defines; works on the ₹8–12k Android baseline phone.
- Offline behaviour defined (works offline, read-only cached, or needs connection) and tested.
- Golden tests for key screens; integration test on the device farm.

**Shared**
- Strings in en-IN, hi and ta via message keys (`i18n-text` skill); pseudo-locale build passes with no clipping.
- Usability check of the slice's main task with 3 users of the right persona (full rounds per [design/USABILITY_TEST_PLAN.md](design/USABILITY_TEST_PLAN.md) at Phases 1, 5 and 7).
- ADR written for any decision that changes the architecture.
- Demo recorded; release notes written for the pilot customer.

---

## 4. Phases in detail

### Phase 0: Walking skeleton (2 sprints)

**Goal.** From a clean checkout, one command runs the whole stack locally; CI deploys it; a seeded tenant renders "Home" on web and phone. No business features.

| Layer | Deliverables |
|---|---|
| Backend | Gradle (Kotlin DSL) monorepo with `build-logic` convention plugins and version catalog. **One Spring Boot application** `backend/app` assembled from `backend/platform/*` libraries (`common-core`; `common-tenancy` with TenantContext, the tenant filter (subdomain → tenant, JWT claim check, channel tag), Hibernate resolver and RLS hook; `common-security`; `common-web` with list contract, problem+json, idempotency, ETag, rate limiting; `common-events` with the Modulith event publication registry as outbox and idempotent listeners; `common-test`) and the first modules: `tenant` (provision tenant, `GET /bootstrap`), `web-channel` (token handler `/auth/*`, session cookie, CSRF), `mobile-channel` (bootstrap only). Modulith verification and ArchUnit in CI. Keycloak realm with organisation claim. Flyway with one schema per module, OpenTelemetry, structured logs. The same image deployed as `backend-api` and `backend-batch` (profile `batch`, no ingress). `docker-compose` for local; Helm charts and GitHub Actions deploying to `dev`. |
| Web | `frontend` scaffold: React, TS, Vite, TanStack, shadcn/ui. **Style Dictionary pipeline** from `design-tokens/tokens.json` → CSS variables + Tailwind preset. Storybook with the first 10 components (Button, Text Field, Badge, KPI Tile, Side Nav, Utility Rail…). App Shell v2 layout (left nav, utility rail, top context bar) with placeholder content. Pseudo-locale build. |
| Mobile | `mobile` scaffold: Flutter, Riverpod, go_router, Drift (encrypted), generated dart-dio SDK, flavors dev/staging/prod, Fastlane, Crashlytics. **Flutter `ThemeExtension`** generated from the same tokens. Bootstrap call and a placeholder Home. Kiosk mode entry point behind a provisioning stub. *Done 2026-11-22 except Drift (arrives with the offline punch queue in Phase 4) and Fastlane and Crashlytics, deferred to the first release slice because they need Play Console, App Store Connect and Firebase accounts.* |
| Shared | OpenAPI → SDK generation in CI. ADR 0001 (Boot 4 / Java 25), ADR 0002 (coding conventions) and ADR 0003 (one backend, one web, one mobile) are written; ADR 0004 (no GraphQL) to write. Toolchain from ADR 0002 wired into pre-commit (lefthook) and CI: Spotless, Checkstyle, Error Prone/NullAway, ESLint/Prettier, `dart analyze`, Spectral, SQLFluff, commitlint. CLAUDE.md updated with build, lint and test commands. |

**Exit.** `make up` (or Gradle task) starts everything; CI green; two seeded tenants cannot read each other's `/bootstrap`; tokens render identically in Storybook and the Flutter theme gallery.

---

### Phase 1: Sign in and the app shell (2 sprints)

**Goal.** A real tenant admin, a supervisor and an employee each sign in on the channel they use, in their own language, and land on a Home that shows live counts (zeros are fine).

| Layer | Deliverables |
|---|---|
| Backend | `identity` module: users, roles from the permission catalogue, data scopes (legal entity, branch, client), Keycloak sync, **mobile OTP login**, device registry, "can't sign in" help request. `tenant` module: industry-profile templates, typed settings, feature flags, terminology overrides, number series, branding (logo, colours). Bootstrap returns branding, modules, permissions, terminology bundle version, min app version. `audit` module. Events: `TenantProvisioned`, `UserInvited`. |
| Web | Sign in with OTP, wrong-code state, Can't sign in (Figma `00 Sign in`). App Shell v2 for real: grouped Side Nav with module-flag visibility, Utility Rail with Quick actions and Notifications (empty), Shortcuts sheet, command palette (`Ctrl K`) over commands only for now, Global Context Bar wired to data scope. Home with KPI tiles reading count endpoints. Settings → Users and roles, Preferences (status bar on/off). Tenant white-label branding applied from bootstrap. |
| Mobile | OTP sign in, PKCE for office staff, device-bound refresh token, biometric unlock of the token. Mode selection by role (employee / supervisor). Employee Home (EN, TA) with placeholder tiles. Supervisor Home. Language switcher. Forced-upgrade screen. **Kiosk provisioning**: admin generates enrolment QR on web, tablet scans it, receives device credential bound to a work location, shows the idle screen. |
| Shared | Figma: Sign in, Home, Setup step 1, Settings → Users. First **usability round** (sign-in tasks P1, P2) on the real build. Translators engaged for hi/ta glossaries. |

**Exit.** Three personas sign in on three channels; permissions hide what they may not do; a second tenant's subdomain shows its own logo and colours; audit shows every sign-in and role change.

---

### Phase 2: Company and people (3 sprints)

**Goal.** The tenant sets up its company structure and loads its workforce. Employees see themselves on the phone.

| Layer | Deliverables |
|---|---|
| Backend | `organization` module: legal entities (PAN, TAN, CIN, PF code, ESIC code, PSARA), zones, branches, **work locations with state and holiday calendar**, GST/PT/LWF/ESIC registrations, company bank accounts, departments, pay groups, designations. `workforce` module: employee master, employment type, statutory profile (PF/ESIC/PT/LWF applicability, UAN, IP number), bank accounts, family and nominees, custom fields, effective-dated history, **field-level PII encryption** with masked display and blind index. **Bulk import framework** (`common-import`): template from metadata, validate, error file, commit as a job; first use for employees. Location-rule resolution (holidays, PT and LWF state) from work location. Events: `EmployeeOnboarded`, `StatutoryProfileChanged`. |
| Web | Setup wizard steps 2–4 and 6 (Legal entities, Work locations, Holidays, Branding). Employees list with filters, saved views, column chooser and masked PII; employee detail with tabs (Profile, Statutory, Bank, Documents placeholder, History); **bulk import flow** (Figma `03 People`: upload → validate → error file → use suggestion → commit). Designations and pay groups. Command palette now jumps to employees. |
| Mobile | Employee: My profile (masked), my work location and this month's holidays, bank details view with "report a problem". Supervisor: My team list (from data scope) with search; employee card. Kiosk: roster of the location cached (names and codes only). |
| Shared | Figma: Setup 2–4, 6; Employees; Import. Golden data set: the fictional *Sentinel Guard Services* tenant (3 sites, 2 states, 300 employees) and an IT tenant (3 state offices). **Face SDK / kiosk PoC starts** (see §1 decision 4). |

**Exit.** 300 employees imported from the template with 9 deliberate errors reported and fixed; Hyderabad and Chennai employees resolve to different PT, LWF and holiday rules; a supervisor sees only their branch.

---

### Phase 3: Clients, sites and contracts (2 sprints)

**Goal.** The staffing structure (Client → Site → Post → Shift) and the money rules behind it. Field-service and corporate tenants skip this module by flag.

| Layer | Deliverables |
|---|---|
| Backend | `customer-contract` module: customers, client sites (with work location), posts/sub-units, contacts and reporting officers, CLRA licences, client GST details. **Contracts** with status machine (Draft → Active → Expiring → Expired/Renewed), **rate cards** (designation × wage break-up × bill rate × posts), shifts, billing terms (service charge, RCM flag), employee deployments (effective-dated, validated against CLRA headcount). Document types with expiry. Events: `ContractActivated`, `ContractExpiring`, `EmployeeDeployed`. |
| Web | Clients list and detail; Sites with posts and shifts; Contract and rate card editor; margin preview (bill vs wage); deployments grid (who is posted where); expiring-contract alerts in Notifications (Figma `07 Clients & sites`). |
| Mobile | Supervisor: My sites, site detail (posts, shifts, deployed people, client contact tap-to-call). Employee: My posting (site, post, shift, reporting officer). Kiosk: bound to one site, shows site name and shift times on the idle screen. |
| Shared | Figma: Clients & sites, Contract rate card, Deployments. **PoC concludes**: face SDK, kiosk tablet, MDM and terminal vendors chosen; ADRs written. |

**Exit.** Marina Tech Park contract shows ₹3,09,600 monthly bill and 17.1% margin from its rate card (test task P5); a deployment over the CLRA cap is blocked with a clear message.

---

### Phase 4: Attendance (4 sprints)

**Goal.** Every punch source feeds one attendance pipeline; supervisors fix gaps; the month locks for payroll.

| Layer | Deliverables |
|---|---|
| Backend | **`attendance` module**: punches (source app/face/QR/kiosk/supervisor/upload/manual, geo, face score), attendance sheets per contract or pay group per month, entries, work information (duties, OT, reliever, leave, LOP), additional duties, leave types and balances, approval and **lock** (Draft → Submitted → Approved → Reopened with permission). `AttendanceMode` strategies: `SHIFT_DUTY` and `DAILY_CHECKIN` now, `TIMESHEET` in Phase 10. **`mobile-channel`**: batched idempotent `POST /api/v1/mobile/sync/punches`, device clock-offset check, delta sync for roster/shifts/holidays, offline limits per tenant. Face verification: `FaceEngine` port, chosen SDK adapter, server re-verification (sampled), consent record (DPDP). Bulk attendance import. Events: `PunchRecorded`, `WorkInformationApproved`. |
| Web | Attendance grid, date-wise and monthly, with legend and keyboard navigation; corrections with reason; approvals inbox (Figma `04 Time & attendance`); leave and holiday management; flagged-punch review (clock skew, face mismatch, geofence). Tablet layout for supervisors. Background-jobs panel shows imports. |
| Mobile | **Employee check-in**: face 1:1 with liveness, geofence, offline queue with "Saved offline" state, punch history. **Supervisor**: team shift view (not checked in first), mark on behalf with photo, **assign reliever** sheet, approve corrections. **Kiosk**: 1:N face or QR/PIN fallback, offline queue, result screens with audio, auto-return, remote disable. Push notification plumbing (FCM) for "you haven't checked in". |
| Shared | Figma: Attendance, Supervisor, Check-in, Kiosk (all exist). Device-farm tests on the low-end phone and kiosk tablet. **Second usability round** (G1–G4, S1–S2). |

**Exit.** 500 offline punches sync with no duplicates; a spoof photo is rejected; the October sheet for a site locks and cannot be edited without the reopen permission; the PoC pass criteria hold on real sites for two weeks.

---

### Phase 5: Payroll and statutory core (5 sprints) · MVP

**Goal.** A compliant monthly payroll for the staffing pilot and the IT reference tenant, reconciled to the rupee, published to employees' phones.

| Layer | Deliverables |
|---|---|
| Backend | **`payroll` module**: salary components (category, statutory flags, wage-base flags, proration, rounding), sandboxed formula language with dependency ordering, effective-dated salary structures, `PayBasis` (per duty, monthly, daily, hourly), `ProrationDivisor`, `OvertimeRule`, payroll runs per `PayrollGrouping`, **frozen input snapshots**, paysheets and lines, salary holds, approval via the generic approval module (maker–checker), publish, payslip PDF (English, OpenHTMLtoPDF), bank transfer file (first two bank formats), per-(tenant, group, month) lock, resumable Spring Batch job on `backend-batch`. **`compliance` module**: the **rule-pack registry** ([schema/compliance.md](schema/compliance.md): `statutory_rule_set` with JSON-schema validation per rule type, maker–checker publish, supersede-never-edit, tenant overrides limited to the schema's overridable keys, seeds from `rule-packs/`, `RulePackPublished`), with packs for PF (incl. EPS cap and EDLI), ESIC (contribution periods), PT (state slabs, TN half-yearly), LWF, minimum wages (state × zone × skill) with **minimum-wage guard**; challans and returns tables; ECR, ESIC monthly, PT and LWF files; wage register and muster roll. **Explainability API**: every paysheet line returns its basis, rule and inputs. Events: `PayrollApproved`, `PayrollPublished`, `SalaryPaid`. |
| Web | Components and structures setup (Setup step 5); **Payroll runs** list and run detail; paysheet table with explain drawer ("why does ESIC apply?"); approvals; **Month-close cockpit** (sites ready / blocked, run payroll unlocks at 100%); payslip preview; bank file and statutory file downloads; holds and corrections (Figma `02 Month close`, `05 Payroll`); **Statutory rule packs** admin screen for platform roles: list by type, year and jurisdiction, new version, form editor from the rule schema, diff, simulate against sample profiles, submit and publish. Quick actions: Run payroll, Create bank transfer file. |
| Mobile | Employee: **Payslips** (list, detail with explain, PDF view, cached after first view), salary hold notice. Supervisor: approve attendance → see "payroll ready" status for my sites. Manager (tablet): approve payroll if in role. Kiosk: unchanged. |
| Shared | **Golden-file test suite** owned by the domain expert: per state and scenario, inputs → expected lines. Reference workbook for the pilot tenant. **Third usability round** (P3–P7). Compliance consultant signs off the rule tables. Parallel run with the incumbent system begins. |

**Exit (MVP, same as initiation document §8).** Both reference tenants reconcile to the rupee including PF, ESIC, PT and LWF; payslips in English PDF; UI in en/hi/ta; isolation tests pass; 10,000 paysheets compute in under 5 minutes; a correction after approval goes through arrears or a revision run, never an edit.

---

### Phase 6: Client billing (3 sprints)

**Goal.** Close the hire-to-cash loop for staffing tenants.

| Layer | Deliverables |
|---|---|
| Backend | **`billing` module**: invoices generated from approved attendance and rate cards (`MANPOWER_FROM_ATTENDANCE`), items and adjustments, GST (CGST/SGST vs IGST by place of supply, **RCM**, LUT), **e-invoice IRN** via GSP adapter, credit notes, receipts, write-offs, withholdings, payment status, invoice PDF with attendance register annex, **payroll-vs-invoice P&L** per site. Events: `InvoiceApproved`, `EInvoiceGenerated`, `ReceiptRecorded`. |
| Web | Invoices list and detail, generate from month close, approve and send, receipts and reconciliation, site P&L view, GST summary export. Quick action: Generate client invoice. |
| Mobile | Supervisor: site P&L card (if permitted). **Client portal (web, responsive, read-only)**: deployments, attendance register, invoices, challans for the client's sites. |
| Shared | Figma: Invoices, Receipts, Client portal. IRP sandbox tests. |

**Exit.** October invoice for Marina Tech Park matches the rate card and approved attendance; IRN generated in the sandbox; RCM invoice shows no GST charged with the right note.

---

### Phase 7: Digital onboarding and documents (3 sprints)

**Goal.** Hire from the phone: candidate to active employee with verified identity, bank and letters.

| Layer | Deliverables |
|---|---|
| Backend | `workforce`: candidate pipeline (Applied → KYC → Bank verified → Letters acknowledged → Approved → Converted), joining fee with instalments, police verification and PSARA tracking, warnings, termination with full-and-final hook. **`verification` adapters**: Aadhaar e-KYC (licensed provider), penny-drop, PAN, GSTIN; verification log. **Documents module**: document types and expiry alerts, letter templates, letters (generate → publish → acknowledge), presigned storage, server-driven onboarding forms. Face enrolment with consent. Events: `CandidateConverted`, `DocumentExpiring`, `EmployeeTerminated`. |
| Web | Onboarding pipeline board, candidate detail with KYC results, letter templates editor, document expiry dashboard, police verification and PSARA registers, termination flow. |
| Mobile | **Employee self-onboarding**: OTP, Aadhaar e-KYC, selfie enrolment with liveness and consent in own language, bank details with penny-drop result, read and acknowledge letters, upload documents. Supervisor: start onboarding for a walk-in candidate. |
| Shared | Figma: Onboarding, Letters, Documents. DPDP consent text reviewed by counsel. **Fourth usability round** (onboarding on low-literacy users). |

**Exit.** A candidate completes KYC and letters on a ₹10k phone in under 10 minutes; expired police verification raises an alert 30 days ahead; consent withdrawal deletes the face template.

---

### Phase 8: Compliance depth (3 sprints)

**Goal.** The parts of statutory compliance that bite after the first few months.

| Layer | Deliverables |
|---|---|
| Backend | `payroll`: **arrears engine** (minimum-wage/VDA revisions, back-dated structure changes) producing arrear groups; benefits (bonus monthly or annual, gratuity accrual as employer cost, leave encashment); revision runs. `compliance`: **TDS** per [schema/compliance.md](schema/compliance.md) §4 to §6: `INCOME_TAX` packs per tax year and regime, `tax_deduction_type` catalogue, pure `IncomeTaxCalculator` with explanation lines, cumulative monthly projection, regime comparison, employee tax profile and declarations with proof verification, previous-employer income, perquisite packs, year roll-over job; 24Q and Form 16 under the Income-tax Act 2025 schedule, challans and return filing status, statutory registers, Labour Code wage-definition add-back. |
| Web | Arrears review and approval, benefit groups, tax profiles and declarations review with proof verification, regime comparison, income-tax packs in the rule-pack admin screen with simulate, challan tracker with due dates in Notifications, returns export, registers. |
| Mobile | Employee: **regime choice with comparison, tax declaration and proof upload**, monthly TDS explanation on the payslip, Form 16 download, bonus and gratuity statements. |
| Shared | Golden tests extended per scenario. Compliance consultant second sign-off. |

**Exit.** An April VDA revision notified in June produces correct arrears for April–May with PF and ESIC recomputed; a Form 16 reconciles to the 24Q quarters.

---

### Phase 9: Operations and ecosystem (3 sprints)

**Goal.** Everything that makes the product feel finished for daily operations and plugs it into what tenants already run.

| Layer | Deliverables |
|---|---|
| Backend | `notification` module: email, SMS (DLT templates), WhatsApp, push; per-tenant templates; recipient-locale. **Field ops**: site-visit forms (server-driven), incidents, grievances, service requests. **`device-ingest`** channel module: ZKTeco/eSSL ADMS adapter, then Matrix COSEC. **`partner-api`** channel module: Partner API v1 (employees, attendance, payroll outputs, billing; read, then write scopes) and **webhooks** with HMAC signing and delivery log. `reporting` module: read models fed by module events, async report jobs on `backend-batch`, queries on the read replica, Excel export, the standard reports in the initiation document Appendix B. **Kafka decision point** (ADR 0003): introduce it behind `common-events` only if device ingest volume or partner event feeds need a stream. |
| Web | Notification settings, dashboards (home, client, employee counts; invoice and paysheet totals), custom report builder (saved filters and columns), webhook and API-key management, incident log. |
| Mobile | Supervisor: **site visit** with geo-tagged form and photos, incident report. Employee: notifications centre, WhatsApp payslip link. Kiosk: unchanged. |
| Shared | Figma: Dashboards, Reports, Field ops. Public API documentation portal. |

**Exit.** A ZKTeco terminal at an IT office pushes punches that appear in the sheet within 60 s; a webhook fires on `payroll.published` with a signed payload; a supervisor's site visit shows on the client portal.

---

### Phase 10: Inventory, purchase and projects (4 sprints)

**Goal.** Complete the project/field-service profile and the commercial plumbing.

| Layer | Deliverables |
|---|---|
| Backend | **Inventory module** (one module, profile defaults): items, warehouses and site stores, stock ledger, transfers, issue to employee (uniform recovery → payroll deduction) or to work order, tool custody, reorder levels, e-way bill. **Purchase**: vendors, PO, GRN, bills. **Projects**: work orders, AMC, `TIMESHEET` attendance mode, job costing (`PROJECT_JOB_COSTING`), `WORK_ORDER` / `AMC_PERIODIC` / `TIME_AND_MATERIAL` billing modes. Zoho Books and Tally connectors on the public API. **Subscription billing and usage metering** in `tenant`. Optional consented field tracking, if approved. |
| Web | Stock and issue screens, PO and GRN flow, work orders and timesheets, job-cost view, subscription and plan management, platform operator console. |
| Mobile | Technician: **work-order timesheet** with check-in/out at site, material issue and return, service report. Supervisor: approve material issue. Employee: asset custody acknowledgement. |
| Shared | Figma: Inventory, Purchase, Work orders. Boiler-engineering reference tenant golden tests. |

**Exit.** A boiler-engineering tenant issues spares to a work order, bills it as time-and-material and sees job cost against invoice; uniform issue to a guard appears as a payroll deduction next month.

---

## 5. Deployables and modules

Per ADR 0003 there are four deployables for the whole product; everything below is a module inside the one backend image.

| Deployable | Appears in | Holds |
|---|---|---|
| `backend-api` | Phase 0 | The single Spring Boot application: all modules below, serving `/api/v1/...` for every channel |
| `backend-batch` | Phase 0 (first real jobs in Phase 2) | The same image with the `batch` profile: payroll runs, imports, statutory files, reports, scheduled jobs |
| `web` | Phase 0 | The React SPA (static hosting) |
| `mobile` | Phase 0 | The Flutter app (Play Store / App Store; MDM for kiosks) |

| Module (Spring Modulith) | Phase | Kind |
|---|---|---|
| `tenant`, `identity`, `audit`, `web-channel`, `mobile-channel` | 0–1 | domain / channel |
| `organization`, `workforce`, `common-import` (library) | 2 | domain |
| `customer-contract` | 3 | domain |
| `attendance` (with the `FaceEngine` port) | 4 | domain |
| `payroll`, `compliance`, `approval` | 5 | domain |
| `billing` | 6 | domain |
| `verification`, `documents` | 7 | domain |
| `notification`, `field-ops`, `reporting`, `device-ingest`, `partner-api` | 9 | domain / channel |
| `inventory-projects` | 10 | domain |

A module becomes its own deployable only through an ADR. The API paths and event types would not change if that happened, because every module already owns its schema and talks to the others only through its published `api` package and events.

---

## 6. Figma coverage

Screens already designed and tested (see [design/figma-state.json](design/figma-state.json)) land in these phases:

| Figma page | Phase |
|---|---|
| 00 Sign in | 1 |
| 01 Home, App Shell v2 | 1 |
| 06 Setup (7 steps) | 1 (step 1), 2 (steps 2–4, 6), 5 (step 5), 7 (step 7 invite team) |
| 03 People (Employees, Import) | 2 |
| 07 Clients & sites | 3 |
| 04 Time & attendance; Mobile check-in, Supervisor, Kiosk | 4 |
| 02 Month close, 05 Payroll; Mobile payslip | 5 |

Screens still to design, two phases ahead of their build: Invoices and Client portal (design in Phase 4), Onboarding and Letters (Phase 5), TDS and Arrears (Phase 6), Dashboards and Field ops (Phase 7), Inventory and Work orders (Phase 8).

---

## 7. Team allocation by phase

| Role | P0–P1 | P2–P3 | P4 | P5 | P6–P8 | P9–P10 |
|---|---|---|---|---|---|---|
| Architect / tech lead | Platform libraries, module boundaries, tenancy | Reviews, event design | Sync protocol | Engine design, locks | Billing and GST design | Public API, read models |
| Senior backend (2) | tenant and identity modules, Keycloak, bootstrap | organization, workforce, import | attendance, mobile sync endpoints | **payroll engine, compliance** | billing, arrears, TDS | reporting, device-ingest |
| Backend (3–4) | CI, observability, audit | contracts, rate cards, PII | face port, leave, flags | files, PDFs, bank formats | verification, documents, notifications | inventory, projects, connectors |
| Front end (2) | tokens pipeline, shell, Storybook | setup wizard, employees, import | attendance grid, approvals | month close, payroll, explain | invoices, onboarding, TDS | dashboards, reports, inventory |
| Mobile (1–2) | app scaffold, login, kiosk provisioning | profile, team, sites; **PoC** | **check-in, supervisor, kiosk** | payslips | self-onboarding, declarations | site visits, timesheets |
| QA / SDET (2) | isolation tests, pipelines | import and data tests | device farm, offline tests | **golden tests** with domain expert | GST and IRP tests | API contract tests |
| Domain expert | glossary, rule tables draft | reference tenants, workbook | attendance rules | **rule tables, golden cases, sign-off** | TDS, bonus, gratuity cases | — |
| Designer | shell, sign in, setup | employees, clients, invoices | onboarding, letters | TDS, arrears, dashboards | field ops, inventory | work orders |
| Product owner | pilot agreement, backlog | usability round 1 | round 2 | round 3, parallel run | round 4 | pricing, plans |

---

## 8. Working ahead without breaking the slice rule

- **Design** runs two phases ahead (§6). Screens pass usability testing before engineers pick them up.
- **Backend contracts** (OpenAPI) for phase *n+1* are written and reviewed in the last sprint of phase *n*, so front-end and mobile can start against mocks (Prism or the generated SDK's mock server) on day one.
- **Domain tables** (statutory rule sets, golden cases) are prepared by the domain expert from Phase 2, long before Phase 5 consumes them.
- The payroll engine's **pure calculation core** (formulas, strategies, rounding, rule evaluation) is a plain Java library with no Spring or database dependency. It can be built and golden-tested from Phase 3 by one senior engineer while the team finishes attendance.

---

## 9. Risks specific to this plan

| Risk | Mitigation |
|---|---|
| Face SDK PoC slips and blocks Phase 4 | Phase 4 ships QR/PIN kiosk and supervisor marking first; face check-in is the last sprint of the phase and can slide into Phase 5 without blocking payroll |
| Payroll phase is the longest and the least parallel | Pure engine built early (§8); two seniors paired on it; golden tests written before code |
| Team not fully staffed by Phase 2 | Phases 0–1 are sized for 5–6 people; Phase 2 is the first that needs the full team |
| Spring Boot 4 / Hibernate 7 surprises | Phase 0 proves the multi-tenancy and RLS approach on the real versions before any domain code exists |
| The single backend degrades into a tangle | Modulith verification and ArchUnit are blocking CI gates from Phase 0; modules expose only their `api` package; a cross-module shortcut needs an ADR, not a PR comment |
| Scope pressure from the pilot after Phase 5 | Phases 6–10 are explicitly reorderable; each is a self-contained slice |
| Tamil and Hindi quality | Native review is part of every slice's DoD, not a final-phase task |

---

## 10. First two weeks

If this plan is approved:

1. Record decisions 3–5 from §1 as ADRs (decision 1 is ADR 0001, conventions ADR 0002, decision 2 ADR 0003).
2. Create the monorepo skeleton and CI (Phase 0 sprint 1).
3. Agree the pilot tenant and the parallel-run window.
4. Order the PoC hardware list in [MOBILE_AND_DEVICES.md](MOBILE_AND_DEVICES.md) §4 so it arrives by Phase 2.
5. Start the hi/ta glossary with the translators, using the Figma screens as source.
