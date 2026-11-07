# Team Skills Matrix

Companion to [PROJECT_INITIATION.md](PROJECT_INITIATION.md). This lists the skills needed to build and run Allied Payroll: a **multi-tenant, multilingual, industry-configurable** payroll and workforce SaaS built as one Spring Boot API backend (Spring Modulith modules), one React web app and one Flutter app for the Indian market.

Proficiency levels: **L1** working knowledge · **L2** independent practitioner · **L3** expert / can set direction and review others.
Phase: **P0** foundation · **P1** MVP payroll · **P2** billing, onboarding, mobile · **P3** operations, inventory, ecosystem.

---

## 1. Skill inventory

### 1.1 Domain knowledge (hardest to hire, most important)

| Skill | Why this project needs it | Level | Phase |
|---|---|---|---|
| Indian payroll processing | Components, CTC structuring, proration, LOP, arrears, F&F settlement, payslips | L3 (1 person), L2 (lead dev, QA) | P0+ |
| EPF & MP Act / Social Security Code | PF wages, ceiling, EPS/EDLI, ECR, UAN lifecycle, Labour Code changes | L3 | P1 |
| ESIC | Eligibility, contribution periods, IP numbers, sub-codes, monthly filing | L3 | P1 |
| State Professional Tax & Labour Welfare Fund | State-wise slabs and frequencies, registrations | L2–L3 | P1 |
| Minimum Wages / Code on Wages | State × zone × skill schedules, VDA revisions → arrears, 50% wage definition | L3 | P1 |
| Shops & Establishments Acts / National & Festival Holidays Acts | State holiday minimums, working hours, weekly off, OT | L2 | P1 |
| Income-tax on salary (TDS) | Old/new regimes, declarations, proofs, Form 16, 24Q; transition to Income-tax Act 2025 | L3 | P2 |
| Bonus & Gratuity | Eligibility, ceilings, accrual vs payout, billing as cost | L2 | P2 |
| Contract Labour (CLRA) | Licences, headcount caps, statutory registers, principal-employer obligations | L2 | P1 |
| PSARA & police verification | Security-agency licensing, guard verification and training records | L2 | P3 |
| GST for services and goods | 18% services, place of supply, CGST/SGST/IGST, RCM for security services, LUT, HSN/SAC | L3 | P2 |
| GST e-invoicing & e-way bill | IRN via IRP/GSP, cancellation windows, e-way bill for material movement (engineering profile) | L2 | P2–P3 |
| Staffing / security operations | Sites, posts, shifts, relievers, rate cards, billing from attendance, site P&L | L2 | P1 |
| Project & field-service operations | Work orders, AMC, timesheets, job costing, site stores (boiler-engineering profile) | L2 | P3 |
| Inventory & procurement | Stock ledger, valuation, GRN, transfers, issue to job, reorder | L2 | P3 |
| Data protection (DPDP Act 2023, Aadhaar Act) | Consent, purpose limitation, retention, Aadhaar vault, breach process | L2 (+ legal advisor) | P0+ |

### 1.2 Backend engineering

| Skill | Specifics for this project | Level | Phase |
|---|---|---|---|
| Java 25 | Records, sealed types, pattern matching, virtual threads, `BigDecimal` money handling, `java.time`, JSpecify null-safety | L3 (lead), L2 (all) | P0 |
| Spring Boot 4.x | Modular starters, Jackson 3, auto-config, profiles, Actuator, configuration properties, Spring Modulith; Boot 3→4 migration notes read before Phase 0 | L3 / L2 | P0 |
| Spring Data JPA / Hibernate 7 | `@TenantId` discriminator, filters, effective-dated entities, N+1 avoidance, batch inserts | L3 (1–2), L2 | P0 |
| Multi-tenant SaaS design | Tenant resolution, context propagation, PostgreSQL RLS, noisy-neighbour control, tenant lifecycle | L3 (architect) | P0 |
| PostgreSQL | Schema design, RLS policies, partitioning (paysheets, audit), JSONB + GIN, ICU collations, query tuning | L3 (1), L2 | P0 |
| Flyway | Versioned migrations, tenant-safe data migrations, zero-downtime patterns | L2 | P0 |
| Domain-driven design | Bounded contexts, aggregates, effective dating, domain events | L3 (architect), L2 | P0 |
| Event-driven design (Spring Modulith) | Event publication registry as outbox, async idempotent listeners, replay of incomplete publications; Kafka topics and consumers only from Phase 9 | L3 (1), L2 | P0 (Kafka P9) |
| Distributed consistency | Sagas / choreography, snapshots, eventual consistency in UI | L2–L3 | P1 |
| Spring Batch | Partitioned payroll runs, restartable jobs, bulk imports, statutory file generation | L2–L3 | P1 |
| Rules & formula engines | Sandboxed expression evaluation, dependency-graph ordering, strategy plug-ins per industry | L3 (1) | P1 |
| Spring Security / OAuth2 / OIDC | Resource servers, JWT claims (tenant, scopes), method security, data-scope enforcement | L3 (1), L2 | P0 |
| Keycloak | Realm/organization model, OTP login, token mappers, admin API, theming per tenant | L2 | P0 |
| Edge and ingress (cloud LB / Kubernetes ingress) | TLS, subdomain routing, WAF, coarse rate limits; in-app tenant filter and per-tenant rate limiting | L2 | P0 |
| REST API design / OpenAPI | Contract-first, pagination/filter conventions, problem+json, versioning, idempotency keys, ETags, SDK generation | L2–L3 | P0 |
| Channel modules & API product design | Token-handler endpoints, mobile sync endpoints, public API products, quotas, webhooks (HMAC, retries, DLQ) | L2–L3 | P0–P9 |
| Resilience & integration | Resilience4j, retries/backoff, ports-and-adapters for KYC, bank, IRP, SMS providers | L2 | P2 |
| Redis | Caching per tenant, distributed locks (payroll run), idempotency keys | L2 | P1 |
| Document generation | Thymeleaf + OpenHTMLtoPDF (English PDFs), Apache POI for Excel; Chromium-based rendering only if regional PDFs are added later | L2 | P1 |
| Internationalization (backend) | ICU4J MessageFormat, `MessageSource`, locale propagation, number-to-words per language | L2 | P0 |
| Reporting / CQRS read models | Event-fed projections, async report jobs, Excel exports at scale | L2 | P2 |

### 1.3 Front end & mobile

| Skill | Specifics | Level | Phase |
|---|---|---|---|
| React + TypeScript (or chosen SPA stack) | Large admin app, module federation or lazy routes, global context bar (branch/period/client) | L3 (1), L2 | P1 |
| Data-heavy UI | Server-side tables, filters in URL, bulk import wizard, attendance grids (virtualised) | L2–L3 | P1 |
| Front-end i18n | ICU messages, namespaces, RTL via logical CSS, Indic font loading, pseudo-localization | L2 | P0 |
| Accessibility | WCAG 2.2 AA | L2 | P1 |
| **Flutter / Dart** (decided) | Riverpod, go_router, Drift + SQLCipher offline queue, generated dart-dio client, platform channels (kiosk lock-task), ARB i18n, Fastlane CI | L3 (1), L2 | P2 (start the PoC in P1) |
| Face SDK integration | Commercial face SDK behind `FaceEngine`; 1:1 / 1:N, liveness, quality; FAR/FRR testing | L2 | P2 |
| Device fleet / MDM | Samsung Knox / Android Enterprise dedicated devices, OTA app updates | L2 | P2 |
| On-device ML / face capture | Liveness checks, image quality, provider SDK integration | L2 | P2 |
| Maps & geolocation | Geofence validation, visit timelines (field ops) | L2 | P3 |
| Offline-first sync | Encrypted local queue, idempotent batch sync, delta sync with cursors and tombstones, clock-offset handling | L3 (1) | P2 |
| Kiosk / dedicated-device apps | Android Enterprise / MDM dedicated mode, device provisioning by QR, remote disable | L2 | P2 |
| Responsive / tablet UX | One web SPA that works on desktop and tablet; touch-friendly attendance grids | L2 | P1 |
| Biometric device integration | Vendor push/poll protocols (eSSL, ZKTeco, Matrix …), adapter pattern | L2 | P3 |

### 1.4 Platform, DevOps & security

| Skill | Specifics | Level | Phase |
|---|---|---|---|
| Docker & Kubernetes | Helm charts, HPA, pod security, config per deployment (`backend-api`, `backend-batch`, `web`) | L2–L3 | P0 |
| CI/CD (GitHub Actions) | Gradle builds, Testcontainers in CI, image signing, environment promotion | L2 | P0 |
| Cloud (AWS / Azure / GCP — India region) | Managed Postgres, Redis, object storage, KMS, load balancer / WAF; managed Kafka only from Phase 9 | L2–L3 | P0 |
| Infrastructure as code | Terraform / OpenTofu | L2 | P0 |
| Observability | OpenTelemetry, Prometheus/Grafana, Loki/ELK, tenant-tagged telemetry, SLO alerting | L2 | P0 |
| Application security | OWASP ASVS L2, threat modelling, SAST/DAST, dependency scanning, secrets management | L2–L3 | P0 |
| Data encryption & key management | Envelope encryption, per-tenant data keys, blind indexes, Aadhaar vault | L3 (1) | P1 |
| Backup / DR | PITR, cross-zone replicas, restore drills, per-tenant export | L2 | P1 |

### 1.5 Quality engineering

| Skill | Specifics | Level | Phase |
|---|---|---|---|
| Test automation (JUnit, AssertJ, Testcontainers) | Unit, module (`@ApplicationModuleTest`), repository tests with real Postgres | L2 | P0 |
| Contract testing | Spring Cloud Contract / Pact between services | L2 | P1 |
| Payroll golden-file testing | Scenario workbooks per state × profile, rupee-exact reconciliation | L3 (with domain expert) | P1 |
| Tenant-isolation testing | Automated cross-tenant access tests at API, DB (RLS) and storage layers | L2 | P0 |
| Performance testing | Gatling/k6: 10k-paysheet runs, month-end load, report generation | L2 | P1 |
| Localization QA | Pseudo-locale runs, Indic script checks in web and mobile UI | L1–L2 | P1 |
| E2E UI testing | Playwright | L2 | P1 |

### 1.6 Product, analysis & delivery

| Skill | Specifics | Level | Phase |
|---|---|---|---|
| Product management (B2B SaaS) | Pricing per employee/month, plan packaging, tenant onboarding | L2–L3 | P0 |
| Business analysis | Industry workshops (security, engineering, IT), process maps, acceptance criteria | L2–L3 | P0 |
| UX design for enterprise + low-literacy mobile | Dense admin screens; icon-first regional-language mobile flows | L2 | P1 |
| Technical writing | API docs, admin guides, multilingual help content | L1–L2 | P2 |
| Translation & linguistic review | Payroll/legal glossary in each launch language | Vendor | P1 |
| Implementation / customer success | Data migration from legacy payroll, parallel runs | L2 | P2 |

---

## 2. Proposed team composition

| Role | Count | Must-have skills (L3 unless noted) | Join by |
|---|---|---|---|
| Solution architect / tech lead | 1 | Multi-tenant SaaS, DDD, Spring Modulith, PostgreSQL, security | P0 |
| Payroll & compliance domain expert | 1 (+ external labour-law/GST consultant on retainer) | Indian payroll, EPF/ESIC/PT/LWF/TDS, Labour Codes, GST | P0 |
| Senior backend engineers | 2 | Spring Boot, JPA multitenancy, Spring Batch / rules engine | P0 |
| Backend engineers | 3–4 | Spring Boot L2, PostgreSQL L2, Spring Modulith L2 | P0–P1 |
| Front-end engineers | 2 | React/TS L3 (1), data-heavy UI, i18n | P1 |
| Mobile engineer | 1–2 | Flutter (L3 for the lead), offline sync, camera/face SDK, Android device management | P1 (PoC) – P2 |
| DevOps / platform engineer | 1 | Kubernetes, Terraform, CI/CD, observability | P0 |
| Security engineer | 0.5 (shared or consultant) | AppSec, encryption, DPDP | P0 |
| QA / SDET | 2 | Test automation L2, payroll golden tests with the domain expert | P0–P1 |
| Product owner / business analyst | 1–2 | Staffing + corporate payroll domains, B2B SaaS | P0 |
| Senior product designer | 1 | Data-dense enterprise UX, design systems (Figma Variables/tokens), low-literacy mobile, kiosk | P0 |
| UX researcher | 0.5 (contract) | Discovery interviews, site visits, usability testing, SUS | P0–P1 |
| Design technologist (front end) | 1 (from the front-end team) | Tokens pipeline (Style Dictionary), Storybook, shadcn/Radix, Flutter theming, a11y | P0–P1 |
| Translators / linguistic reviewers | Vendor | Hindi, Tamil, Telugu, Kannada, Malayalam (launch) | P1 |

**Critical single points of failure to cover early:** payroll/compliance domain expertise, the multi-tenancy/security architect, and the formula/rules-engine owner. Pair a second person with each.

---

## 3. Skill gaps to close (training plan)

| Topic | Audience | Format |
|---|---|---|
| Indian payroll & statutory basics (PF, ESIC, PT, LWF, minimum wages, TDS) | All engineers, QA | 2-day internal workshop by the domain expert; golden-workbook walkthrough |
| GST & e-invoicing for services | Billing module team | 1-day session + IRP sandbox exercise |
| Multi-tenancy patterns & RLS in this codebase | All backend engineers | Brown-bag + `tenant-aware-entity` Claude skill + code review checklist |
| Event-driven design with outbox | Backend engineers | Workshop + reference implementation in `common-events` |
| i18n & Indic text rendering | Full-stack, QA | Session on ICU, pseudo-localization, PDF shaping pitfalls |
| Industry profiles & strategies | All engineers, BA | Walkthrough of [INDUSTRY_PROFILES_AND_STRUCTURE.md](INDUSTRY_PROFILES_AND_STRUCTURE.md) with the three reference tenants |
| Secure handling of PII / Aadhaar | Everyone | Mandatory session; DPDP obligations |

---

## 4. AI-assisted development (Claude Code skills)

Project conventions are captured as Claude Code skills in [`.claude/skills/`](../.claude/skills/). Each one encodes a rule from these documents, so that generated code follows the architecture:

| Skill | Use when |
|---|---|
| `new-backend-module` | Adding a Spring Modulith module to the single backend |
| `tenant-aware-entity` | Adding any table / JPA entity / repository |
| `industry-profile-feature` | Adding behaviour that differs by industry, or a new module toggle |
| `statutory-rule` | Adding or changing PF/ESIC/PT/LWF/min-wage/holiday/TDS rules |
| `payroll-component` | Adding salary components, formulas, pay-basis or rounding logic |
| `i18n-text` | Adding any user-facing text, master data names, documents or notifications |
| `bulk-import` | Adding an Excel/CSV import or export |
| `headless-api` | Adding any REST, channel, sync, device-ingest, webhook or partner API endpoint |
| `flutter-mobile` | Building the Flutter app (employee, manager, kiosk modes), offline sync, face capture |
| `ui-design-system` | Any UI work: tokens, theming, white-label, typography, components, accessibility |
| `ux-patterns` | Designing or implementing any screen or flow (list, form, wizard, import, approvals, month-close cockpit, mobile, kiosk) |
| `ui-review` | Reviewing UI before done: Playwright/Flutter screenshots, a11y, i18n, responsive, Figma comparison |
| `design-taste` | Composing and polishing any screen: hierarchy, colour discipline, contrast, rhythm, restraint, polish audit |
| `react-web` | Building the React web app: structure, App Shell, routing, SDK data access, forms, i18n, a11y, testing |
| `solid-clean-code` | Writing or refactoring any code: SOLID, value objects, small functions, typed errors, per-language idioms |
| `testing-strategy` | Adding or reviewing tests: pyramid, mandatory slice tests, payroll golden files, CI gates |
| `secure-coding` | Anything touching auth, scope, PII, uploads, imports, providers, secrets, logs (ASVS L2, DPDP) |
| `performance-and-scale` | Queries, batch jobs, caches, locks, large tables, low-end mobile, observability and SLOs |
| `engineering-workflow` | Branching, commits, PRs, review checklist, CODEOWNERS, ADRs, releases, definition of done |

Installed tooling for design work (project scope): the `frontend-design` plugin skill, the **Playwright**, **Context7** and **Figma** (remote MCP + Figma skills) plugins, and the **Dart/Flutter** and fallback **figma-desktop** MCP servers in `.mcp.json`. See [UI_UX_STRATEGY.md](UI_UX_STRATEGY.md) §8.

These skills describe **proposed** conventions. Update them when the platform libraries are built and the conventions change.
