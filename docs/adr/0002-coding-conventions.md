# ADR 0002: Coding conventions follow the current industry standard per language

**Status:** Accepted · **Date:** 2026-11-03 · **Deciders:** Mahesh (product/engineering lead)

## Context

The project skills (`solid-clean-code`, `testing-strategy`, `secure-coding`, …) say how code is designed. They did not say which style guide, formatter and linter settle the mechanical questions, and a team of 10–12 across Java, TypeScript and Dart will otherwise spend review time on formatting and naming. The user's direction is to adopt the **industry standard as of now** rather than invent house rules.

## Decision

Each language uses its de-facto public style guide, enforced by an automatic formatter and a type-aware linter that run in pre-commit and fail CI. Review never comments on anything a tool enforces.

| Language | Style guide | Formatter | Static analysis |
|---|---|---|---|
| Java 25 | Google Java Style Guide | google-java-format via Spotless (`spotlessCheck` in CI) | Checkstyle (`google_checks`), Error Prone + NullAway in JSpecify mode (`@NullMarked` packages), ArchUnit for hexagonal boundaries, Sonar "Sonar way", JaCoCo gate |
| Kotlin (Gradle DSL only) | Kotlin official style | ktlint via Spotless | — |
| TypeScript / React 19 | typescript-eslint `strictTypeChecked` + `stylisticTypeChecked`; `tsconfig` strict + `noUncheckedIndexedAccess` + `exactOptionalPropertyTypes` + `verbatimModuleSyntax` | Prettier 3 (defaults, `eslint-config-prettier` last) | ESLint flat config: typescript-eslint, `@eslint-react`, `react-hooks`, `jsx-a11y`, `import-x` boundaries, `tsc --noEmit`, Knip for dead exports |
| Dart / Flutter | Effective Dart | `dart format` (formatter defaults, trailing commas managed by the formatter) | `flutter_lints` + `very_good_analysis`, `strict-casts/inference/raw-types`, `riverpod_lint` via custom_lint, `dart analyze --fatal-infos` |
| SQL / Flyway | snake_case identifiers, one statement per concern | — | SQLFluff (postgres dialect) on migrations |
| OpenAPI | project ruleset on top of Spectral `oas` | — | Spectral in CI; openapi-diff breaking-change check |
| Commits / PRs | Conventional Commits, trunk-based (see `engineering-workflow`) | commitlint | — |

Pre-commit runs formatters and the fast lint set through lefthook (one config for the monorepo). CI runs the full set as gates before tests (`testing-strategy` §7). Exact tool versions live in the Gradle version catalog and `package.json`/`pubspec.yaml`, pinned at Phase 0 and updated by Renovate.

Design rules that tools cannot check (SOLID, value objects, typed errors, ubiquitous language) stay in `solid-clean-code` and are checked in review.

## Consequences

- Good: zero formatting debate; a new engineer's first PR looks like everyone else's; type-aware lint catches whole classes of bugs before tests run.
- Good: public style guides have documentation, IDE plugins and training material the team does not have to write.
- Bad: google-java-format's 2-space, 100-column style differs from the Spring and IntelliJ defaults many Java developers are used to; the IDE plugin removes the friction, and the rule is not negotiable per developer.
- Bad: `strictTypeChecked` and `very_good_analysis` are noisy on scaffolded code; Phase 0 cleans the scaffold rather than lowering the bar. A rule is only disabled repo-wide through an ADR; a single-line disable needs a reason comment.

## Alternatives considered

- **Palantir Java Format** (4-space, Spring-like): popular, but Google style has broader tooling and documentation. Rejected.
- **Biome** as the single JS formatter+linter: fast, but the React a11y and type-aware rule coverage still trails the ESLint ecosystem. Revisit in 2027.
- **House style guide**: rejected on principle (see Context).
