---
name: payroll-component
description: Use when adding or modifying salary components, formulas, pay-basis/proration/OT strategies, rounding, arrears, benefits, salary holds or paysheet calculation logic in the payroll module. Covers component categories, wage-base flags, sandboxed formulas, BigDecimal rounding, run snapshots and golden tests.
---

# Payroll components and calculation

Background: `docs/PROJECT_INITIATION.md` §4.1 and `docs/INDUSTRY_PROFILES_AND_STRUCTURE.md` §4.

## Model
- **Category:** `EARNING` | `DEDUCTION` | `BENEFIT` | `EMPLOYER_COST`.
- **Flags:**
  - `is_statutory`, `taxable`
  - `part_of_gross`, `part_of_pf_wages`, `part_of_esi_wages`, `part_of_bonus_wages`, `part_of_min_wage_check`
  - `prorate`
  - `rounding` (inherits the payroll setting unless overridden)
- **Value source:** `FIXED` amount | `FORMULA` | `RATE_CARD` (from the contract, by designation) | `INPUT` (variable, per period) | `STATUTORY` (computed by the compliance module).
- **Names** are user-visible, so they need `name_i18n` (see the `i18n-text` skill). The component `code` is canonical and never translated.

## Rules
1. **Money:**
   - Use `BigDecimal` at scale 2 everywhere. Only ever create values with `new BigDecimal(String)` or `BigDecimal.valueOf(long, int)`, never from a `double`.
   - Intermediate values may keep a higher scale. Apply rounding **only** where the rounding setting says to: per component, or only on the total.
   - Modes are `OFF`, `ROUND` (HALF_UP to the rupee), `CEIL` and `FLOOR`.
2. **Formulas:**
   - Use the sandboxed expression evaluator only, with whitelisted variables (`BASIC`, `GROSS`, `PAID_DAYS`, `DUTIES`, `OT_HOURS`, `RATE`, other component codes …) and functions (`min`, `max`, `round`, `if`, `slab`).
   - No SpEL, scripting engines or reflection.
   - Evaluate in **dependency-graph order**. Detect cycles when the formula is saved and reject them.
3. **Pay basis by strategy:** `PER_DUTY`, `MONTHLY_CTC`, `DAILY_WAGE`, `HOURLY`, `PIECE_RATE`, with `ProrationDivisor` and `OvertimeRule`. Never switch on the industry (see the `industry-profile-feature` skill).
4. **Inputs are frozen.** At run start, snapshot:
   - approved work information
   - effective salary structure or rate card
   - statutory profile and resolved rule-set IDs
   - holds, arrears, loans, joining-fee instalments and inventory deductions

   Re-running a Draft payroll recalculates from a new snapshot. **Approved payrolls are immutable**: corrections go through arrears or a revision run.
5. **Explainability.** Every paysheet line records its basis, as in `"BASIC = 18,500.00 × 26/26"` or `"PF_EE = 12% × min(PF_WAGES 18,500, 15,000)"`. These lines feed the audit view and support queries.
6. **Minimum-wage guard.** After calculation, compare the components flagged `part_of_min_wage_check` against the resolved minimum wage for the location, skill category and date. Flag or block according to the tenant setting.
7. **Concurrency.** Hold one run per (tenant, grouping key, period) using a distributed lock. Runs are restartable and idempotent (Spring Batch partitioned by employee chunk).
8. **Statutory contributions** (PF, ESIC, PT, LWF, TDS) come from the compliance module's calculators (through its `api` package). The payroll module never re-implements their rules.

## Tests (required)
- A golden-file scenario workbook for each change, covering at least:
  - security per-duty pay with OT and a reliever
  - IT monthly CTC with LOP and TDS
  - an engineering technician with timesheet OT and a site allowance
- Rounding: each mode × per-component vs total-only.
- A Draft → re-run produces the same result from the same snapshot. Approved runs are not touched by later rule or structure changes.
- Performance: no N+1 queries. Track the 10k-paysheet run benchmark in CI (nightly).
