# Allied Payroll — Figma Design Specification

| | |
|---|---|
| **Purpose** | Blueprint for building the Allied Payroll design system and product designs in Figma, to industry standards |
| **Audience** | Product designers, design technologists, front-end and Flutter engineers, product owners |
| **Inputs** | Concept prototype ([canvas](https://claude.ai/artifact/2f154UjeWQRyqwfYsYE3o1), 10 screens) · [UI_UX_STRATEGY.md](../UI_UX_STRATEGY.md) · tokens in [`design-tokens/tokens.json`](../../design-tokens/tokens.json) |
| **Status** | v1, ready for the designer to build the Figma library |

> The concept prototype set the direction: refined enterprise, calm and fast, with explainable numbers. Figma is where that direction becomes a **governed design system** and **developer-ready designs**. Whatever is finished in Figma is the authority; the canvas is reference only.

---

## 1. Standards and best practices we follow

| Standard | How it applies |
|---|---|
| **WCAG 2.2 Level AA** (W3C) | Mandatory for web portal, client portal, mobile and kiosk. Key criteria are in §9 |
| **W3C Design Tokens (DTCG) format** | All design values defined once in `tokens.json`, then imported into Figma Variables and generated for CSS and Flutter |
| **Material Design 3** | Base for Flutter (Android-first) components, motion and touch patterns, themed with our tokens. Not a stock Material look |
| **Apple Human Interface Guidelines** | iOS specifics: navigation and back behaviour, safe areas, Dynamic Type, 44 pt targets |
| **Nielsen's 10 usability heuristics** | Review checklist for every flow (status visibility, error prevention, recognition over recall, …) |
| **ISO 9241-110 dialogue principles** | Suitability for the task, self-descriptiveness, error tolerance. Used in payroll and approval flows |
| **Atomic design** | Library structure: foundations → components → patterns → templates → screens |
| **Indian conventions** | ₹ with lakh/crore grouping (₹12,34,567.00), dd-MM-yyyy display, April–March financial year, Indic script support |
| **DPDP Act 2023 / privacy by design** | Masked PII in every design, consent screens, data minimisation in the UI |
| *(If selling to government or PSU clients)* **GIGW 3.0** | Guidelines for Indian Government Websites: extra accessibility and bilingual requirements |

---

## 2. Figma workspace setup

### 2.1 Team and plan
- One Figma team: **Allied Payroll Design**.
- **Plan: Figma Professional** (decided). The free Starter plan is not enough: it allows only 3 design files, has no team libraries, and limits Dev Mode and the MCP server.
- **Seats:**
  - a **Full** seat for each designer
  - a **Dev** seat for the design technologist and any engineer who uses Dev Mode or the Figma MCP from Claude Code
  - everyone else (product, QA, stakeholders) views and comments for free
- **Professional gives us:** unlimited files and projects, published **team libraries**, variables with **up to 4 modes per collection**, Dev Mode, the Figma MCP server (Dev or Full seat), and full version history.
- **Professional does not give us**, so we work around it:

  | Missing (Organization plan and higher) | Our workaround |
  |---|---|
  | Branching and merging | Library changes go through a **staging copy** of the library file (§12) |
  | Code Connect | Component and property names match the code exactly (§3), so Dev Mode and the MCP map them by name |
  | Library analytics | Monthly manual audit with a lint plugin (§12) |
  | More than 4 variable modes | Every collection in §4 is designed with 4 modes or fewer. Extra demo brands are swapped into the Brand collection; they are not added as new modes |

  Revisit the Organization plan when the design team grows past about 3 designers or library changes start colliding.
- Check current seat prices at figma.com/pricing before buying. Annual billing is cheaper than monthly.
- Fonts: **IBM Plex Sans / Mono** and **Noto Sans** (Tamil, Devanagari, Telugu, Kannada, Malayalam) from Google Fonts. They're available in Figma by default, so no font upload is needed.

### 2.2 File architecture

| File | Type | Contents | Published as library |
|---|---|---|---|
| `00 · Allied DS — Foundations` ([open](https://www.figma.com/design/rTrXnvXxmDkh1xO8SBMRh3)) | Design | Variables, text and effect styles, grids, icons, logos. **v0.1 built:** 7 collections / 150 variables, 21 text styles, 3 effect styles, 6 grid styles, Cover, Color and Typography specimens | ✅ (publish pending) |
| `01 · Allied DS — Web Components` ([open](https://www.figma.com/design/yw6iyjD8ZhRZnsyat5VPEh)) — **v0.1 built:** 24 Lucide icons, 24 component sets + 4 components (Actions, Inputs, Selection, Navigation, Data display, Data table, Feedback, Overlays), theme-check frames on *Getting started* | Design | Web components, patterns and page templates (React/shadcn parity) | ✅ |
| `02 · Allied DS — Mobile Components` ([open](https://www.figma.com/design/MQ6CaSxdqrR9JkE0WAhuGO)) — **v0.1 built:** App Bar, Bottom Navigation, Check-in Button, Face Capture Frame, Check Item, Sync Indicator, Shortcut Tile, List Item, Month Chip, Language Switch, Payslip Hero Card, Expandable Section, Kiosk Result Overlay | Design | Flutter/M3-based components for phone, tablet and kiosk | ✅ |
| `10 · Product — Web Portal` ([open](https://www.figma.com/design/BopjPxgfVJL1FyElxulB4d)) — screens 1–5 built from library instances: Home, Month-close cockpit, Site attendance grid, Payroll run (Explain open + Approve confirm), Employees (3 selected) | Design | All admin and portal screens, flows, prototypes | — |
| `11 · Product — Mobile & Kiosk` ([open](https://www.figma.com/design/2UYGDRE7sRQ3X2G08aHzts)) — screens 6–7: Guard home (English + Tamil), check-in flow (aligning → matched → saved offline), payslip, supervisor team, kiosk idle → recognised → badge/PIN | Design | Employee, manager and kiosk app screens, flows, prototypes | — |
| `12 · Product — Client Portal` | Design | Client-facing portal (later phase) | — |
| `20 · Research & Flows` | FigJam | Personas, journey maps, user flows, interview notes, IA | — |
| `30 · Marketing & Brand` | Design | Website, sales deck visuals, illustrations (later) | — |
| `99 · Archive` | Design | Retired explorations | — |

Rules:
- Product files **only use library components and variables**. They never contain local copies or detached instances, except in clearly labelled "Exploration" sections.
- Each file has a **Cover** page (title, owner, status, last update) and uses a consistent thumbnail.

### 2.3 Page structure

**Foundations file:** `Cover` · `Changelog` · `Color` · `Typography` · `Spacing & Layout` · `Radius & Elevation` · `Motion` · `Iconography` · `Accessibility` · `Brand & White-label`

**Component files:** `Cover` · `Changelog` · `Getting started` · one page per category (`Actions`, `Inputs`, `Selection`, `Navigation`, `Data display`, `Data table`, `Feedback`, `Overlays`, `Domain`) · `Patterns` · `Templates` · `Deprecated`

**Product files:**
- `Cover` · `Flows`
- One page per module: `01 Home`, `02 Month close`, `03 People`, `04 Time & attendance`, `05 Payroll`, `06 Clients & sites`, `07 Billing`, `08 Inventory`, `09 Onboarding`, `10 Settings & admin`
- `Prototype` · `Exploration` · `Archive`

**Inside each module page:** use one **Section per screen**. Each section shows:
- the screen at desktop width
- the same screen at tablet and phone width (web)
- its **state variants**: default, loading, empty, error, no-permission, long-content
- the Tamil/pseudo-locale version where layout risk exists

Mark sections with Figma's **Ready for dev** status only after review (§11).

---

## 3. Naming conventions

| Item | Convention | Example |
|---|---|---|
| Variable | `group/subgroup/name`, lower camelCase leaf | `color/bg/surface`, `color/status/danger/fg`, `space/4` |
| Text style | `platform/role` | `web/h1`, `web/body`, `mobile/heroNumber`, `kiosk/clock` |
| Effect style | `shadow/size` | `shadow/md` |
| Component | Title Case, singular, matches code | `Button`, `Data Table`, `Status Badge` |
| Variant property | lowercase key, Title Case value | `variant=Primary`, `size=Md`, `state=Hover` |
| Boolean property | `has…` / `show…` | `hasIcon`, `showHelper` |
| Instance swap | `icon`, `leadingIcon`, `trailingIcon` | — |
| Text property | the label's purpose | `label`, `helperText` |
| Layer | Semantic, never "Frame 123" | `Header`, `Actions`, `Row/Cell` |
| Private sub-component | prefix `.` (hidden from assets panel) | `.Table Cell Base` |
| Screen frame | `Module / Screen / State @ breakpoint` | `Payroll / Run detail / Default @1440` |

Component names and property names **must match the React and Flutter component APIs** so Dev Mode and the Figma MCP map them one to one. Without Code Connect (Professional plan), matching names are the only link between design and code, so this rule is strict.

---

## 4. Variables (Figma Variables ⇄ `tokens.json`)

Import `design-tokens/tokens.json` using **Tokens Studio** or a DTCG-compatible variables import plugin. After import, Figma Variables are the working source. Changes are exported back to `tokens.json` through a PR, and CI regenerates the CSS and Flutter outputs.

| Collection | Modes | Contents | Scoping |
|---|---|---|---|
| **Primitives** | 1 (Value) | Colour ramps (navy, slate, blue, green, orange, red, violet + brand ramps), font families (Plex + Noto Indic), durations | **Hidden from publishing.** Designers never apply primitives directly |
| **Dimension** | 1 (Value) | `space/*` (0–64) and `radius/*`. Kept out of Primitives so they stay bindable after publishing | Gap/padding; corner radius |
| **Color (semantic)** | **Light**, **Dark** | `bg/*`, `text/*`, `border/*`, `status/{success,warning,danger,info,neutral}/{fg,bg,solid}`, `attendance/*` | Fill: bg/status; Text: text/*; Stroke: border/* |
| **Brand** | **Default**, Teal, Amber, Plum (demo tenants) | `accent/{default,hover,subtle,on}` | Fill and stroke |
| **Density** | **Comfortable**, Compact | `control/height/{sm,md,lg}`, `control/paddingX`, `table/rowHeight`, `table/cellPaddingX` | Width/height and gap |
| **Layout** | **Desktop**, Tablet, Mobile | Page margin, grid columns, gutter, nav width | Width and gap |
| **Script** | **Latin**, Indic | `font/family/sans` (IBM Plex Sans ↔ Noto Sans Tamil as the reference Indic face), and `lineHeight/{platform}/{role}` per text style (Indic = Latin × 1.2, rounded) | Font family and line height. Every text style except `web/code` is bound to these, so switching the mode re-flows the text |
| **Shadow colours** | (in Color) | `color/shadow/{sm,md,lg}` Light/Dark | Effect colour. The `shadow/*` effect styles bind to them |

Rules:
- **Semantic variables only** in components and screens. No raw hex, no detached values. Use a lint plugin (for example Design Lint) to catch violations.
- **No more than 4 modes per collection**, the Professional plan limit. The Brand collection holds Default plus 3 demo brands. A real tenant's brand is never added as a mode: tenant colours are applied at runtime from the tenant settings.
- **Brand mode only changes `accent/*`.** Status colours are never brandable.
- Every new brand colour must pass the contrast checks in §9 before it's added (`accent/on` text on `accent/default` ≥ 4.5:1).
- Dark mode is designed for the **web portal and mobile app**. The kiosk is always the dark theme for glare and power.

### 4.1 Colour usage map
| Use | Variable |
|---|---|
| Page background | `color/bg/canvas` |
| Cards, tables, panels | `color/bg/surface` + `color/border/default` |
| Primary button, links, focus ring, selected tab | `accent/default` (`accent/hover` on hover) |
| Selected row | `color/bg/selected` + 3 px inset bar in `accent/default` |
| Left navigation | `color/bg/nav`, `color/text/nav`, active `color/bg/navActive` |
| Status badge | `status/*/bg` + `status/*/fg` (+ dot in `status/*/solid`) |
| Attendance codes | `attendance/*` + matching `status/*/fg` text |
| Amounts negative | `status/danger/fg` **and** a "−" sign. Never colour alone |

---

## 5. Styles

### 5.1 Text styles (from `typography` in tokens)
| Style | Size / line | Weight | Use |
|---|---|---|---|
| `web/display` | 32 / 40 | 600 | Marketing-like hero (rare in app) |
| `web/h1` | 26 / 34 | 600 | Page title |
| `web/h2` | 20 / 28 | 600 | Panel and drawer titles |
| `web/h3` | 16 / 24 | 600 | Card and section titles |
| `web/bodyLg` | 16 / 24 | 400 | Long-form text, forms on tablet |
| `web/body` | 14 / 20 | 400 | Default UI text, table cells |
| `web/bodyStrong` | 14 / 20 | 500 | Names in rows, emphasised labels |
| `web/small` | 13 / 18 | 400 | Secondary lines, filters |
| `web/caption` | 12 / 16 | 400 | Meta, helper text |
| `web/overline` | 12 / 16 | 600, +0.04em, UPPERCASE | Table headers, group labels |
| `web/kpi` | 24 / 32 | 600, tabular | KPI tiles |
| `web/code` | 12 / 16 | Plex Mono 500 | IDs, codes, formulas in the explain panel |
| `mobile/title` · `mobile/body` (16/24) · `mobile/label` · `mobile/caption` · `mobile/heroNumber` | — | — | Mobile app; **body is never below 16 px** on mobile |
| `kiosk/clock` · `kiosk/name` · `kiosk/prompt` · `kiosk/body` | — | — | Kiosk: readable at 1 m |

- **All numbers use tabular figures** (OpenType `tnum`). Right-align currency columns.
- Each style has an **Indic variant** via the Script variable mode: Noto family and line height × 1.2.

### 5.2 Effect styles
`shadow/sm` (cards on hover), `shadow/md` (popovers, mobile cards), `shadow/lg` (dialogs, drawers). Prefer borders to shadows on data screens.

### 5.3 Layout grids
| Breakpoint frame | Grid | Margin | Gutter | Notes |
|---|---|---|---|---|
| Desktop 1440 × 900 | 12 columns | 32 | 24 | Left nav 232 px fixed; content fluid; max content 1280 for dashboards, full width for data |
| Laptop 1280 × 800 | 12 columns | 24 | 24 | Most common payroll-desk resolution |
| Tablet landscape 1024 × 768 | 8 columns | 24 | 16 | Nav collapses to icons (72 px) |
| Tablet portrait 768 × 1024 | 8 columns | 24 | 16 | Nav becomes drawer; tables → horizontal scroll with sticky first column |
| Phone 390 × 844 | 4 columns | 16 | 16 | Mobile app and responsive web |
| Kiosk 1280 × 800 landscape | 12 columns | 24 | 24 | Camera area 8 columns, side panel 4 |

Baseline spacing is a **4 px grid**, with layout steps on 8 px multiples (`space/*`).

### 5.4 Responsive rules (web)

These rules come from building screens 1–11 at 1024 and 390. Each tablet frame sits below its desktop frame in `10 · Product — Web Portal`, and each phone frame sits to the right of the tablet frame.

| Element | Tablet 1024 | Phone 390 |
|---|---|---|
| Navigation | **Side Nav Rail**: 72 px, icons only. Each item needs an aria-label and a tooltip | **Phone Header**: a menu button opens the nav drawer |
| Global context bar | Unchanged (4 chips fit) | Collapses to one context chip that opens a sheet |
| Data tables | Drop low-priority columns first (masked IDs, legal entity) and narrow the fixed columns | **Card list.** Each card has a title, a secondary line, a status badge **with its column name** ("Attendance · 2 unmarked") and a chevron |
| Attendance grid | 10-day window with a day-range pager. The guard column stays visible | **One-day view.** A date strip shows a status dot (always backed by text), and each status cell is a 56 × 44 target |
| Side and detail panels | Stack below the main content (client), or open as a drawer from the row (month close) | Open as a full-screen sheet (Explain) |
| Wizard | Step rail narrows to 240 px | Step rail becomes "Step n of 7" plus a progress bar and "Next: …" |
| Page actions | Stay in the page header | Primary action in a **sticky bottom bar**. An inline alert action becomes a full-width button below the alert |
| KPI tiles | Four in one row | 2 × 2 grid. Captions get a shorter phone copy |

The header and bottom bar stay fixed while the content scrolls. Phone frames are drawn at full length to show the whole scroll.

**Still local, to be promoted into `01 · Web Components` on the next library publish:** Side Nav Rail, Phone Header and Icon/Menu, all on the *Local components* page.

---

## 6. Component library

Build with **auto layout everywhere**, **component properties** (boolean, text, instance swap) instead of variant explosion, and **variables for every fill, stroke, radius, gap and padding**. Every component ships with:
1. A usage note in the component description: when to use, when not to, and the code name.
2. All interactive **states**: default, hover, pressed, focus-visible, disabled, loading, error, selected.
3. **Density** support (Comfortable and Compact).
4. Light and dark mode checks, and a long-text (Tamil and pseudo-locale) check.
5. Its accessibility annotation: role, name, keyboard behaviour (§9).

### 6.1 Web components (React + shadcn/ui parity)

| Category | Component | Key properties |
|---|---|---|
| Actions | **Button** | variant: Primary, Secondary, Tertiary, Danger, Link · size: Sm, Md, Lg · hasLeadingIcon, hasTrailingIcon · state incl. Loading |
| | **Icon Button** | variant, size · required `aria-label` annotation |
| | **Split / Menu Button** | items (instance swap) |
| Inputs | **Text Field** | size · state: Default, Focus, Error, Disabled, Read-only · showLabel, showHelper, hasPrefix (₹), hasSuffix |
| | **Money Input** | Indian grouping, ₹ prefix, right-aligned |
| | **Search Field** | with keyboard hint (Ctrl K) |
| | **Select / Combobox** | single, multi, async search, with "Create new" |
| | **Date Picker · Period Picker (Month/FY) · Date Range** | dd-MM-yyyy, FY shortcuts |
| | **Checkbox · Radio · Switch · Segmented Control** | with label and description |
| | **File Upload / Dropzone** | idle, dragging, uploading, error |
| Navigation | **App Shell** | nav: Expanded, Icons, Drawer · tenant logo slot |
| | **Side Nav Item** | state: Default, Hover, Active · badge |
| | **Global Context Bar** | entity, branch, period, client chips |
| | **Command Palette** | recent, results grouped by type, actions |
| | **Tabs · Breadcrumb · Pagination · Stepper** | — |
| Data display | **Status Badge** | tone: Success, Warning, Danger, Info, Neutral · hasDot |
| | **KPI Tile** | label, value, delta (up/down/flat), sparkline optional |
| | **Amount** | sign, currency, breakdown popover trigger |
| | **Masked Value** | masked, revealed (with "logged" hint), not available |
| | **Avatar · Entity Chip (employee/site/client)** | initials tone, size |
| | **Timeline / Approval Timeline** | step state: Done, Current, Waiting, Blocked |
| | **Empty State** | first-use, filtered-empty, error, no-permission |
| | **Chart frame** | bar, line, donut; one palette from status and neutral ramps |
| Data table | **Data Table** | density · selectable · sticky header and first column · column types: text, number, amount, badge, entity, actions · sort indicators · row states: Default, Hover, Selected, Disabled, Error |
| | **Filter Bar + Filter Chip** | chip: Empty, Applied (with ✕), "+ More filters" |
| | **Saved Views tabs** | with counts |
| | **Bulk Action Bar** | count, actions, clear |
| | **Attendance Grid Cell** | code: P, A, L, WO, H, OT, Unmarked · state: Default, Focus, Locked |
| Feedback | **Alert / Banner** | tone, dismissible, action |
| | **Toast** | tone, action, auto-dismiss note |
| | **Progress (bar, steps) · Skeleton · Spinner** | — |
| Overlays | **Dialog** | sizes · confirm (impact summary) · destructive (type-to-confirm) |
| | **Drawer / Side Panel** | Explain panel, quick view |
| | **Popover · Tooltip · Dropdown Menu** | — |
| Domain | **Month-close Step Cell**, **Blocker Card**, **Explain Panel line**, **Paysheet Summary**, **Import Wizard step** | Built from the above |

### 6.2 Mobile components (Flutter, M3-based)

| Component | Notes |
|---|---|
| **App Bar** (back, title, subtitle, actions) | 56 dp, safe-area aware |
| **Bottom Navigation** (4 items) | Icon + label always visible |
| **Check-in Button (hero)** | States: Ready, Verifying, Done, Offline-queued, Disabled (outside geofence) |
| **Face Capture Frame** | Oval guide: Idle, Aligning, Verifying, Success, Failed + hint chip |
| **Check Item** (geofence, light, liveness) | Pending, Pass, Fail |
| **Shortcut Tile** | icon tone, title, status line |
| **List Item / Team Member Row** | avatar, title, detail, status badge, actions |
| **Payslip Hero Card · Expandable Section** | amount, breakdown |
| **Month Chips · Segmented Control** | — |
| **Language Switch** | EN / தமிழ் / हिंदी … |
| **Sync Indicator** | Synced, Queued (n), Syncing, Failed |
| **Bottom Sheet · Dialog · Snackbar** | M3 behaviour |
| **Kiosk Result Overlay** | Recognised IN/OUT, Not recognised, Badge/PIN fallback |
| **Empty / Offline / Error states** | Plain-language, action-first |

Touch targets are **≥ 48 × 48 dp** on Android and 44 × 44 pt on iOS. Kiosk primary targets are ≥ 64 px.

### 6.3 Patterns and templates
- **Templates:** List page, Detail page, Form page, Wizard, Dashboard, Settings page, Month-close cockpit, Import flow, Approval inbox.
- **Patterns:** each is documented as a frame with annotations, mirroring the `ux-patterns` skill:
  - filtering and saved views
  - bulk actions
  - explainability
  - money-moving confirmation
  - blocker resolution
  - empty, loading and error states
  - offline on mobile

### 6.4 Icons
- One stroke icon set: **Lucide**, which matches the prototype's line style. The icon component is 24 px with 1.75 stroke and uses a `color` variable. Sizes are 16, 18, 20, 24 and 44 (hero).
- Icons are instance-swappable in every component. Icon-only buttons must carry a name annotation.

---

## 7. Screens to design (from the concept prototype, then roadmap)

| # | Screen | Prototype ref | Priority |
|---|---|---|---|
| 1 | Home (role-based attention queue, KPIs, close progress, margin) | Web · Home | P1 |
| 2 | Month-close cockpit + site detail and blocker panel | Web · Month close | P1 |
| 3 | Site attendance grid (shifts, unmarked, approve and lock) | Web · Site attendance | P1 |
| 4 | Payroll run detail + explain panel + approve/publish dialogs | Web · Payroll run | P1 |
| 5 | Employees list (saved views, filters, bulk, masked PII) + employee profile | Web · Employees | P1 |
| 6 | Guard home (EN/Tamil), face check-in flow, payslip, supervisor team | Mobile ×4 | P1 |
| 7 | Kiosk idle → recognised → fallback (badge/PIN) | Kiosk | P1 |
| 8 | Login (tenant subdomain), OTP, forgot access | — | P1 |
| 9 | Tenant onboarding wizard (industry profile, entities, locations, holidays, branding) | — | P1 |
| 10 | Clients & sites, contracts with rate card | — | P1 |
| 11 | Import wizard (template → validate → fix → commit) | — | P1 |
| 12 | Invoices, e-invoice IRN, receipts | — | P2 |
| 13 | Onboarding pipeline (candidate → KYC → bank → letters) | — | P2 |
| 14 | IT-company variants: location holidays calendar, CTC payslip, tax declarations | — | P2 |
| 15 | Boiler-engineering variants: work orders, timesheets, inventory issue | — | P3 |
| 16 | Client portal | — | P2 |

Each screen is delivered as: desktop + tablet + phone (web), all states, a prototype link for its flow, and dev annotations.

---

## 8. Prototyping standards
- One **flow starting point** per user journey, named after the job:
  - "Close August for a site"
  - "Guard checks in"
  - "Approve and publish payroll"
  - "Fix missing attendance"
- Use **interactive components** (button states, tabs, toggles, accordions) so prototypes behave like the product.
- Use **variables and conditional logic** for state that matters to the story: approved/not approved, unmarked count, language switch.
- Transitions: Smart Animate at 200 ms ease-out for in-page changes; instant for navigation. Respect the reduced-motion intent (no gratuitous motion).
- Test prototypes on real devices: phone via the Figma mobile app, and a tablet for kiosk and supervisor screens.

---

## 9. Accessibility requirements (WCAG 2.2 AA), designed in, not bolted on

| Criterion | Design rule |
|---|---|
| 1.4.3 / 1.4.11 Contrast | Text ≥ 4.5:1 (≥ 3:1 at 24 px+ or 18.66 px bold); UI parts and focus rings ≥ 3:1. Check every semantic pair in light **and** dark, and every brand mode |
| 1.4.1 Use of colour | Status always carries a text label or icon; negative amounts carry a sign |
| 1.4.4 / 1.4.10 / 1.4.12 Resize, reflow, text spacing | Layouts survive 200% zoom and reflow at 320 px; components grow with text (auto layout, no fixed heights on text containers) |
| 2.1.1 Keyboard | Annotate tab order and keyboard behaviour for grids, menus, dialogs and the command palette |
| 2.4.7 / **2.4.11** Focus visible, not obscured | 2 px focus ring in `border/focus` with 2 px offset; sticky headers and bars must never cover the focused element |
| **2.5.7** Dragging movements | Any drag (column reorder, roster assignment) has a click/tap alternative |
| **2.5.8** Target size (minimum) | ≥ 24 × 24 CSS px everywhere; our standard is **32 px desktop controls, 44–48 px touch, 64 px kiosk** |
| 3.3.1 / 3.3.3 Error identification and suggestion | Errors say what is wrong and how to fix it, next to the field and in a summary |
| **3.3.7** Redundant entry | Don't ask for information already given in the same flow (e.g. onboarding re-uses KYC data) |
| **3.3.8** Accessible authentication | OTP fields allow paste and autofill; no cognitive puzzles |
| **3.2.6** Consistent help | Help/support entry point is in the same place on every screen |
| 4.1.2 Name, role, value | Annotate accessible names for icon buttons, inputs and custom controls (attendance cells, step cells) |

Annotations use Figma's native **Dev Mode annotations**, or an annotation kit page. Required fields per interactive element: role, name, keyboard and screen-reader notes.

**Low-literacy and field use (mobile, kiosk):**
- icons always paired with short labels
- regional language by default
- one primary action per screen
- audio and haptic feedback noted in annotations
- glanceable status colours *plus* words

---

## 10. Content and localisation in Figma
- Write **real copy**, never lorem ipsum. Use the tenant terminology tokens (Site / Plant / Office) in brackets where the label varies: `Add [term.site]`.
- Keep a **pseudo-locale page** (+40% length) and **Tamil and Hindi versions** of the riskiest screens: nav, tables, payslip, check-in.
- Figma copy is the draft for the i18n `en-IN` keys. The design-technologist maps text layers to keys in Dev Mode annotations.
- Numbers use Indian grouping, dates dd-MM-yyyy, and periods "Aug 2026" / "FY 2026-27".
- PII in mocks is always **fictional** and **masked** as the product will show it.

---

## 11. Handoff to development
1. **Status flow:** Exploration → In review → **Ready for dev** (Section status) → Implemented → Verified.
2. **Dev Mode:**
   - Engineers inspect variables, which appear as token names rather than hex values.
   - Text styles and spacing come through as variable names.
   - Exports for illustrations only. Icons come from the Lucide package in code.
3. **Component-to-code mapping:** we have no Code Connect on Professional. Instead:
   - each library component's description links to its Storybook story and its Flutter widget path
   - names and properties match the code (§3)
4. **Figma MCP** (requires a Dev or Full seat): set up per Figma's MCP Server Guide.
   - **Primary:** the `figma@claude-plugins-official` Claude Code plugin. It provides the remote server (`https://mcp.figma.com/mcp`) and Figma's skills. Only the remote server can write to the canvas, which a Full seat needs outside drafts.
   - **Fallback:** the read-only desktop server, `figma-desktop` (`http://127.0.0.1:3845/mcp`) in `.mcp.json`.

   Claude reads selected frames, variables and component names to generate or update UI code, which is then checked with the `ui-review` skill.
5. **Definition of ready (per screen):**
   - all states designed
   - responsive frames done
   - annotations (accessibility, behaviour, data source) complete
   - copy final
   - prototype linked
   - reviewed by product and engineering
6. **Design QA:** after implementation, compare the build with the Figma frame using the `ui-review` rubric. File discrepancies as issues labelled `design-qa`.

---

## 12. Governance
- **Owner:** senior product designer (design system lead), with the design technologist as co-owner.
- **Library changes:**
  - We have no branching on Professional, so use a **staging copy** instead:
    1. Duplicate the library file as `[Staging] 00 · Allied DS — Foundations` (or the relevant component file).
    2. Make and review the change there with design and engineering.
    3. Apply the reviewed change to the real library file and publish it, with release notes on the `Changelog` page.
    4. Delete the staging copy.
  - Only the design-system owner publishes library updates.
  - Versioned with semantic versioning (`v1.3.0`), in step with `tokens.json` and the component code releases.
- **Contribution:** product designers propose new components as Exploration first. They're promoted to the library only after a second real use case appears.
- **Deprecation:** move to the `Deprecated` page with a pointer to the replacement, keep for one release cycle, then remove.
- **Health metrics:** a monthly manual audit of product files with a lint plugin (Professional has no library analytics), detached instances (target 0 in Ready-for-dev frames), token drift between Figma and code (CI check).
- **Cadence:**
  - weekly design critique
  - fortnightly design-system sync with engineering
  - usability test round per major flow before it is marked Ready for dev

---

## 13. Build plan (first 4 weeks for the designer)

| Week | Output |
|---|---|
| 1 | Foundations file: import tokens → Variables (all collections and modes), text and effect styles, grids, icons; accessibility contrast audit of all semantic pairs × light/dark × 4 brands |
| 2 | Web components: Button, inputs, selection, badges, KPI tile, App Shell, Context Bar, Data Table (+ cells), Filter Bar, Tabs, Dialog, Drawer, Toast, Empty State |
| 3 | Mobile components + kiosk; patterns and templates; rebuild prototype screens 1–7 from library components with all states |
| 4 | Screens 8–11, the prototype flows, a usability test with 5 users per persona (payroll exec, supervisor, guard), iterate, then mark the first screens **Ready for dev** |
