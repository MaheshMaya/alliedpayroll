---
name: testing-strategy
description: Use whenever adding, changing or reviewing tests in Allied Payroll — JUnit/Testcontainers for Spring Boot services, Vitest/Testing Library/Playwright for React, Flutter unit/widget/golden/integration tests, payroll golden-file tests, contract tests and tenant-isolation tests. Defines the test pyramid, what each layer must cover, naming, test data, determinism and the CI gates a slice must pass before it is done.
---

# Testing strategy

Payroll is money and compliance: a wrong number costs trust and a wrong filing costs penalties. Tests are the product's audit trail as much as its safety net. Every slice in `docs/DEVELOPMENT_PLAN.md` ships with the tests below, and CI blocks merges without them.

## 1. The pyramid and what lives where

| Layer | Backend (Java) | Web (React) | Mobile (Flutter) | Share of tests |
|---|---|---|---|---|
| **Unit** | Domain and application: pure calculation core, strategies, state machines, formula evaluator, rule evaluation. No Spring context. JUnit 5 + AssertJ. | Hooks, pure functions (formatters, validators, selectors). Vitest. | Domain logic, providers, formatters. `test` package. | ~70% |
| **Integration** | One module with real Postgres via **Testcontainers** (`@ApplicationModuleTest`): repositories, RLS, event publication registry, Flyway migrations, JPA mappings, batch jobs. `@SpringBootTest` only for the whole-application smoke test. | Components with Testing Library against **MSW** mocks of the generated SDK. | Widget tests with a fake SDK; Drift queries against in-memory SQLite. | ~20% |
| **Contract** | OpenAPI contract tests (springdoc output vs committed spec; breaking-change check). Modulith event contract tests (published event records in each module's `api` package) plus the Modulith verification test for module boundaries. | SDK generated from the committed spec; type errors fail the build. | Same, Dart SDK. | — |
| **Golden** | **Payroll and statutory golden files** (§3). | Storybook visual snapshots for design-system components. | Golden image tests for key screens. | — |
| **End to end** | — | **Playwright** flow per slice (sign in → task → assertion), against a seeded `dev` stack. | `integration_test` on the device farm: low-end Android, kiosk tablet, iOS. | ~10% |

Rules of thumb:
- If a test needs Spring to run, ask whether the logic should move to a pure class first.
- Don't mock what you own (repositories, domain services); use Testcontainers. Mock only **external ports** (KYC provider, IRP, bank API, face SDK, SMS) with the port's fake implementation from `common-test`.
- One assertion concept per test; the name states the rule: `esicApplies_whenGrossAtOrBelowCeiling_atStartOfContributionPeriod`.

## 2. Mandatory tests per slice

Every phase's definition of done requires, for the code it adds:

1. **Tenant isolation** — for every new aggregate: tenant A cannot read, update, delete or list tenant B's rows through the API *and* directly through JPA with tenant B's context *and* RLS blocks a raw SQL read. Use `TenantIsolationTestSupport` from `common-test`.
2. **Data scope** — a user scoped to branch X does not see branch Y via list, detail, count or export endpoints.
3. **Authorization** — every endpoint has a test that a user without the permission gets `403` with the right problem code.
4. **Audit** — a create/update/delete produces the audit record with old/new values and the channel.
5. **i18n** — every new problem code and UI string has a key in `en-IN`, `hi` and `ta`; the pseudo-locale build has no missing keys (CI job).
6. **Idempotency** — every mutating endpoint reachable from mobile, kiosk, device or partner replays safely with the same `Idempotency-Key`.
7. **Migration** — Flyway migrates from the previous release's schema with the previous release's data (migration test fixture).
8. **State machine** — every allowed and every forbidden transition of a new aggregate.
9. **Main user task** — one Playwright (web) or integration test (mobile) for the slice's headline task from the plan's exit criteria. The Playwright flow signs in through the real `/auth/login` (OIDC Authorization Code + PKCE) as a seeded tenant user on the tenant subdomain; it signs in once per worker and reuses the saved storage state. No test login route, no injected token, no minted cookie (`vertical-slice` §3).

Two rules apply to every test above (`backpressure` §4): a new test is seen **red before green**, run once against the pre-change code and failing; and a test is **never weakened** (assertion loosened, case deleted, tolerance widened, skipped or quarantined) to obtain a green run. A test changes only when the requirement changed, and the commit says so.

## 3. Payroll golden-file tests

The payroll and compliance engines are verified against **golden cases owned by the domain expert**, not against assertions engineers invent.

- Location: `backend/modules/payroll/src/test/resources/golden/{state}/{scenario}/`.
- Each case: `input.yaml` (attendance/work information, structure or rate card, statutory profile, rule-set version, period) and `expected.yaml` (every paysheet line with basis and amount, statutory contributions, net pay, employer cost). A `README.md` cites the rule (act, section, notification date).
- The runner (`GoldenPayrollTest`) is parameterised over all cases; **a failing golden test cannot be fixed by editing `expected.yaml` without the domain expert's review** (CODEOWNERS enforces it).
- Minimum coverage before the Phase 5 exit: per state in the pilot (TN, TS at least) × {monthly fixed, per duty, OT single, OT double, reliever, LOP, mid-month joiner, mid-month exit, hold, ESIC crossing ceiling mid-period, PF on actual wages, minimum-wage breach}. Phase 8 adds arrears, bonus, gratuity, TDS old/new regime.
- Reconciliation test: the pilot reference workbook (300 employees) runs end to end and the totals match to the rupee.
- Rounding is tested at the boundaries (`.005`, negative arrears, zero duties).

## 4. Test data

- Builders (`anEmployee().inState("TN").withGross(money("21000"))`), never shared mutable fixtures. One builder per aggregate in `common-test` or the service's `testsupport` package.
- All names, sites and amounts are fictional (`Sentinel Guard Services`, `Tidel Park — Gate 2`). Never copy data from `reference/` screenshots.
- A seeded demo tenant pair (`sentinel`, `nimbus-it`) for e2e tests and demos, created by a Flyway `afterMigrate` callback in `dev` profile only.
- Dates are explicit; tests inject a fixed `Clock`. Nothing uses `LocalDate.now()` in a test or in domain code.

## 5. Determinism and speed

- Unit suite under 2 minutes per service; integration under 10; the full pipeline under 20 (parallel jobs).
- No `Thread.sleep`; use Awaitility for async outcomes and Testcontainers reuse locally.
- Flaky test policy: a test that fails twice without a code change is quarantined the same day with a ticket; quarantine older than one sprint fails the build.
- Random data (jqwik / fast-check property tests for formula evaluation, rounding, number formatting) uses a logged seed.

## 6. Front-end specifics

- **React**: Testing Library queries by role and accessible name (this doubles as the a11y check). No testing of implementation details (state, hooks internals). MSW handlers generated from the OpenAPI examples. Playwright runs the slice flow at 1440 and 1024; axe-core assertions on every page visited.
- **Flutter**: widget tests for each screen state (loading, empty, error, ready, offline); golden tests at phone and tablet sizes with Tamil strings to catch clipping; integration test for the offline punch queue (airplane mode → 50 punches → sync → no duplicates) on the device farm.

## 7. CI gates (in order)

1. Compile, lint, format check (Spotless, ESLint/Prettier, `dart format`/`dart analyze`).
2. Unit tests.
3. OpenAPI breaking-change check against `main`.
4. Integration tests (Testcontainers).
5. Golden tests (payroll, compliance).
6. Tenant-isolation and security tests.
7. i18n completeness and pseudo-locale.
8. Build images; deploy to `dev`; Playwright and device-farm smoke.
9. Dependency and image vulnerability scan (fails on high/critical).

## 8. Running tests as backpressure

Tests only help while they are run early, run fast and read cheaply. The `backpressure` skill fixes the ladder: format check and typecheck after every edit set (the Stop hook `.claude/hooks/verify.sh` runs this), the touched module's tests at the end of each plan phase, the mandatory slice tests and golden files before a slice is called done, and the full pipeline above only on push and in CI. Every check goes through `run_silent` (`.claude/hooks/run_silent.sh`): one line on success, full output on failure, never piped through `head`, `tail` or `/dev/null`. Long or verbose runs are handed to a subagent that returns only the failing assertion with `file:line`. Results are quoted in the handoff; a check that was not run is listed as not run.

Coverage is reported, not gated, except the pure payroll core and compliance rules: **100% line and branch**.
