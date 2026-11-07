---
name: react-web
description: Use when building or changing the Allied Payroll web app in frontend — React 19 + TypeScript + Vite with TanStack Query/Router/Table, shadcn/ui on the design tokens and the generated TypeScript SDK. Covers project structure, routing and the App Shell (left nav, utility rail, context bar, command palette, shortcuts), data fetching and forms, server-driven forms, i18n, accessibility, tablet layout, Storybook and testing. Pairs with ui-design-system (look), ux-patterns (behaviour), headless-api (no business logic in the client) and solid-clean-code (how code is written).
---

# React web app

The web app is one responsive SPA for admins, payroll and HR staff, tablet supervisors and (later) the client portal. It talks only to the backend API through the generated SDK, with the browser session handled by the backend's `web-channel` token-handler endpoints (`/auth/*`). It contains **no business rules**: no pay computation, no eligibility, no statutory logic, no permission decisions beyond hiding what the API says the user may not do.

> Versions are pinned in `frontend/package.json`; check them there, not here.

## 1. Structure

```
frontend/
├── src/
│   ├── app/                 # providers, router, App Shell, bootstrap, error boundaries
│   ├── features/            # one folder per bounded context / screen group
│   │   ├── payroll/
│   │   │   ├── runs/        # route components, hooks, components, tests, stories
│   │   │   └── components/
│   │   ├── attendance/
│   │   └── …
│   ├── shared/
│   │   ├── ui/              # design-system components (shadcn-based, token-driven)
│   │   ├── data/            # SDK client, query keys, problem+json handling
│   │   ├── forms/           # form primitives, server-driven form renderer
│   │   ├── i18n/            # ICU message loading, formatters (₹, dates, Indic)
│   │   └── shell/           # nav config, command palette, shortcuts, panels
│   └── test/                # MSW handlers, test utils, builders
└── .storybook/
```

- A **feature** owns its routes, hooks and components. Cross-feature imports go through `shared/` or a feature's explicit `index.ts`; ESLint `import/no-restricted-paths` enforces it.
- `shared/ui` is the only place that imports Radix/shadcn primitives; features import from `shared/ui`.

## 2. App Shell (from Figma App Shell v2)

- Layout: 232 px left **Side Nav** (collapsible to the 72 px rail with `[`), 56 px right **Utility Rail**, **Global Context Bar** on top (legal entity, branch, period, client, search), optional 32 px **Status Bar** (user preference, off by default).
- Nav groups come from a single `navConfig` filtered by **module flags and permissions from bootstrap**; a group with no visible item is hidden. Labels are terminology keys (`term.employee` → "Guards" for a security tenant).
- **Global context** (legal entity, branch, period, client) is URL state (`?le=…&branch=…&period=2026-10&client=…`), persisted per user, and passed to every list query. Changing it invalidates queries by key prefix.
- **Command palette** (`Ctrl K`): commands (navigate, quick actions), then entities from the search endpoint; keyboard-only operable.
- **Shortcuts**: `G then H/A/M/E/T/P/C`, `N`, `/`, `J/K/Enter`, `Shift P`, `[`, `]`, `?`, `Alt Q/N/R/P/J/A`, `Alt 1–9`. Implemented once in `shared/shell/shortcuts` with a scope stack (global → page → dialog); never fire while typing in a field; every button with a shortcut shows it in its tooltip.
- **Utility Rail panels** are overlays (360 px) with focus trap, `Esc` closes, state in URL hash so links survive refresh.

## 3. Routing

- TanStack Router with typed routes and loaders. Route files mirror the nav: `/payroll/runs`, `/payroll/runs/$runId`, `/people/employees/$employeeId?tab=statutory`.
- Guards: `requirePermission('payroll:read')` at the route level renders the 403 page; module-off routes 404.
- Loaders prefetch the route's primary query; mutations live in hooks, not loaders.
- Deep links carry global context; a link shared between users resolves to the viewer's scope (API decides).

## 4. Data

- **Only the generated SDK** (`api/sdk/typescript`, generated from `api/openapi/*.yaml`). Hand-written fetches are banned by lint. Regenerate in CI; a type error means the API changed.
- TanStack Query: query keys from a central factory (`qk.payroll.runs(ctx, filters)`); `staleTime` per resource class (reference data 10 min, lists 30 s, detail 0 with ETag); errors are `Problem` objects rendered by one `<ProblemAlert>` that maps `code` → localized message and field errors → form fields.
- Mutations send `Idempotency-Key` where the API requires it and `If-Match` from the entity's ETag; on `412` show the "changed by someone else" pattern from `ux-patterns`.
- Long work (`202` + job): `useJob(jobId)` polls with backoff and surfaces in the Background jobs panel.
- Money and dates arrive as strings; format with `shared/i18n` formatters (`formatInr('1189288.00')` → `₹11,89,288.00`); never `Number()` money for display math — the API returns totals.

## 5. Forms

- React Hook Form + zod schemas **derived from the SDK types** for static forms; the **server-driven form renderer** (`<DynamicForm schema={…}>`) for custom fields, onboarding and visit forms, using the field-metadata contract from `headless-api`.
- Field label, help, placeholder and error are message keys; required/visibility rules come from metadata (tenant configurable) and are re-validated by the API.
- Wizards (Setup, Import) keep step state in the URL and save drafts through the API ("saves as you go"), not in `localStorage`.
- Destructive or money-moving actions use the confirm dialog pattern with the explicit consequence sentence; approvals show what will be locked.

## 6. UI, tokens, accessibility

- Components come from `shared/ui` built on shadcn/Radix and styled only with **tokens** (`var(--color-…)`, Tailwind preset generated by Style Dictionary). No hard-coded colours, sizes or radii; `ui-design-system` is the rule set, `design-taste` the judgement, `ui-review` the gate.
- Tenant white-label: bootstrap supplies brand tokens; `ThemeProvider` sets CSS variables on `:root` per tenant; test with the light `brand/amber` theme too.
- Density: tables use the compact scale; forms the comfortable scale (tokens, not ad-hoc padding).
- **WCAG 2.2 AA**: every control has an accessible name; focus visible; dialogs trap focus; tables have proper headers and sort announcements; colour never the only signal; axe runs in Playwright.
- **Responsive**: desktop 1440 and tablet 1024 are first-class (supervisors approve on tablets); the 390 web layout exists for the few flows that need it, but field staff use the Flutter app.

## 7. i18n

- All strings via `t('payroll.run.approve.cta')` with ICU plural/select; `en-IN`, `hi`, `ta` shipped with the slice; bundles versioned and fetched per bootstrap; pseudo-locale in CI.
- Terminology keys for industry labels; master-data names come from `*_i18n` with locale fallback done by the API.
- Indic fonts (Noto) loaded for `hi`/`ta`; line-height ×1.2 for Indic text; test Tamil on every screen for clipping.
- Numbers: Indian grouping, ₹ attached, tabular figures in tables; dates `d MMM yyyy`; API gets ISO.

## 8. State

- Server state → TanStack Query. URL state → router search params. Per-user UI preferences (collapsed nav, status bar, saved views) → API-backed preferences with `localStorage` only as a cache. Ephemeral UI state → component state or a small Zustand store per feature when sharing is unavoidable. No global Redux-style store.

## 9. Testing and tooling

- Vitest + Testing Library for components and hooks (queries by role/name), MSW from OpenAPI examples; Storybook story per `shared/ui` component and per key feature state (empty, loading, error, long content, Tamil); Playwright flow per slice at 1440 and 1024 with axe, signed in through the real `/auth/login` as a seeded tenant user (storage state reused per worker; never a bypass, see `vertical-slice` §3).
- ESLint (typescript-eslint strict, react-hooks, jsx-a11y, import boundaries), Prettier, `tsc --noEmit` in CI. No `any`, no `// eslint-disable` without a reason comment.
- Performance budgets and virtualisation rules in `performance-and-scale` §5.

## 10. Checklist for a new screen

- [ ] Figma screen is Ready for dev; dev annotations read
- [ ] Route + permission guard + module flag; nav entry via `navConfig`
- [ ] Data through the SDK and query-key factory; global context applied; problem handling wired
- [ ] Forms from SDK types or server-driven metadata; message keys for all text in en/hi/ta
- [ ] Tokens only; responsive at 1440 and 1024; axe clean; keyboard path works with the shell shortcuts
- [ ] Empty, loading, error, partial and long-content states exist (stories)
- [ ] Tests: component, MSW, Playwright flow; `ui-review` run
