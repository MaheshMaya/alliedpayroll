---
name: industry-profile-feature
description: Use when a feature behaves differently for security/staffing agencies, project/field-service firms (e.g. boiler engineering with inventory), or corporate/IT companies — or when adding a module toggle, a new structural level, terminology, or a new industry profile template. Ensures variation is done via feature flags, strategies and terminology, never industry if-checks.
---

# Industry-profile-aware feature

Background: `docs/INDUSTRY_PROFILES_AND_STRUCTURE.md`. There are three reference tenants, and every feature has to make sense for all three, or be cleanly switched off for those it doesn't apply to:

- **Security agency:** Client → Site → Post → Shift; duty-based pay; billing from attendance.
- **Boiler engineering:** Client → Site → Equipment; work orders, AMC and timesheets; inventory is a core module; job costing.
- **IT company:** office locations in several states; holidays, PT and LWF per location; monthly CTC; heavy TDS; no client billing.

## Decide which mechanism applies

| The difference is… | Use |
|---|---|
| Whole module on or off (inventory, billing, PSARA, field ops) | **Feature flag / module toggle** in the `tenant` module: `tenantFeatures.isEnabled(Module.X)` |
| *How* something is computed or captured (pay basis, attendance mode, billing mode, OT rule, proration, payroll grouping, costing) | **Strategy interface**, resolved from configuration (tenant → pay group/engagement → employee) |
| What things are *called* (Site / Plant / Office, Duty / Man-day) | **Terminology key** (`term.site`) resolved per tenant and locale (see the `i18n-text` skill) |
| Which fields are shown or required | **Field configuration / custom fields** metadata |
| Default masters, components, reports | **Profile template pack** (`backend/modules/tenant/.../profiles/{profile}/v{n}/`) |
| Rules that differ by state or location | **Jurisdiction resolution** via WorkLocation, not industry (see the `statutory-rule` skill) |

## Rules
- **Never** branch on the industry profile in business code (`if (profile == SECURITY)`). Only the `tenant` module and the template loader may reference `IndustryProfile`. An ArchUnit rule enforces this.
- Use **canonical names** in code, APIs and tables: `client`, `site`, `site_sub_unit`, `engagement`, `work_location`, `org_unit`. Industry-specific names are only labels.
- The structural model is fixed: Tenant → LegalEntity → Branch / WorkLocation / OrgUnit, plus the optional Customer → Client → Site → SiteSubUnit. **Do not add hierarchy levels.** Put extra detail in attributes, tags or custom fields.
- A new strategy implementation:
  1. Add an enum value and implement the interface in the owning service's `domain` package.
  2. Register it as a bean keyed by the enum.
  3. Add it to the profile template(s) where it is the default.
  4. Add tests for it **and** a regression test confirming the other profiles' results are unchanged.
- **Disabling a module hides it but never deletes data.** APIs for a disabled module return `403` with code `module.disabled`.
- Any new menu item, report or dashboard widget declares the modules it requires, so the UI hides it automatically.

## Adding a new industry profile
1. Map the industry onto the existing model. Decide which modules are on, which strategies apply, and what the terminology is. If something genuinely doesn't fit, write an ADR before changing the model.
2. Create `profiles/{profile}/v1/` containing modules, strategies, terminology (all launch locales), default designations, components, leave types, document types, letter templates and reports.
3. Add seed data for a reference tenant in `common-test` and add it to the profile test matrix.
4. Update the module applicability table in `docs/INDUSTRY_PROFILES_AND_STRUCTURE.md` §5.

## Test matrix (required)
Run payroll, attendance and billing features against the security, boiler-engineering and IT reference tenants. The PR description must state the expected behaviour for each, or explain why the module is disabled for that profile.
