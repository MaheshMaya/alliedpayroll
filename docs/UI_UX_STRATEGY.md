# UI/UX Strategy & Design System

Companion to [PROJECT_INITIATION.md](PROJECT_INITIATION.md). Allied Payroll is a **product we sell**. UI/UX is a primary differentiator in demos, trials and renewals, not polish added at the end.

---

## 1. Design goals

1. **Calm, trustworthy, fast.** Payroll and money need clarity and confidence, not decoration. The aesthetic is **refined enterprise**: precise typography, generous but efficient spacing, restrained colour, purposeful motion.
2. **Get the month closed with fewer clicks.** Measure success in task time, not screen count.
3. **Explain every number.** Every paysheet, invoice and statutory figure can be traced to its inputs with one click.
4. **One product, many shapes.** Industry terminology, enabled modules and tenant branding adapt the UI without forks (see [INDUSTRY_PROFILES_AND_STRUCTURE.md](INDUSTRY_PROFILES_AND_STRUCTURE.md)).
5. **Everyone included.** Desk staff use dense data screens. Guards and technicians, often in a regional language with limited literacy, use simple icon-first mobile screens. Kiosks show a single purpose, readable from a metre away.
6. **Accessible by default.** WCAG 2.2 AA, keyboard-complete web, readable Indic typography.

> **Figma build spec:** [design/FIGMA_DESIGN_SPEC.md](design/FIGMA_DESIGN_SPEC.md) covers the standards, file architecture, variables, components, accessibility and handoff. Tokens live in [`design-tokens/tokens.json`](../design-tokens/tokens.json). The concept prototype is on the [canvas](https://claude.ai/artifact/2f154UjeWQRyqwfYsYE3o1).

## 2. Lessons from the benchmark (what we will do better)

| Benchmark observation | Our approach |
|---|---|
| 10 top-level menus plus a "••" overflow; deep sidebars with 10–15 items | **Task-based navigation:** role-based home, grouped modules, global **command palette (Ctrl/⌘+K)** to jump to any employee, client, site, payroll or action; recent and pinned items |
| Payroll run fails with a generic "Error found" | **Month-close cockpit** with pre-flight checks: a guided checklist per client site or pay group (attendance → approve → payroll → approve → publish → pay → invoice → IRN) showing exactly what blocks the next step, with one-click fixes |
| Icon-only colour-coded row actions | Primary action as a labelled button; secondary actions in a labelled overflow menu; tooltips; consistent placement |
| Inconsistent date formats; raw codes everywhere | One date format per tenant; human-readable codes plus names; copy-to-clipboard for IDs |
| Full Aadhaar and bank numbers in lists | **Masked by default**; reveal requires permission, is audited, and shows a visible timer |
| Dashboards are mostly counts | **Actionable dashboards:** "needs your attention" queues (pending approvals, expiring contracts, unmarked attendance, statutory due dates) above KPIs |
| Bulk upload is a separate page with instructions text | An **inline import wizard** with live validation preview, error highlighting in a grid, and fix-in-place before commit |
| Separate pages for every filtered list | **Saved views** (filters + columns + sort) per user, shareable within the tenant |
| Desktop-only feel | Responsive web for tablets; dedicated Flutter app for field staff and kiosks |

## 3. Personas and their primary jobs

| Persona | Channel | Top jobs | UX priorities |
|---|---|---|---|
| **Payroll executive** | Web (desktop) | Run monthly payroll, fix exceptions, publish payslips, statutory files | Speed, keyboard, bulk actions, explainability |
| **Operations / branch manager** | Web, tablet, mobile | Deploy staff, approve attendance, cover absences with relievers, client relationships | At-a-glance site status, quick approvals, roster grid |
| **Accounts / billing** | Web | Generate invoices, IRN, receipts, outstanding, profitability | Accurate totals, reconciliation views, exports |
| **Owner / tenant admin** | Web, mobile | Margins, compliance health, growth | Executive dashboard, alerts |
| **HR (IT company)** | Web | Onboarding, leave and holiday calendars, TDS declarations | Clean forms, self-service adoption |
| **Field supervisor / engineer** | Mobile, tablet | Mark team attendance, site visits, incidents, material issue | One-hand use, offline, camera, minimal typing |
| **Guard / technician** | Mobile (often low-end Android), kiosk | Check in, view payslip, apply for leave, acknowledge letters | Regional language, icons, large targets, under 3 taps per task |
| **Employee (IT)** | Mobile, web | Payslip, leave, tax declarations | Consumer-grade polish |
| **Client contact** | Web portal | Attendance, invoices, compliance proof, visit reports | Simple, branded, read-mostly |

Validate these personas in **discovery interviews with 3–5 pilot customers per industry** before high-fidelity design.

## 4. Information architecture (web)

```
Home (role-based: attention queue + KPIs)
├── Month Close      (cockpit per client site / pay group: attendance → payroll → billing)
├── People           (applicants, candidates, employees, deployments, documents, letters, exits)
├── Time             (attendance, shifts & rosters, leave, holidays, timesheets)
├── Payroll          (runs, paysheets, arrears, benefits, payments, statutory)
├── Clients          (customers, sites, contracts, contacts, visits, incidents)        [module-gated]
├── Billing          (invoices, e-invoices, receipts, credit notes, profitability)      [module-gated]
├── Inventory        (items, stock, issues, purchase)                                   [module-gated]
├── Reports
└── Settings         (organisation, locations, policies, users & roles, integrations, branding)
```

- **Global context bar** (from the benchmark, refined): legal entity / branch / period / client. Compact, sticky, with clear "All" states. It persists in the URL.
- **Labels follow the tenant's terminology** (Site / Plant / Office…). Modules a tenant hasn't enabled don't appear at all.

## 5. Design system: "Allied DS"

### 5.1 Tokens (single source of truth)
- **Figma Variables → exported tokens (W3C Design Tokens JSON) → Style Dictionary** generates:
  - **web:** CSS custom properties + Tailwind theme
  - **Flutter:** `ThemeData` + a `ThemeExtension` (Dart)
- Repository: `design-tokens/`. CI fails if generated outputs are stale.
- **Token tiers:** primitive (palette, spacing scale) → **semantic** (`color.bg.surface`, `color.text.muted`, `color.status.success`) → component tokens. Code uses **semantic tokens only**.
- **Modes:** light and dark; density (comfortable / compact for data-heavy screens); **tenant brand**.

### 5.2 Tenant branding (white-label, a sales feature)
- A tenant uploads a logo and picks a primary brand colour. The system **generates an accessible tonal palette** (OKLCH-based), checks contrast against AA, and adjusts automatically when the chosen colour fails.
- Branding applies to the web, client portal, mobile app (runtime theme from bootstrap), kiosk, emails and PDFs.
- Status colours (success / warning / danger / info) are **not** brandable, so they keep their meaning.

### 5.3 Typography
- **UI typeface:** a highly legible humanist or grotesque sans with tabular figures (for amounts). Choose a licence-friendly family with a strong **Noto Sans Indic** pairing for Devanagari, Tamil, Telugu, Kannada and Malayalam. Finalise in the visual-design phase.
- **Numbers:** tabular lining figures in tables and amounts. Right-align currency. Indian grouping (₹12,34,567.00).
- **Indic scripts:** about 1.2× line height relative to Latin. Never truncate mid-cluster. Test with long Tamil and Malayalam strings.

### 5.4 Core components (built once, documented in Storybook)
- **App shell:** navigation, global context bar, command palette.
- **Data table:** server-side sort, filter and paginate; column chooser; saved views; row selection with bulk actions; sticky header and first column; inline edit; virtualisation for 10k+ rows; empty, loading and error states.
- **Filter bar** with chips and a "More filters" drawer.
- **Forms:** schema-driven from server metadata (custom fields), with inline validation, sections, sticky save bar and unsaved-changes guard.
- **Attendance grid:** employee × day × shift, keyboard entry of codes, paste from Excel, totals row, conflict highlighting.
- **Import wizard:** template → upload → validation grid with errors → fix → commit.
- **Approval timeline**, **status badge**, **amount** (with breakdown popover), **money input**, **period picker**, **entity pickers** (employee, client, site; searchable, async).
- **Explain panel:** a side drawer showing a calculation's lines and its sources.
- **Charts:** a consistent theme and accessible palettes (dashboards, profitability). Use **one** charting library.
- **Feedback:** toasts, inline alerts, a confirmation dialog for destructive or money-moving actions, and typing confirmation for irreversible ones.

### 5.5 Motion & micro-interactions
- Purposeful and fast: 150–250 ms ease-out. Motion shows state changes (row added, approval done, sync complete). No decorative animation in working screens.
- Respect `prefers-reduced-motion`.
- Brand moments are allowed on the login, onboarding wizard, empty states and the marketing site.

## 6. Web front-end stack (recommendation)

| Concern | Recommendation |
|---|---|
| Framework | **React + TypeScript + Vite** (SPA on the backend API, browser session via the `web-channel` token handler) |
| Components | **shadcn/ui on Radix primitives** (accessible, unstyled base, code we own) + Tailwind driven by our tokens |
| Data | TanStack Query (server state), TanStack Router, TanStack Table + virtualisation |
| Forms | react-hook-form + zod (zod schemas generated from OpenAPI where possible) |
| i18n | FormatJS (ICU) with namespaces |
| Charts | Apache ECharts (rich dashboards, good performance), or Recharts for simplicity. Decide once |
| Heavy grids | Evaluate **AG Grid** (commercial licence) for the attendance and paysheet grids, if TanStack Table hits limits |
| Workshop / docs | Storybook with a11y and interaction tests; visual regression (Chromatic or Playwright screenshots) |
| Testing | Vitest + Testing Library; Playwright E2E across desktop, tablet and pseudo-locale |

Flutter (mobile and kiosk) uses **Material 3 with our tokens** through a `ThemeExtension`. Components mirror the web names so design and code talk about the same things.

## 7. Process: how we keep quality "top-notch"

1. **Discovery:** pilot-customer interviews and site visits (guard check-in at a gate, a payroll executive's month-end), then journey maps.
2. **IA and wireflows** for the month-close cockpit, attendance and payroll first. These are the core value and the demo story.
3. **Design system first:** tokens and core components in Figma and Storybook before feature screens.
4. **High-fidelity prototypes** (Figma), **usability tested** with 5 users per persona before build.
5. **Design ↔ code handoff** through the **Figma MCP** (Claude reads frames, variables and components) and the `ui-design-system` / `ux-patterns` skills.
6. **UI review gate** on every UI PR (`ui-review` skill + Playwright MCP):
   - screenshots at desktop, tablet and phone widths
   - light and dark modes
   - `en`, `ta` and the pseudo-locale
   - axe accessibility scan
   - comparison against the Figma frame
7. **Measure:**
   - task time for month close
   - System Usability Scale (SUS ≥ 80 target)
   - Core Web Vitals
   - mobile check-in success rate and time
   - support tickets per 100 users

## 8. Tooling installed for this project

| Tool | Purpose | How it's configured |
|---|---|---|
| `frontend-design` (Anthropic plugin skill) | High-quality, distinctive front-end implementation. **Our `ui-design-system` skill takes precedence** for product screens; use the plugin's bold-aesthetic guidance only for marketing pages, login and empty states | Project plugin (`.claude/settings.json`) |
| **Playwright MCP** | Drive the running web app: screenshots at breakpoints, flows, a11y checks, visual review | Project plugin |
| **Context7 MCP** | Up-to-date docs for React, shadcn/ui, TanStack, Flutter packages and Spring | Project plugin |
| **Figma MCP** + Figma skills | Read designs, variables (tokens) and components; design-to-code; **write to the canvas** (build libraries and screens) | Project plugin `figma@claude-plugins-official` (remote server, sign in through `/plugin`). Fallback: read-only `figma-desktop` in `.mcp.json`. Needs a Figma Professional Dev or Full seat. See [FIGMA_DESIGN_SPEC.md](design/FIGMA_DESIGN_SPEC.md) §2.1 |
| **Dart & Flutter MCP** | Analyse, fix, run tests, inspect widget trees and hot-reload in the Flutter app | `.mcp.json` (`dart mcp-server`, Dart SDK 3.9+) |
| *Optional later:* Chrome DevTools MCP | Performance traces, network and console debugging | Install when performance work starts |
| *Optional later:* shadcn MCP | Browse and install shadcn/ui registry components | After the React stack is confirmed (`npx shadcn@latest mcp`) |
| *Optional:* Miro plugin | Journey maps and workshop boards, if the team uses Miro | Official marketplace |

## 9. Team
- **Product designer, senior** (systems thinking, data-dense enterprise UX, design systems). Join in P0.
- **UX researcher**, part-time or contract, for discovery and usability tests.
- **Design technologist / front-end engineer** who owns the design system code (tokens, Storybook, components).
- Optional **illustration and brand designer** for marketing, empty states and onboarding visuals.

These roles are added to [TEAM_SKILLS_MATRIX.md](TEAM_SKILLS_MATRIX.md).
