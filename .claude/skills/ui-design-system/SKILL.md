---
name: ui-design-system
description: Use when building or changing any Allied Payroll UI — web (React), client portal, Flutter mobile/kiosk, emails or PDFs — to apply the "Allied DS" design system: semantic tokens, tenant white-label theming, typography (incl. Indic), density, components, motion and accessibility. Takes precedence over generic frontend-design aesthetics for product screens.
---

# Allied DS: design system rules

Background: `docs/UI_UX_STRATEGY.md`. The product is sold on its UX. The look is **refined enterprise**: calm, precise, trustworthy and fast.

> **Precedence.** The `frontend-design` plugin skill pushes bold, unexpected aesthetics. For **product screens** (anything an operator, employee or client works in), these rules win. The bold, distinctive treatment is allowed only on the login, onboarding/empty-state illustrations and marketing pages, and must still use our tokens.

## Tokens: never hard-code
- Use **semantic tokens only**: `bg.surface`, `bg.subtle`, `text.default`, `text.muted`, `border.default`, `accent.*` (tenant brand), `status.success|warning|danger|info`, `space.*`, `radius.*`, `shadow.*`, `font.*`.
- **No hex values, px spacing or font sizes in component code.**
  - Web: Tailwind classes mapped to tokens, or CSS variables.
  - Flutter: `Theme.of(context).extension<AlliedTokens>()`.
- Tokens come from `design-tokens/` (Figma Variables → Style Dictionary). Generated files are never edited by hand. If a token is missing, add it to the source JSON.
- Support **light and dark modes** and **density** (comfortable / compact) through tokens, never by forking components.
- Form-control boundaries use `border.input` (≥3:1). `border.strong` and `border.default` are decorative only.
- Selection or active tints use `bg.selected`, never `accent.subtle` as a background. Brand tokens have no dark mode, and `bg.selected` follows the brand in light mode.
- Focus indicators on the dark nav (`bg.nav`) use `text.nav`, because `border.focus` is below 3:1 there.

## Tenant branding
- Only `accent.*` and the logo are brandable. Status colours are fixed.
- Assume any brand colour. Text on `accent` uses the generated `accent.on` token, which is contrast-checked.
- The brand applies to: the web shell, the client portal, the Flutter theme (from bootstrap), emails and PDF headers.

## Typography and numbers
- Use the token type scale (`font.size.*`, `font.weight.*`, `lineHeight.*`). Use tabular figures for amounts and tables.
- Amounts: right-aligned, Indian grouping (₹12,34,567.00), `Decimal` (never floating-point). Negative values carry a sign and colour, and **never rely on colour alone**.
- Indic scripts: use the Noto Sans family fallbacks with taller line height. Never clip vertically or truncate mid-character. Test with Tamil and Malayalam strings.

## Components
- Reuse the shared components before creating new ones:
  - App shell, global context bar, command palette
  - DataTable, FilterBar, SavedViews, ImportWizard, AttendanceGrid
  - SchemaForm (server-driven), EntityPicker, PeriodPicker, MoneyInput, Amount (with breakdown popover), ExplainPanel
  - StatusBadge, ApprovalTimeline, ConfirmDialog, Toast, EmptyState
- New shared components need a Storybook story (all states: default, loading, empty, error, disabled, long text, RTL/pseudo-locale) and an accessibility check.
- Web uses shadcn/ui + Radix primitives styled with our tokens. Flutter uses Material 3 widgets themed with our tokens. Names stay the same across both.

## Layout
- 4/8-point spacing scale. Content max width for forms (~720 px); full-width data screens.
- Breakpoints: phone < 640, tablet 640–1279 (touch targets ≥ 44 px), desktop ≥ 1280.
- Sticky table headers and the context bar. A sticky save bar on long forms.

## Motion
- 150–250 ms, ease-out, used only to communicate state changes. Respect `prefers-reduced-motion` (web) and the system animation scale (Flutter). No decorative animation on working screens.

## Accessibility (WCAG 2.2 AA, required)
- Contrast ≥ 4.5:1 for text, 3:1 for UI components and focus rings.
- Target size ≥ 24 px everywhere (2.5.8). Our standard is 32 px for desktop controls, 44–48 px for touch, and 64 px for kiosk. The focused element must never be hidden behind sticky bars (2.4.11). Every drag needs a click alternative (2.5.7). OTP fields allow paste and autofill (3.3.8).
- The token source is `design-tokens/tokens.json`, and the Figma structure is described in `docs/design/FIGMA_DESIGN_SPEC.md`. Component and property names must match the Figma library.
- Everything is keyboard reachable with a visible focus ring. Grids support arrow-key navigation.
- Every icon button has an accessible label (an i18n key). Status is never conveyed by colour alone.
- Form errors are linked to their fields and announced.

## Sensitive data
- PII (Aadhaar, PAN, bank account, UAN, phone) renders **masked** through the `<Masked>` component. Revealing requires a permission and is audited, and the value re-masks after a timeout.
