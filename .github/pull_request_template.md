## What and why

<!-- One paragraph. Link the plan line, Figma frame or API contract this slice delivers. -->

## How to verify

<!-- Commands to run. Screenshots at 1440 and 1024 for web screens, device video for mobile. -->

## Checklists

Always
- [ ] Only the current slice, nothing more
- [ ] `solid-clean-code`: one responsibility per class, value objects, no framework in domain, typed errors
- [ ] `testing-strategy`: tests at the right layer; mandatory slice tests present
- [ ] `i18n-text`: no hard-coded user-facing strings
- [ ] Ubiquitous language; no industry if-checks
- [ ] `antislop` Delivery Gate passes

Backend (delete if not touched)
- [ ] `tenant-aware-entity`: tenant_id, RLS, indexes, audit, effective dating, PII
- [ ] `headless-api`: canonical resources, list contract, problem+json, OpenAPI updated and non-breaking
- [ ] `secure-coding`: permission, data scope, input validation, secrets, logs without PII
- [ ] `performance-and-scale`: indexes with the query, pagination, jobs for long work
- [ ] Events through the Modulith registry; listeners idempotent; imports only `api` packages; migration online-safe

Web / mobile (delete if not touched)
- [ ] SDK only, no business logic, tokens only, all states covered
- [ ] `ui-review` run on changed screens; accessibility clean; Tamil checked
- [ ] Offline behaviour stated and tested (mobile)

## Risk and rollback

<!-- Is the migration reversible? Is there a flag to turn the change off? -->
