---
name: engineering-workflow
description: Use when starting a task, creating a branch, committing, opening or reviewing a pull request, writing an ADR, cutting a release, or asking whether a slice is done in Allied Payroll. Defines trunk-based branching, Conventional Commits, PR size and template, the review checklist that pulls in the other skills, CODEOWNERS rules (payroll golden files, statutory rules, security), ADR format, versioning and the per-slice definition of done from DEVELOPMENT_PLAN.
---

# Engineering workflow

How work moves from a plan line to production. Keep it light; the checks that matter are automated.

## 1. From plan to task

- Work comes from the current phase in `docs/DEVELOPMENT_PLAN.md`. A task is one **functionality slice** (`vertical-slice` skill): the backend API and the web screen for one capability, delivered together. A sub-task may name a **layer** (backend / web / mobile / shared) for parallel work, but the slice merges and closes as one. Every task links the Figma node and the OpenAPI path it touches.
- Before coding a new capability: the OpenAPI contract is reviewed (contract-first), the Figma screen is Ready for dev, and the relevant skills are read (`headless-api`, `tenant-aware-entity`, `react-web`, `flutter-mobile`, `statutory-rule`, …).
- Anything that changes an architecture decision gets an **ADR first** (§5).
- **Right-size the process.** A one-shot change (copy, an obvious bug, a config value) needs no document. A medium task gets one plan document with phases and, for each phase, the verification steps that prove it. A large task runs the four steps from HumanLayer's "Why Software Factories Fail": product review (the problem in user terms, success criteria, a rough mockup if UI), system design (modules, endpoints, schemas, events, a sequence sketch), program design (types, signatures, file layout, call stack, as pseudocode rather than diagrams), then vertical slices. Use `/rpi` for the research and plan documents; they live under `.agents/artifacts/` and are read by the reviewer before the code is.
- **Every plan phase carries its verification.** A phase without a named check (the test to add, the curl or Playwright flow to run, the golden case) is not a phase. After each phase the result is compacted back into the plan and the `backpressure` ladder is run before the next phase starts.
- **Review as you go.** Reviewers read plans (about 200 lines) rather than only the resulting code (about 2,000). Code is reviewed in 100 to 200 line increments per slice, not in one pass at the end; re-steer early.

## 2. Branching and commits

- **Trunk-based.** `main` is always deployable to `dev`; short-lived branches `feat/<slice>-<short-name>`, `fix/…`, `chore/…`, `adr/…`; rebase on `main`, no long-lived integration branches. Feature flags, not branches, hide unfinished work.
- **Conventional Commits**: `feat(payroll): compute ESIC on contribution-period eligibility`, `fix(attendance): reject punches with clock offset > 5 min`, `test(golden): add TN half-yearly PT cases`, `docs(adr): 0007 modular monolith first`. Scope = module or client (`web`, `mobile`, `kiosk`, `sdk`, `deploy`). Body says *why*; footer references the task and `BREAKING CHANGE:` when an API or event changes incompatibly.
- Commits are atomic and build on their own; squash noisy WIP before the PR is marked ready.
- Never commit secrets, generated SDK edits, `reference/` content, real client or employee data, or screenshots of the benchmark product.

## 3. Pull requests

- **Small**: target < 400 changed lines excluding generated code and golden files; split by layer or by migration vs code if larger. One PR = one reviewable idea.
- Template (filled, not deleted):
  1. **What and why** (one paragraph, link to plan line / Figma / contract)
  2. **How to verify** (commands, screenshots for UI at 1440 and 1024, device video for mobile)
  3. **Checklists** pulled from the skills the change touches (see §4)
  4. **Risk and rollback** (migration reversible? flag to turn off?)
- CI must be green (gates in `testing-strategy` §7). A PR that touches `golden/`, `statutory-rule` tables, `common-security`, `common-tenancy` or anything under `pii` requires a **CODEOWNERS** approval (domain expert / security / architect respectively).
- Two approvals for migrations on partitioned tables and for anything in `payroll` or `compliance`; one otherwise.
- Author merges (squash) after approval; the PR title becomes the commit message, so it follows Conventional Commits.

## 4. Review checklist (reviewer runs this; author pre-fills)

**Always**
- [ ] Does the change belong to the current slice and nothing more (YAGNI)?
- [ ] `solid-clean-code`: one responsibility per class, value objects, no framework in domain, typed errors
- [ ] `testing-strategy`: tests at the right layer; mandatory slice tests present; no flaky patterns
- [ ] `i18n-text`: no hard-coded user-facing strings; keys in en/hi/ta
- [ ] Names from the ubiquitous language; no industry if-checks (`industry-profile-feature`)
- [ ] `antislop` Delivery Gate passes for the change (comments via `antislop-code`; screens via `antislop-ui`/`antislop-human`/`antislop-layoutmobile`; user-facing text via `antislop-copywriting`), with direction taken from `DESIGN.md`

**Backend**
- [ ] `tenant-aware-entity`: tenant_id, RLS, indexes, audit, effective dating, PII
- [ ] `headless-api`: canonical resources, list contract, problem+json, idempotency, OpenAPI updated and non-breaking
- [ ] `secure-coding`: permission, data scope, input validation, secrets, logs without PII
- [ ] `performance-and-scale`: indexes with the query, pagination, N+1 test, jobs for long work
- [ ] Events via the Modulith event registry; listeners idempotent; module imports only `api` packages; migration online-safe

**Web / mobile**
- [ ] `react-web` / `flutter-mobile`: SDK only, no business logic, tokens only, states covered
- [ ] `ui-review` run on changed screens; a11y clean; Tamil checked
- [ ] Offline behaviour stated and tested (mobile)

**Payroll / compliance**
- [ ] `payroll-component` / `statutory-rule`: rules as data, effective-dated, golden cases added or updated **with domain-expert review**

Review tone: comments say *what* and *why*, mark nits as `nit:`, and ask rather than assert when unsure. Reviews are returned within one working day.

## 5. ADRs

- Location `docs/adr/NNNN-short-title.md`, numbered sequentially, immutable once accepted (supersede with a new one).
- Format: **Context** (the forces), **Decision** (one paragraph, present tense), **Consequences** (good and bad), **Alternatives considered**, **Status** (Proposed / Accepted / Superseded by NNNN), date and deciders.
- Write one for: stack versions, deployable boundaries, data isolation choices, provider selections (face SDK, KYC, GSP, SMS), API versioning events, anything the initiation document §11 lists, and any deviation from the skills.
- Link the ADR from the PR that implements it and from the relevant doc section.

## 6. Environments and releases

- `dev` (every merge to `main`), `staging` (nightly from `main` plus on demand; load tests run here), `prod` (tagged releases).
- Versioning: services and clients share a **calendar version** `2027.03.1` per release train; APIs stay `/v1` until a breaking change (12-month parallel run for `/v2`, `Deprecation`/`Sunset` headers). Mobile app versions follow semver; `min_supported_version` in bootstrap enforces upgrades.
- Release checklist: changelog from Conventional Commits; migrations reviewed for rollback; feature flags default-off for unfinished slices; statutory rule-pack version noted; pilot release notes written in plain language for the customer.
- Hotfix: branch from the tag, PR to `main` first, cherry-pick to the release branch, tag `2027.03.2`.

## 7. Definition of done for a slice

A phase from `docs/DEVELOPMENT_PLAN.md` is done when **all** of these hold; a layer finishing early does not close it:

1. Every functionality slice of the phase is closed per `vertical-slice` §5: backend and web merged together (mobile/kiosk where the phase lists it), deployed to `staging`, and demoed together.
2. The phase's exit test and each slice's Playwright flow pass on `staging` through the real tenant login with the seeded reference tenants; `403`, data-scope and tenant-isolation tests exist for every new endpoint.
3. All mandatory slice tests (`testing-strategy` §2) exist and pass; golden cases signed off by the domain expert where payroll or compliance changed.
4. OpenAPI and SDKs published; breaking-change check clean.
5. Strings in en/hi/ta; native review done for ta; pseudo-locale clean.
6. `ui-review` has no Blocker or Major on the slice's screens; the `antislop` Delivery Gate report is PASS; usability check (3 users) done.
7. ADRs written for decisions taken; docs (`PROJECT_INITIATION.md` service table, `CLAUDE.md` commands) updated.
8. Observability: dashboards and alerts exist for the new paths; load test (if the slice touches month-end paths) within budget.
9. Security review for anything touching §2–§4 of `secure-coding`; dependency scan clean.
10. Release notes and a recorded demo for the pilot customer.

## 8. Day-to-day commands

Once the monorepo exists, `CLAUDE.md` lists the build, lint, test and run commands. Until then, nothing here should be assumed to exist; update `CLAUDE.md` in the same PR that adds a command.
