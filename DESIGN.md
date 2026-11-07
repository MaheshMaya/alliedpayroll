# DESIGN.md

Design direction for Allied Payroll, in the form the `antislop` filter reads. This file transcribes decisions already made in [docs/UI_UX_STRATEGY.md](docs/UI_UX_STRATEGY.md), [docs/design/FIGMA_DESIGN_SPEC.md](docs/design/FIGMA_DESIGN_SPEC.md) and `design-tokens/tokens.json`. Those remain the source of truth; change them first, then update this file.

## Identity

An Indian payroll and workforce platform for agencies that deploy people to client sites. Users are payroll executives at month end, site supervisors on a phone, guards and technicians at a kiosk, and client contacts in a portal. Money and compliance are at stake, so the product must read as exact and trustworthy.

## Personality

Refined enterprise: calm, precise, fast. Clarity over decoration. Explainable numbers, masked PII, a month-close cockpit and a command palette are the signature moments, not visual flourishes.

## Palette

- Semantic tokens only in code (`color.bg.surface`, `color.text.muted`, `color.status.*`).
- Light canvas on slate, dark canvas on navy. Both modes are first class and both must work fully.
- One accent per tenant. Default accent is blue 600; the presets are teal, amber and plum. Tenants upload a brand colour and the system derives an accessible OKLCH tonal palette.
- Status colours (success, warning, danger, info) are fixed and never brandable.
- No gradients, glows or glass in working screens. Brand moments are allowed on login, onboarding, empty states and marketing only.

## Typography

- Sans: IBM Plex Sans, with Noto Sans as fallback. Mono: IBM Plex Mono for amounts and identifiers.
- Indic scripts use the Noto Sans family for Tamil, Devanagari, Telugu, Kannada and Malayalam.
- Reason (R-06): Plex has Indic companions, tabular figures for ledgers and a quiet enterprise character. It is a chosen face, not a default.
- Currency in Indian digit grouping (₹11,89,288.00) in tabular figures.

## Mood

Quiet confidence. Generous but efficient spacing; comfortable and compact density switchable by token. Nothing on a working screen moves unless it reports a state change.

## Dials

`Dial: ENERGY 1 / RHYTHM 2 / MOTION 1`

- ENERGY 1: working screens stay calm. Login, onboarding and empty states may rise to 2.
- RHYTHM 2: list, detail, cockpit and form pages each have their own composition; sections inside a page are not uniform card grids.
- MOTION 1: 150 to 250 ms ease-out on state changes only; `prefers-reduced-motion` respected.

## Motion

Motion shows that something happened: a row added, an approval done, a sync complete. No decorative animation in working screens.

## Components and icons

- Web: shadcn/ui on Radix, themed by the tokens. Mobile: Flutter Material 3 with a ThemeExtension from the same tokens.
- Icons: Lucide, as shipped with shadcn/ui and mirrored in the Figma icon set. Reason (R-04): one icon set across web, Figma and Flutter outweighs a bespoke set; icons are chosen by meaning, and every icon button has an accessible name.

## Precedence

1. ADR 0002 formatters and linters decide code formatting.
2. The `ui-design-system` skill and the Figma libraries decide the look of product screens.
3. `antislop` is the filter and the Delivery Gate on everything: UI, copy, comments. Where this file asks for something antislop flags, name the element and the rule and ask before keeping or dropping it.

## Non-goals

No marketing-style landing pages inside the product. No dark-by-default. No copying the layout or branding of the reference product in `reference/`.
