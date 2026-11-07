---
name: vertical-slice
description: Use when starting, scoping, implementing or closing any functionality in Allied Payroll — a new capability, screen, endpoint, report, import or rule. Defines the unit of delivery as one functionality cut through the backend API and the web screen together, proven end to end through the real tenant login, with the quality gates inside the slice rather than after it. Orchestrates headless-api, new-backend-module, tenant-aware-entity, react-web, testing-strategy, backpressure, secure-coding, ui-review and engineering-workflow; it does not replace them.
---

# Vertical slice

A functionality is done when a tenant user can sign in on the web and use it against the real backend, with the tests that prove it. Nothing ships as "backend only" or "UI with mocks". A phase in `docs/DEVELOPMENT_PLAN.md` is a set of these slices.

## 1. What a slice is

| A slice contains | A slice is not |
|---|---|
| One functionality a user can name ("approve work information for a site", "upload employees from Excel", "generate the ESIC challan") | A layer ("the payroll tables", "the employee list screen") |
| The OpenAPI contract, the backend module work, the generated SDK, the web screen, the end-to-end flow and the slice tests | A backend PR merged ahead of its screen, or a screen built against mocks and left waiting |
| Mobile or kiosk screens only where the plan gives this functionality one | Every layer on every slice |

Size: a slice is reviewable in 100 to 200 line increments and demonstrable in under five minutes. Split a functionality that needs more than a sprint into slices that each leave the product working.

## 2. The order of work

1. **Scope.** Name the functionality, the persona, the phase it belongs to, the Figma node and the resources it touches. For anything beyond a one-shot change, write the plan with `/rpi` (`engineering-workflow` §1): each phase of the plan names its verification.
2. **Contract first.** Add or change the canonical resources in `api/openapi/` (`headless-api`). No screen-shaped endpoints; the web composes from resources, `fields=` and `summary`. Breaking-change check clean. SDKs regenerated.
3. **Backend.** Module work per `new-backend-module`; tables per `tenant-aware-entity`; permissions declared with `@RequiresPermission` and data scope; audit and events; problem+json with message keys. The mandatory slice tests from `testing-strategy` §2 are written red first (`backpressure` §4).
4. **Web.** The feature per `react-web` from the Ready-for-dev Figma screen, design-system components and tokens only, SDK only, no business rules in the client. Storybook states; Testing Library tests with MSW from the OpenAPI examples.
5. **End to end through the real login.** The Playwright flow for the slice signs in through `/auth/login` as a seeded tenant user on the tenant subdomain, completes the task and asserts the outcome in the UI and, where it matters, through the API (`testing-strategy` §2 item 9). See §3.
6. **Gates inside the slice.** The `backpressure` ladder at each step; antislop Delivery Gate on the screen, copy and comments; `ui-review` with no Blocker or Major; `secure-coding` checklist for anything touching auth, tenancy or PII; i18n keys in en-IN, hi and ta.
7. **Close.** Demo against `dev` with the seeded tenants, the `engineering-workflow` §7 items that apply, docs and `CLAUDE.md` commands updated in the same PR.

Steps 3 and 4 may be built in parallel by two people on one branch or a stacked pair of PRs, but they merge together and the slice is not done until step 5 passes.

## 3. The login path is part of every slice

- Web sign-in is OIDC Authorization Code with PKCE through the backend's `web-channel` token handler. The tenant is resolved from the subdomain, the user authenticates at the tenant's Keycloak login, tokens stay on the server, the browser holds the HttpOnly cookie. `secure-coding` §1 has the details.
- **No bypass, ever.** No test login route, no "dev mode" user, no token injected into the browser, no cookie minted by a test helper. The Playwright flow goes through the real `/auth/login`, the real IdP login page and the real callback.
- **Fast runs without a bypass.** Playwright signs in once per worker with the seeded user and reuses the saved storage state for the rest of the suite. Sign-in itself has its own test.
- **Seeded users per tenant.** The demo tenants (`sentinel`, `nimbus-it`, `testing-strategy` §4) each have users for the personas the slice needs, with the permissions they would have in production. A slice that needs a new persona adds the user to the seed.
- **Authorization is tested in every slice.** For each new endpoint: a user without the permission gets `403` with the right problem code; a user scoped to another branch or site does not see the data; tenant A never sees tenant B. The web hides what the API forbids, and the test checks the API answer, not only the hidden button.
- A slice that changes the auth or tenancy path itself (new realm setting, cookie attribute, CSRF rule) is reviewed by the security owner before merge (`engineering-workflow` §4).

## 4. Quality is not a later step

- ADR 0002 formatters and linters green before every commit; `solid-clean-code` on every file touched; antislop on every screen, string and comment; `backpressure` results quoted in the handoff.
- A slice does not lower a gate to get through. A failing gate is a finding to fix inside the slice, or a reason to split the slice, never a reason to skip the check.
- The reviewer reads the plan, then the tests, then the code. The PR says which Figma node, which OpenAPI paths and which seeded user the flow uses.

## 5. Checklist (paste into the PR)

- [ ] Functionality named with persona, phase and Figma node
- [ ] OpenAPI contract first; breaking-change check clean; SDKs regenerated
- [ ] Backend module work with the mandatory slice tests, seen red first
- [ ] Web feature from the Figma screen, SDK only, states covered
- [ ] Playwright flow through the real `/auth/login` as a seeded tenant user, on the tenant subdomain
- [ ] 403, data-scope and tenant-isolation tests for every new endpoint
- [ ] Backpressure ladder clean; antislop Delivery Gate PASS; `ui-review` no Blocker or Major
- [ ] Strings in en-IN, hi, ta
- [ ] Demo on `dev` with seeded tenants; docs and commands updated
