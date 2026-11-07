---
name: ui-review
description: Use after building or changing any Allied Payroll web or Flutter screen, or when asked to review UI/UX quality. Runs a visual + accessibility + i18n + responsiveness review using the Playwright MCP (web), Dart/Flutter MCP (mobile) and Figma MCP (design comparison), and reports issues against the design system and UX patterns.
---

# UI review gate

Use this before calling any UI work done. Standards: the `ui-design-system` and `ux-patterns` skills, plus `docs/UI_UX_STRATEGY.md`.

## 1. Gather
- The screen or flow, its persona and its job (from the PR or task).
- The Figma frame, if one exists. Use the **Figma MCP** to read the frame, its variables and component names. If the Figma MCP isn't connected, ask the user for a screenshot or export of the frame instead.

## 2. Capture (web, with the Playwright MCP)
Run the app locally against seeded reference tenants (security, boiler engineering, IT). Then for each key state (default, loading, empty, error, long data):
- Viewports: **1440×900** (desktop), **1024×768** (tablet landscape), **768×1024** (tablet portrait), **390×844** (phone).
- Modes: light and dark. Density: comfortable and compact (data screens).
- Locales: `en-IN`, `ta-IN` and the **pseudo-locale** (longer strings, RTL variant).
- Tenant brand: the default brand, plus one with an extreme brand colour (very light, e.g. yellow).
- Run an **axe accessibility scan** in the page and report violations.
- Walk the keyboard path: Tab through the primary flow and check focus order and visible focus.

## 3. Capture (Flutter, with the Dart/Flutter MCP)
- Run on an emulator with a low-end profile, plus a 10–11" tablet (kiosk landscape).
- Inspect the widget tree for hard-coded colours, sizes and strings (they must come from tokens or ARB).
- Check the text-scale settings at 1.3× and 2.0×, the `ta` and `hi` locales, and offline mode.

## 4. Check against the rubric
| Area | Pass when |
|---|---|
| Fidelity | Matches the Figma frame (spacing, type, components). Deviations are intentional and noted |
| Tokens | No hard-coded colours, spacing or fonts. Dark mode and brand colour both work |
| Hierarchy | One clear primary action. The page purpose is obvious within 5 seconds |
| Patterns | Uses the standard list, form, wizard, import or approval pattern. All states are present |
| Copy | i18n keys only, verb-first buttons, tenant terminology, no truncation in `ta` or pseudo-locale |
| Data | Amounts are right-aligned with tabular figures and Indian grouping; dates use the tenant format; PII is masked |
| Accessibility | 0 serious or critical axe violations; keyboard-complete; contrast passes; labelled icons |
| Responsive | Usable at tablet and phone widths (tables become cards or scroll horizontally with a sticky first column) |
| Performance | No layout shift on load; skeletons; list interactions feel instant (< 100 ms); large tables virtualised |
| Trust | Money-moving actions confirm their impact; errors say how to fix them; computed values are explainable |

## 5. Report
- A list of issues by severity: **Blocker** (broken, inaccessible, wrong data), **Major** (pattern or token violation, poor state handling), **Minor** (polish).
- Include the screenshot reference, the viewport and locale, the file and line, and the suggested fix.
- Fix the Blockers and Majors before marking the work done. List the Minors in the PR.
