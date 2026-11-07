---
name: statutory-rule
description: Use when adding or changing any Indian statutory or location rule — EPF, ESIC, Professional Tax, Labour Welfare Fund, minimum wages/VDA, bonus, gratuity, TDS/income-tax, state holiday lists, Shops & Establishments/Labour Code limits, CLRA, GST/RCM/e-invoice thresholds. Rules are effective-dated data resolved by work-location jurisdiction, never hard-coded constants.
---

# Statutory and location rules

Background: `docs/PROJECT_INITIATION.md` §4.2 and `docs/INDUSTRY_PROFILES_AND_STRUCTURE.md` §6.

## Non-negotiables
1. **No statutory numbers in code.** Rates, ceilings, slabs, thresholds, due days and holiday lists all live in `compliance.statutory_rule_set` (or a dedicated master such as `minimum_wage_rate`, `holiday_master`) with `effective_from` / `effective_to`. The registry, its JSON schemas, the maker-checker publish flow and the income-tax model are designed in `docs/schema/compliance.md`; read it before touching a rule.
2. **Rules are resolved by jurisdiction and date.**
   - Central rules: PF, ESIC, bonus, gratuity, TDS, GST.
   - State rules: PT, LWF, minimum wages, S&E Act, holidays.
   - The state comes from the employee's **work location** on that date, never from the employee's home address or the tenant's head office (remote staff use the REMOTE location for their state).
3. **Rule changes never alter approved payrolls.** Payroll runs snapshot the resolved rule-set IDs. A retroactive change, such as a VDA revision, is applied through **arrears**.
4. **Platform-maintained packs.** Only the compliance owner publishes rule sets (`DRAFT` → `PUBLISHED`), and each one carries a `source_reference` (notification or circular number and date). Tenants can only add overrides, and those overrides must pass the rule's validation hook. A tenant can never go below a statutory floor.
5. **Get a domain-expert sign-off.** A PR that changes rule values or rule logic needs the payroll and compliance owner's review, plus updated golden tests.

## Steps
1. Identify `rule_type` and `sphere`:
   - `CENTRAL`, or `STATE` plus `state_code`.
   - For minimum wages, also: zone, skill category and scheduled employment.
2. Add or extend the **params JSON schema** for the rule type. For example, PT is `{frequency, slabs:[{from,to,amount,months?}], gender_exemptions?, annual_cap}`. Validate params against the schema when publishing.
3. Add the pack as a seed file under `backend/modules/compliance/src/main/resources/rule-packs/{rule_type}/{state_code|central}/{effective_from}[.variant].json` (loaded by the seed migration for fresh environments and used as the golden fixture). In a running environment the compliance owner drafts, simulates, submits and a second owner publishes through the admin screen; the CI job exports published packs back to the repo. **Never edit a published version.** Add a new version with `supersedes_id`; publishing closes the previous one.
4. Implement or extend the **calculator** as a pure function: `(ruleParams, inputs) → result + explanation lines`. No I/O, no clock. The date comes in as input.
5. Hook into resolution through `JurisdictionResolver` (work location → state, zone, registrations). Handle employees who worked in **several locations** in the period, following the documented policy (per-day evaluation for attendance and minimum wages; dominant location for PT and LWF unless the tenant configures otherwise).
6. **Holidays.**
   - National and state lists go into `holiday_master` per year.
   - Tenant calendars are built from them and attached to WorkLocations (or to client sites in the staffing profile).
   - Publishing a calendar validates it against the state's minimum holiday count.
7. **Tests (required):**
   - Golden cases for boundaries: exactly at the ceiling, ₹1 above, mid-period joiner, ESIC contribution-period continuation, the PT month with the special amount, an LWF half-year month.
   - The same employee transferred across two states within one month.
   - A date before and after the effective date.
   - Explanation lines are present, because they are shown on the paysheet audit view.
8. Update the rule summary table in `docs/PROJECT_INITIATION.md` §4.2 if the behaviour (not just a value) changed.

## Income tax (TDS on salary)

- One `INCOME_TAX` pack per tax year per regime (`variant` `NEW` or `OLD`), `effective_from` 1 April, `effective_to` 31 March. Slabs are keyed by age band inside the pack. A Budget change is two new packs; a mid-year amendment is a new version from the amendment date.
- Amounts and caps live in the pack under `deductionLimits[code]`; the identity of each deduction (category, regimes, proof, formula key, legal section per Act) is the platform catalogue `compliance.tax_deduction_type`. Adding a new deduction touches the catalogue once; changing its limit touches the pack.
- `IncomeTaxCalculator` is a pure function: project annual income, apply exemptions and deductions allowed in the regime with their caps, round, slabs, rebate with marginal relief, surcharge with marginal relief, cess, without-PAN rule, subtract TDS to date, spread over the remaining months. Every step emits an explanation line; the `tax_computation` row stores inputs, result, explanation and the pack id.
- Regime: default from the pack's `isDefaultRegime`, employee opt-in per year, locked after the tenant's cut-off month. A `COMPARISON` computation runs both regimes and changes nothing.
- Tenants may override only `/declarationCutoffMonth`, `/proofDeadlineMonth`, `/allowRegimeSwitchAfterFirstRun` and `/perquisitesProvided`.
- Golden cases per tax year and regime: income at the rebate limit and ₹1 above, each surcharge threshold with marginal relief, senior-citizen slabs, no PAN, mid-year joiner with previous-employer income, a declaration dropped at proof verification, a mid-year pack amendment.

## Known transitions to keep in mind
- **Labour Codes:** effective from 21 Nov 2025. Central and state rules are still being notified, which affects the "wages" definition (the 50% rule), gratuity for fixed-term staff and working-hour limits.
- **Income-tax Act, 2025:** replaces the 1961 Act from 1 Apr 2026. Section numbers, form names (Form 16 / 24Q equivalents) and terms like "tax year" change, so keep them as data and labels.
