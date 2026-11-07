---
name: ux-patterns
description: Use when designing or implementing any Allied Payroll screen or flow — list pages, filters, forms, wizards, bulk import, approvals, month-close cockpit, dashboards, attendance grid, mobile field-staff flows, kiosk screens, empty/loading/error states and microcopy. Provides the standard interaction patterns and persona-specific rules.
---

# UX patterns

Background: `docs/UI_UX_STRATEGY.md` (personas, information architecture, benchmark lessons). Pair this skill with `ui-design-system` (visuals), `i18n-text` (copy) and `headless-api` (data).

## Before building a screen
1. Name the **persona** and their **job** (e.g. "Payroll executive closes August payroll for 40 client sites").
2. Write the **happy path in at most 5 steps**, then the exceptions (missing attendance, rule failure, no permission, offline).
3. Pick the pattern below. Don't invent a new pattern without a design review.

## Web patterns

**List page**
- Title, primary action, filter bar with chips and "More filters", saved views, DataTable, then bulk-action bar on selection.
- Filters and sort live in the URL.
- The default sort is the most useful one (e.g. pending items first). Rows open a detail view (side drawer for quick look, full page for heavy edits).

**Detail page**
- A header with identity (name, code, status), key facts and primary actions.
- Tabs for sections. An activity / audit timeline.
- Related records are linked, never duplicated.

**Forms**
- Schema-driven where custom fields apply.
- Validation runs on blur and on submit, and shows the server's localized errors on the matching field.
- Defaults are smart (current period, the context-bar selection).
- A sticky save bar and an unsaved-changes guard.

**Wizard** (onboarding, tenant setup, contract creation)
- 3–7 steps with a progress indicator. Users can save and resume, and can go back without losing data.
- A summary step before commit.

**Import**
- Use the inline ImportWizard. Validation results appear in a grid with errors highlighted, and rows can be fixed in place.
- Commit runs asynchronously with progress. The result summary links to created and updated records.

**Approvals**
- An inbox ("My approvals") grouped by type, with a preview drawer and approve/reject with a comment.
- Bulk approve is available for homogeneous items. The ApprovalTimeline appears on the record.

**Month-close cockpit**
- One row per client site or pay group. Columns are the steps: Attendance → Approved → Payroll → Approved → Published → Paid → Invoice → IRN.
- Each cell shows status, and the blocker with a one-click fix ("12 employees unmarked → Open attendance").
- **Never show a bare "Error found".** Always say what, where, and how to fix it.

**Dashboards**
- "Needs attention" queues sit above KPIs. Every number is clickable to its filtered list.
- Show comparisons (vs last month) only when they help a decision.

**Money-moving and irreversible actions** (approve or publish payroll, disburse, cancel IRN, write off)
- A confirmation dialog stating the impact ("Publish 412 payslips for Aug 2026 · Net ₹1,23,45,678").
- Irreversible actions require typing to confirm. Show what happens next.

**Explainability**
- Every computed amount opens the ExplainPanel: its formula, inputs, rule versions and source records.

## States (every screen)
| State | Pattern |
|---|---|
| Loading | Skeletons shaped like the content. No spinners on whole pages longer than 300 ms |
| Empty (first use) | An explanation, a primary action and a link to import. Illustration allowed |
| Empty (filtered) | "No results for these filters", with a clear-filters action |
| Error | What happened, plus a retry or fix action. Include the `traceId` for support. Never a raw stack trace or error code alone |
| No permission | Explain who can grant access. Don't show broken pages |
| Module disabled | The item is hidden from navigation. A deep link shows an upgrade or enable message for admins |

## Mobile patterns (Flutter, field staff)
- **Home is the action:** a big "Check in / Check out" button with status (shift, site, time), then the payslip, leave and letters tiles.
- At most 3 taps for any common task. One-handed reach for primary actions.
- Icon plus a short label. The regional language is the default. Use numbers and pictograms over long text.
- **Offline is normal:** show a queued or synced indicator per punch. Never block check-in on the network.
- The camera flow guides the user to good capture (face outline, lighting hint) and gives instant feedback (success haptic and sound).
- Errors explain what to do ("Move closer to the site gate", "Ask your supervisor to mark attendance").

## Kiosk patterns
- Full-screen and single-purpose: idle screen → face detected → result (name, photo, IN/OUT, time) shown for 3 s → idle.
- Readable from 1 m away. Audio and colour feedback, plus a text label.
- Badge/PIN fallback is always visible. There is no navigation chrome. The admin exit is hidden behind a long-press plus a server-issued code.

## Microcopy
- Use the user's words and the tenant's terminology (`term.*` keys). Start buttons with a verb ("Approve attendance", not "Submit").
- Use specific numbers and periods ("412 payslips for Aug 2026").
- Keep a neutral, respectful tone. Never blame the user.
