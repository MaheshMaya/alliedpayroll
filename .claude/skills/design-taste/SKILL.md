---
name: design-taste
description: Use whenever designing, composing or polishing any Allied Payroll screen in Figma or code — web, tablet, phone, kiosk, emails, PDFs — and before calling a prototype done. Encodes the craft judgement that separates a designed screen from a generated one: visual hierarchy, colour discipline (60-30-10, semantic-only colour, WCAG 2.2 AA + APCA contrast), typographic rhythm, spacing and alignment, restraint, and a measurable polish audit. Works alongside ui-design-system (the rules) and ui-review (the gate).
---

# Design taste: crafting, not generating

`ui-design-system` says *what is allowed*. This skill says *what is good*. A screen can use only tokens and library components and still look generated: everything the same weight, colour sprinkled for decoration, boxes inside boxes, uneven gaps, nothing to look at first. Apply these principles while composing, then run the audit in §9 before showing anyone.

The bar: calm, precise, fast — the feel of Linear, Stripe Dashboard and GOV.UK forms, not a template admin panel.

## 1. Every screen has one job and one focal point
- Name the screen's job in one sentence before placing anything ("Approve October payroll once the 3 blockers are clear").
- The **focal point** is the thing that answers the job — the blocker count, the net pay, the Check-in button. It gets the largest type or the only solid accent on screen. Everything else steps back.
- **Squint test:** blur the screen (or view at 25%). You should see, in order: the focal point, the primary action, the content groups. If you see a uniform grey field, hierarchy is missing.
- One **primary** button per view. Secondary actions are secondary/ghost buttons; rare actions go in a "More" menu.

## 2. Colour discipline
- **60-30-10.** ~60% neutral surface (`bg.canvas`/`bg.surface`), ~30% structure (text, borders, `bg.subtle`, the dark nav), ≤10% accent and status. If a screen looks colourful, it is wrong.
- **Colour carries meaning, never decoration.**
  - `accent` = interactive or selected: primary button, links, focus, the active nav item, the selected row. Never on headings, never as a card fill, never on icons that do nothing.
  - `status.*` = state only: success, warning, danger, info, neutral. A green number must mean "good", not "money".
  - At most **one solid saturated fill** competing for attention per viewport (normally the primary button). Status uses the subtle `bg` + strong `fg` pair; solid status fills are reserved for a single critical banner or a kiosk result screen.
- **Grayscale test:** desaturate the screen. Every state must still be readable through text, icon or shape (WCAG 1.4.1). Red/green pairs always carry an icon or label.
- **Contrast** (measure, don't eyeball):

  | Element | WCAG 2.2 AA | APCA target (secondary check) |
  |---|---|---|
  | Body text < 18 px | ≥ 4.5:1 | Lc ≥ 75 (14 px), ≥ 60 (16 px+) |
  | Large text ≥ 24 px, or ≥ 18.66 px bold | ≥ 3:1 | Lc ≥ 45 |
  | Captions, placeholders, disabled hints that convey info | ≥ 4.5:1 | Lc ≥ 75 |
  | Control boundaries, focus rings, icons that convey meaning | ≥ 3:1 | Lc ≥ 30 |
  | Text on any tint (badge, alert, selected row, attendance cell) | ≥ 4.5:1 against **that tint** | — |

  Our pairs that pass: `text.default`/`text.secondary`/`text.muted` on `bg.surface`, `bg.canvas` and `bg.subtle`; `status.*.fg` on `status.*.bg`; `text.nav` on `bg.nav`; `accent.on` on `accent.default`. Anything else must be checked. `border.default`/`border.divider` are decorative and never the only boundary of an input.
- **Dark nav, light work area.** Don't add a third surface tone; depth comes from `bg.canvas` vs `bg.surface` plus one hairline.
- Brand colours are tenant-supplied: design with the default blue, then check that the screen still works with `brand/amber` (light brand) — nothing may depend on the brand being dark.

## 3. Typography and rhythm
- Use only the token type scale. **At most three text levels per region** (e.g. h3 / body / caption) and **two weights** (400 and 600; 500 only for `bodyStrong`/table emphasis).
- Hierarchy comes from size, weight and colour together — not from bold everywhere. If more than ~20% of the text on a card is semibold, nothing stands out.
- Text colour ladder: `text.default` for content and values, `text.secondary` for labels, `text.muted` for metadata and helper text. Never lighter than `text.muted` for anything a user needs to read.
- Minimums: web 12 px (caption) at every breakpoint including the 390 web layout; Flutter app 13 px (caption); kiosk 18 px. Body is 14 px on web and 16 px in the app.
- **Line length** 45–90 characters for prose (helper text, alerts, empty states). Wider than ~720 px of prose is a layout bug.
- Overline / all-caps only for small section labels, with the token's 0.04em tracking. Never all-caps sentences or buttons.
- **Numbers:** tabular figures, right-aligned in columns, Indian grouping, consistent decimals within a column, the ₹ sign attached. Totals row in 600 weight with a top rule — not a coloured fill.
- Sentence case everywhere ("Save and continue", not "Save And Continue").
- Indic text gets ×1.2 line height and must never clip — check Tamil on every screen.

## 4. Spacing, grouping and alignment
- 4/8 grid only (4, 8, 12, 16, 24, 32, 48). No 10, 14, 18, 22 gaps.
- **Proximity:** the gap *inside* a group is always smaller than the gap *between* groups (label→field 4–8, field→field 16, section→section 32). If they're equal, groups dissolve.
- **One left edge.** Titles, field labels, table first columns and buttons share vertical alignment lines. Check with a vertical guide at the content's left edge — anything 1–4 px off is worse than 16 px off.
- Consistent container padding: cards 24 (desktop) / 16 (phone); table cells 12 horizontal; dialogs 24.
- Balance: one dominant column and supporting columns (2:1 or 3:1), not equal thirds of equal weight.
- Leave whitespace on purpose. A dense data table is fine; a dense *form* or *dashboard header* is not.

## 5. Restraint — remove before you add
- **No boxes in boxes.** Maximum two nested containers (page → card). Inside a card, group with spacing and a heading, not another bordered box.
- Use either a border *or* a shadow *or* a fill change to separate — never all three. Shadows are for floating layers (menus, dialogs, sheets, toasts); cards on the page use a 1 px `border.default` and no shadow.
- One radius family: 6 controls, 8 cards, 12 sheets/dialogs, full for pills. Don't mix 4 and 10.
- Icons only where they speed recognition (nav, status, file types, actions without room for a label). One stroke weight, 16/20/24 px, `text.muted` unless interactive or a status.
- Badges are for state, not for labels or counts that a plain number would show.
- After composing, do a **"remove one thing" pass**: delete the least useful element (a divider, a redundant label, a decorative icon, a second CTA). Repeat until removing anything hurts.

## 6. Content is design
- Real, plausible, fictional data (Indian names, sites, amounts in ₹ with Indian grouping, realistic dates). No lorem ipsum, no "John Doe", no round 1,000s. Never real names from `reference/` screenshots.
- Include the hard cases on purpose: the long name, the zero, the negative, the awaiting state, the Tamil string.
- Microcopy: verb-first buttons, errors that say how to fix it, numbers that explain themselves (a link to "Why?" beats a tooltip). Cut every word that doesn't change what the user does.

## 7. States make it feel finished
Every view shows (or has a frame for): default, loading (skeleton, not spinner), empty (one sentence + one action), error (what happened + what to do), partial/awaiting, and long content. Disabled buttons say why, nearby.

## 8. Form factor taste
- **Desktop:** density is a feature. Keyboard-first, sticky headers, side panels over page jumps.
- **Tablet:** same information, fewer columns; touch targets ≥ 44 px.
- **Phone:** one task per screen, thumb-zone primary action (sticky bottom bar), cards over tables, no horizontal scroll, 16 px side margins.
- **Kiosk:** read from 1 m. One instruction, one huge target (≥ 64 px), results in full-screen colour with an icon and a word, auto-return.

## 9. The polish audit (run before "done")
Do this on every frame you touched, at 100% zoom, then fix and re-screenshot.

1. **Five-second test:** can you state the screen's job and its next action?
2. **Squint test** (§1) and **grayscale test** (§2).
3. **Contrast scan:** compute every text node against its actual backdrop; nothing below the table in §2. In Figma, use a script that walks text nodes, resolves the nearest ancestor solid fill, and computes the WCAG ratio — flag anything under 4.5 (or 3 for ≥ 24 px / ≥ 18.66 px bold).
4. **Token scan:** no unbound solid fills or strokes on product frames (except image fills and intentional illustration); no text without a text style.
5. **Rhythm scan:** auto-layout gaps and paddings are on the 4/8 grid; no font sizes off the scale; no text below the minimum.
6. **Overflow scan:** no text whose bounds exceed its parent; no truncated labels; no clipped Indic glyphs.
7. **Alignment scan:** one left edge per column; numbers right-aligned.
8. **Restraint scan:** nested bordered containers ≤ 2; ≤ 1 solid saturated fill per viewport; no decorative colour.
9. **States and data:** hard cases present, real-looking data, Indian formats.

Report what you found and fixed as Blocker / Major / Minor (same scale as `ui-review`). A screen isn't done while any Blocker or Major remains.
