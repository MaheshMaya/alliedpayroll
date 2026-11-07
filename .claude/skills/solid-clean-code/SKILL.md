---
name: solid-clean-code
description: Use whenever writing or refactoring any Allied Payroll code — Java/Spring Boot services, React/TypeScript web, Flutter/Dart app, Gradle or build scripts. Applies SOLID, clean-code and domain-modelling rules (value objects for money/period/ids, small pure functions, explicit errors, no primitive obsession, no god classes) and the per-language idioms the team has agreed. Works with new-backend-module, headless-api, react-web and flutter-mobile, which say where code goes; this skill says how it is written.
---

# SOLID and clean code

The other skills say *where* code lives and *what* it must do. This one says *how it is written*, so that a payroll engine other people will audit for eight years stays readable. Apply it to every language in the repo.

## 0. Convention baseline (industry standard, ADR 0002)

Mechanical style is settled by public style guides and tools, never by review comments. Decided 2026-11-03 in `docs/adr/0002-coding-conventions.md`; exact versions are pinned in the version catalog, `package.json` and `pubspec.yaml`.

| Language | Style guide | Formatter | Lint / analysis (all fail CI) |
|---|---|---|---|
| Java 25 / Spring Boot 4 | **Google Java Style Guide** (2-space, 100 col, import order) | google-java-format via Spotless | Checkstyle `google_checks`; **Error Prone + NullAway in JSpecify mode** (`@NullMarked` on every package via `package-info.java`); ArchUnit (`domain` imports no framework; `application` depends only on ports); Sonar "Sonar way"; JaCoCo |
| Gradle (Kotlin DSL) | Kotlin official style | ktlint via Spotless | version catalog `gradle/libs.versions.toml`; convention plugins in `build-logic`; no `allprojects {}` blocks |
| TypeScript / React 19 | typescript-eslint **`strictTypeChecked` + `stylisticTypeChecked`**; `tsconfig` `strict`, `noUncheckedIndexedAccess`, `exactOptionalPropertyTypes`, `verbatimModuleSyntax` | Prettier 3 defaults; `eslint-config-prettier` last | ESLint flat config (`eslint.config.ts`): typescript-eslint, `@eslint-react`, `react-hooks`, `jsx-a11y`, `import-x` boundaries; `tsc --noEmit`; Knip |
| Dart / Flutter | **Effective Dart** | `dart format` (formatter manages trailing commas) | `flutter_lints` + `very_good_analysis`; `strict-casts`, `strict-inference`, `strict-raw-types`; `riverpod_lint`; `dart analyze --fatal-infos` |
| SQL / Flyway | snake_case; `V<yyyyMMddHHmm>__<verb>_<object>.sql` | — | SQLFluff (postgres) |
| OpenAPI | Spectral `oas` + project ruleset (`headless-api`) | — | Spectral; openapi-diff breaking-change check |
| Commits | Conventional Commits (`engineering-workflow` §2) | — | commitlint |

- Pre-commit (lefthook) runs the formatters and fast lints on staged files; CI runs everything before tests.
- Disabling a rule on one line needs a reason comment; disabling repo-wide needs an ADR. Scaffolded code is cleaned up to the bar, the bar is not lowered.
- Naming follows the style guide of each language: Java `UpperCamelCase` types / `lowerCamelCase` members / `UPPER_SNAKE` constants; TS `PascalCase` components and types, `camelCase` values, kebab-case file names; Dart `UpperCamelCase` types, `lowerCamelCase` members, `snake_case` files. Domain words come from §4.

## 1. SOLID, as it applies here

| Principle | Rule in this codebase | Smell to reject in review |
|---|---|---|
| **Single responsibility** | One class, one reason to change. A use case (`ApprovePayrollRun`) is one class; a controller only maps HTTP to a use case; an entity holds state and invariants, not HTTP, JPA queries or formatting. | `PayrollService` with 40 public methods; a React component that fetches, transforms and renders; a Riverpod notifier that also talks to the camera |
| **Open/closed** | Variation goes through **strategies and configuration**, never through editing a switch: `PayBasis`, `OvertimeRule`, `AttendanceMode`, `FaceEngine`, `PdfRenderer`, bank-file formats, KYC providers. New behaviour = new implementation + registration. | `switch (industry)`, `if (tenant.id == …)`, `if (bank == "HDFC")` inside business code |
| **Liskov substitution** | Every strategy implementation honours the interface contract and its tests. A `ProrationDivisor` that throws for February breaks LSP. Contract tests run against every implementation. | Implementations that return `null` or throw `UnsupportedOperationException` for a method the interface promises |
| **Interface segregation** | Ports are small and named by the caller's need: `EmployeeStatutoryProfileReader`, not `EmployeeRepository` with 30 methods passed into the payroll engine. Front-end components take the props they render, not the whole API object. | A use case injected with a repository and using 1 of its 25 methods; a widget taking a `Paysheet` to show a name |
| **Dependency inversion** | `domain` and `application` depend on ports (interfaces) they own; `infrastructure` implements them. Spring wires them. The pure payroll calculation core has **no** Spring, JPA, messaging or clock dependency. | `domain` importing `jakarta.persistence`, `org.springframework.web`, `dio`, `react-query` |

## 2. Domain modelling rules

- **No primitive obsession.** Money is `Money` (BigDecimal scale 2 + currency), never `double` or a bare `BigDecimal`. Periods are `YearMonth` or a `PayPeriod` value object. Ids are typed (`EmployeeId`, `TenantId`), not `UUID`/`String`. Percentages are `Percentage`. State and zone codes are enums or typed codes.
- **Value objects are immutable** — Java `record`, TS `readonly` types, Dart `@freezed` or `final` classes with `==`.
- **Aggregates guard their own invariants.** A `PayrollRun` refuses `approve()` unless it is `IN_APPROVAL`; the check is not in the controller. State machines are explicit (`enum` + transition method), not scattered `if (status == …)`.
- **Make illegal states unrepresentable.** Prefer sealed interfaces / discriminated unions (`PunchResult = Accepted | Flagged(reason) | Rejected(reason)`) over nullable fields and boolean flags.
- **Effective dating is a type**, not two nullable columns handled ad hoc: `EffectiveRange(from, to)` with `contains(date)`.
- **Time and randomness are injected** (`Clock`, `IdGenerator`) so tests are deterministic and payroll snapshots reproducible.

## 3. Functions and classes

- A function does one thing and its name says what: `computeEsicContribution(wages, ruleSet)`. Side effects live in verbs (`record`, `publish`, `send`); queries have none (CQS).
- Keep functions short enough to read without scrolling; extract when a comment would otherwise be needed to label a block.
- **At most 3–4 parameters**; beyond that, pass a request/record object.
- No boolean parameters that change behaviour (`generate(payslip, true)`); use two methods or an enum.
- **Fail fast at the boundary, trust the inside.** Validate at controllers, importers and sync endpoints (Bean Validation, zod, Dart validators); domain code assumes valid input and asserts invariants.
- **Return results, don't signal with nulls.** Java `Optional` for absence in return types only (never fields or parameters); TS/Dart nullable types with explicit handling. Domain outcomes that callers must handle are result types, not exceptions.
- **Exceptions** are for unexpected failures. Domain errors are typed (`PayrollRunLockedException` → problem+json code `payroll.run.locked`), carry a message key and params, never a formatted English string.
- Errors are never swallowed. `catch (Exception e) { log.warn(...) }` with no rethrow or result needs a written justification in the catch block.

## 4. Naming and structure

- Names use the **ubiquitous language** from `docs/PROJECT_INITIATION.md` §3: `Paysheet`, `WorkInformation`, `Deployment`, `RateCard`. Industry terminology (`Guard`, `Technician`) never appears in code identifiers — it is a terminology key.
- Packages/folders by **feature**, not by layer-only: `payroll/run/`, `payroll/component/`, not `services/`, `utils/`, `helpers/`. No `Util`, `Helper`, `Manager`, `Common`, `Misc` classes; name what it does (`IndianNumberFormatter`).
- One public class per file; files under ~300 lines; a class with more than ~7 collaborators is split.
- Delete dead code; don't comment it out. Git has it.
- Comments explain **why** (a statutory rule reference, a workaround with a ticket), never what the code already says. Javadoc/TSDoc on public ports and domain types only.
- **Anti-slop comments** (`antislop-code`, the project standard): no banner or boxed separators, no ALL-CAPS labels, no step-by-step narration of visible control flow, no comment that restates the next line, no signature-echo docs, no emoji, no end-of-block markers, no TODO without a concrete task. One comment per logical block at most, one line (two only when the second adds a fact), sentence case, plain developer voice. Keep comments on business intent, architecture, security, performance, concurrency, protocol, API contract, workaround, edge case and licence.
- No magic numbers: `ESIC_WAGE_CEILING` lives in the rule table, not in code; UI spacing uses tokens.

## 5. DRY, YAGNI, and the opposite

- Duplicate once, abstract on the second real repetition — and only when the two uses change for the same reason. Two similar-looking statutory rules that change under different laws are not duplication.
- Don't build for hypothetical tenants or phases ("in case we add PIECE_RATE later"). The strategy *seam* is free; the implementation waits for its phase in `docs/DEVELOPMENT_PLAN.md`.
- Shared code goes to `backend/platform/common-*` only when two modules need it *now*.

## 6. Per-language idioms

**Java 25 / Spring Boot 4 (ADR 0001)**
- `record` for DTOs, events and value objects; sealed interfaces for result types; pattern matching in `switch` instead of `instanceof` chains.
- **Null-safety is JSpecify**: every package is `@NullMarked`; `@Nullable` only where absence is a real state; NullAway fails the build on an unchecked dereference. Never `Optional` as a field or parameter.
- Jackson 3 (`tools.jackson`) for JSON; records serialize without annotations; API field names come from the record components through the global naming strategy, not per-field `@JsonProperty` renames.
- Spring Modulith `@ApplicationModule` per bounded context in `backend/modules/`; cross-module calls go through the module's published `api` package, verified by the Modulith verification test (ADR 0003).
- Constructor injection only; no field injection, no `@Autowired` on constructors; `final` fields.
- `@Transactional` on application services, never on controllers or repositories; read-only where applicable.
- Use `BigDecimal` with explicit `RoundingMode` and the rounding strategy from the component; never `double` for money.
- Virtual threads for I/O-bound work; no `synchronized` around I/O.
- Prefer `RestClient`/declarative HTTP interfaces over `RestTemplate`; Resilience4j on every outbound call.
- Lombok only for `@Slf4j`; records and constructors replace `@Data`/`@Builder` (builders are fine for test data).

**TypeScript / React 19**
- `strict: true`, no `any`; `unknown` + narrowing at boundaries; generated SDK types are the API types — never hand-written mirrors.
- Discriminated unions for UI state (`{status:'loading'} | {status:'error', problem} | {status:'ready', data}`).
- Components are functions with typed props; hooks for logic (`usePayrollRun`), components for rendering; no business rules in either (see `headless-api`).
- Immutable updates; derived state computed, not stored.
- Named exports; one component per file; co-locate test, story and styles.

**Dart / Flutter**
- `final` everywhere it can be; `const` constructors; `sealed` classes and exhaustive `switch` for states and results.
- Riverpod providers hold state and orchestration; widgets only build. No `BuildContext` in providers.
- Effective Dart naming and lints (`flutter_lints` + the project `analysis_options.yaml`, which turns on `prefer_final_locals`, `avoid_dynamic_calls`, `require_trailing_commas`).
- No `dynamic` from JSON: the generated dart-dio SDK models are the API types.

## 7. Review checklist (paste into the PR when relevant)

- [ ] Each new class has one responsibility and a name that says it
- [ ] Variation added through a strategy/config seam, not a conditional on tenant, industry or vendor
- [ ] Money, period, ids, percentages are value objects
- [ ] Domain layer imports no framework
- [ ] No nulls or booleans used as signals; results and sealed types used
- [ ] Errors are typed with message keys; nothing swallowed
- [ ] Names from the ubiquitous language; no `Util`/`Helper`/`Manager`
- [ ] No dead code, no commented-out code, no TODO without a ticket
- [ ] Formatter and linters green locally (§0); any rule disable has a reason comment
- [ ] `antislop-code` checklist clean: every comment adds information; no banners, narration or restating; density one per block
