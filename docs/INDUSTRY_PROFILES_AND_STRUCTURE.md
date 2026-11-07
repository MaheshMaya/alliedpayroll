# Industry Profiles & Configurable Organisation Structure

Companion to [PROJECT_INITIATION.md](PROJECT_INITIATION.md). This document explains how a single multi-tenant codebase serves tenants whose **organisational structure, attendance, pay and billing models differ by industry**, and how **state- and location-level Indian rules** (holidays, PT, LWF, minimum wages, Shops & Establishments) are resolved.

---

## 1. Design principle

> **One data model, many shapes.** Industry differences are expressed through **configuration, metadata and strategy plug-ins**, never through per-industry forks, per-tenant code branches or `if (industry == SECURITY)` checks scattered in business logic.

Three mechanisms carry all variation:

| Mechanism | What varies | Example |
|---|---|---|
| **Industry profile (template)** | Which modules are on, structure levels and their labels, default masters, default strategies | Security → Clients/Sites/Posts/Shifts on; Inventory off |
| **Strategies selected by configuration** | How attendance is captured, how pay is computed, how clients are billed | `PayBasis = PER_DUTY` vs `MONTHLY_CTC` vs `HOURLY_TIMESHEET` |
| **Jurisdiction resolution by work location** | Which statutory rules and holidays apply to a person on a date | Employee at a Bengaluru office → Karnataka PT, Karnataka LWF, Karnataka holiday list |

The profile is a **starting template applied at provisioning**. After that, the tenant owns its configuration and can enable or disable modules, rename levels, and add structure levels within the limits below.

---

## 2. Reference tenants

### 2.1 Security agency (staffing profile)
A security firm supplies guards and supervisors to many clients. Each client has one or more sites (factory, office tower, residential society, hospital), and each site has posts (main gate, parking, reception) manned in shifts.

```
Tenant: Security agency
└── Legal entity (PSARA licence per state, PF/ESIC codes, GSTIN per state)
    └── Branch (agency office, e.g. Chennai / Hyderabad / Bengaluru)
        └── Client (customer organisation, e.g. a manufacturing group)
            └── Site (plant A, plant B …)            ← work location
                └── Post (main gate, store, patrol)  ← optional sub-level
                    └── Shift (06–14, 14–22, 22–06)
Contract: Client/site ↔ manpower rate card (designation × posts × shift × bill rate × wage breakup)
Deployment: Guard ↔ site ↔ post ↔ designation ↔ shift (effective-dated); relievers cover absences
Attendance: duty/shift-based (P, A, WO, OT duty, reliever), face/QR/kiosk/supervisor marking
Payroll: one run per site per month, pay = duties × rate (+ OT duties), minimum-wage guarded
Billing: monthly GST invoice per site from approved attendance (+ service charge, statutory reimbursement; RCM where applicable)
Compliance specials: PSARA, police verification, CLRA licence per site, uniform/ID issue
```

### 2.2 Boiler engineering company (project/field-service profile)
An engineering firm installs, commissions and maintains boilers and pressure equipment for industrial clients. Each client has several plant sites, each site has equipment, and work is executed as projects, work orders and AMC (annual maintenance contract) visits. Spares, consumables and tools are tracked in inventory.

```
Tenant: Boiler engineering company
└── Legal entity
    └── Branch / Regional office
        ├── Warehouse / Central store            ← inventory location
        └── Client
            └── Site (plant)                     ← work location; site store (optional)
                └── Equipment / Asset (boiler #1, serial, capacity, statutory inspection due)
Engagement: Project / Work order / AMC (scope, milestones, SLA, billable materials)
Deployment: Engineer/technician ↔ project/work order ↔ site (date range; travel)
Attendance: timesheet hours per work order + site check-in (geo); OT in hours; travel days
Payroll: monthly CTC for staff + site allowance / per diem / OT hours; contract labour per day where used
Billing: milestone, work-order, AMC periodic and material invoices (HSN/SAC by line), e-way bill for goods movement
Inventory (CORE for this profile): items (spares, consumables, tools), warehouses + site stores,
  purchase → GRN → stock, transfer to site, issue to work order (job costing), returns, tool custody, reorder levels
Job costing: labour (from timesheets × cost rate) + materials issued + expenses → project margin
```

### 2.3 IT services company (corporate profile)
An IT firm has offices in several Indian cities and states. There are no client sites for payroll purposes. Employees belong to an office location, and **holidays, PT, LWF and Shops & Establishments rules differ by state and location**.

```
Tenant: IT company
└── Legal entity (one or more; TAN for TDS)
    └── Office location (Bengaluru – KA, Pune – MH, Hyderabad – TS, Chennai – TN, Noida – UP, Remote)
        ├── Holiday calendar (national + state + location-specific + optional/restricted quota)
        ├── Statutory jurisdiction (PT state, LWF state, S&E Act, ESIC sub-code)
        └── Department → Team (reporting manager), Grade/Band, Cost centre
Attendance: daily check-in (web/mobile/biometric), WFH, leave & holiday driven; shifts optional (support teams)
Payroll: monthly CTC structure (basic, HRA, special allowance, flexi benefits, variable pay), LOP from leave/attendance
Compliance: TDS on salary (regime choice, declarations, proofs, Form 16/24Q), PF, ESIC (few eligible), PT by office state, LWF, gratuity, bonus eligibility
Billing: none in the payroll product (optional project billing module if they bill time-and-material clients)
```

### 2.4 Other likely tenants (same model)
| Tenant type | Closest profile | Notes |
|---|---|---|
| Housekeeping / facility management | Staffing | Same as security, without PSARA; consumables inventory common |
| Manufacturing contractor / contract labour | Staffing | CLRA heavy, piece-rate pay basis |
| Hospital / hospitality | Corporate + shifts | Rotational shifts, night allowances |
| Construction | Project/field-service | Project sites, daily-wage labour, BOCW cess |
| Retail chain | Corporate + shifts | Store = location, S&E Act per store |

---

## 3. Generic structural model

### 3.1 Fixed core (every tenant)
```
Tenant ─┬─ LegalEntity ─┬─ StatutoryRegistration (PF, ESIC, ESIC sub-code, PT, LWF, GST, TAN, PSARA, CLRA, S&E)
        │               └─ BankAccount
        ├─ Branch (tenant's own operating offices; may equal office locations)
        ├─ WorkLocation  ◄── every employee is attached to exactly one primary work location per date
        ├─ OrgUnit tree (Department / Team / Division — optional, configurable depth)
        ├─ Designation, Grade/Band, CostCentre
        └─ HolidayCalendar, LeavePolicy, AttendancePolicy, PayGroup
```

### 3.2 WorkLocation is the anchor for location-dependent rules
| Field | Purpose |
|---|---|
| `type` | `OWN_OFFICE`, `CLIENT_SITE`, `PROJECT_SITE`, `WAREHOUSE`, `REMOTE` |
| `address`, `state_code`, `district`, `pin` | Jurisdiction resolution |
| `geo_point`, `geofence_radius_m` | Mobile check-in validation |
| `holiday_calendar_id` | Location holidays |
| `pt_jurisdiction_state`, `lwf_jurisdiction_state` | Defaults to `state_code`, can be overridden |
| `min_wage_zone` | Minimum-wage zone/area class for the state |
| `esic_sub_code_id`, `pt_registration_id`, `lwf_registration_id`, `se_registration_id` | Which registration the employee's contributions are filed under |
| `timezone` | IST by default; stored explicitly for future overseas offices |
| `client_site_id` (nullable) | Set when the location is a client site |

For **remote employees**, the jurisdiction follows the state of the declared work-from address, using a `REMOTE` location per state.

### 3.3 Optional customer/engagement layer (module: `CUSTOMERS`)
```
CustomerOrganization (legal entity of client; GSTIN per state)
└── Client (billing unit)              — optional ClientGroup above
    └── Site  (= WorkLocation of type CLIENT_SITE)
        └── SiteSubUnit (configurable: Post | Area | Equipment/Asset | Line)   — optional
Engagement (polymorphic by profile):
    ManpowerContract   (staffing)   – rate card, posts, shifts, billing terms
    ProjectWorkOrder   (projects)   – scope, milestones, AMC schedule, materials
    —                  (corporate)  – not used
Deployment: Employee ↔ Engagement ↔ Site ↔ (SubUnit) ↔ Designation ↔ Shift, effective-dated
```

### 3.4 Configurable hierarchy levels and labels
Profiles name levels differently. The **data model stays fixed**. Labels come from the tenant's terminology map (see [LOCALIZATION_I18N.md](LOCALIZATION_I18N.md) §5):

| Canonical concept | Security | Boiler engineering | IT company |
|---|---|---|---|
| `client` | Client | Customer | — (hidden) |
| `site` | Site | Plant / Project site | — |
| `site_sub_unit` | Post | Equipment | — |
| `work_location` | Site | Site / Workshop | Office |
| `engagement` | Contract | Work order / AMC | — |
| `duty` | Duty | Man-day | Day |
| `org_unit` | Branch | Division | Department / Team |

Rules:
- Canonical keys are used in code, APIs and database. Labels are presentation only.
- The `site_sub_unit` level is optional. Further levels below it are not supported. Anything deeper should be modelled as tags or attributes, which avoids unbounded recursive hierarchies in payroll queries.
- The OrgUnit tree can be deeper (adjacency list + materialised path) because it is used for reporting and approvals, not for pay computation.

---

## 4. Strategies (plug-ins chosen by configuration)

Each strategy is a Java interface with implementations registered as Spring beans. The active implementation is resolved from **tenant → pay group / engagement → employee override**.

| Strategy | Implementations | Chosen at |
|---|---|---|
| `AttendanceMode` | `SHIFT_DUTY` (shift/duty grid, relievers), `DAILY_CHECKIN` (present/absent/half-day, WFH), `TIMESHEET` (hours per work order), `PIECE_RATE` (units) | Attendance policy |
| `PayBasis` | `PER_DUTY`, `MONTHLY_CTC`, `DAILY_WAGE`, `HOURLY`, `PIECE_RATE` | Pay group / rate card |
| `ProrationDivisor` | `CALENDAR_DAYS`, `FIXED_26`, `FIXED_30`, `WORKING_DAYS`, `PAID_DAYS_IN_CONTRACT` | Pay group / contract |
| `OvertimeRule` | `NONE`, `DUTY_BASED`, `HOURLY_SINGLE`, `HOURLY_DOUBLE` (statutory double rate), `FLAT_PER_HOUR` | Contract / pay group |
| `PayrollGrouping` | `PER_CLIENT_SITE`, `PER_ENGAGEMENT`, `PER_PAY_GROUP`, `PER_LEGAL_ENTITY` | Tenant (profile default) |
| `BillingMode` | `NONE`, `MANPOWER_FROM_ATTENDANCE`, `MILESTONE`, `WORK_ORDER`, `AMC_PERIODIC`, `TIME_AND_MATERIAL`, `MATERIAL` | Engagement |
| `CostingMode` | `NONE`, `CLIENT_SITE_PNL`, `PROJECT_JOB_COSTING` | Tenant |

New industries are supported by **adding a strategy implementation plus a profile template**. Existing modules do not change.

---

## 5. Module applicability by profile

`●` core (on by default) · `○` optional · `—` not applicable

| Module | Staffing (security / FM) | Project / field service (boiler eng.) | Corporate (IT) |
|---|---|---|---|
| Organisation, locations, statutory registrations | ● | ● | ● |
| Employees, onboarding, KYC, documents | ● | ● | ● |
| Org units (department/team), reporting manager | ○ | ● | ● |
| Customers, clients, sites | ● | ● | — |
| Site sub-units | ● (posts) | ● (equipment/assets) | — |
| Manpower contracts, rate cards | ● | ○ (contract labour) | — |
| Projects / work orders / AMC | — | ● | ○ |
| Shifts & rosters | ● | ○ | ○ (support teams) |
| Attendance: shift/duty | ● | ○ | — |
| Attendance: daily check-in, WFH | ○ | ● | ● |
| Timesheets | — | ● | ○ |
| Leave & holiday calendars per location | ● | ● | ● |
| Payroll engine | ● | ● | ● |
| TDS (declarations, Form 16, 24Q) | ● (few taxable) | ● | ● (heavy) |
| Client invoicing (GST, e-invoice) | ● | ● | ○ |
| Payroll-vs-invoice / job costing | ● (site P&L) | ● (project costing) | — |
| **Inventory** (items, warehouses, stock, issue) | ○ (uniforms, consumables) | **●** (spares, consumables, tools, site stores) | ○ (IT assets) |
| Purchase (vendors, PO, GRN, bills) | ○ | ● | ○ |
| E-way bill | — | ● | — |
| Field ops (site visits, incidents, geo tracking) | ● | ● (service reports) | — |
| PSARA / police verification | ● (security only) | — | — |
| CLRA licences & registers | ● | ○ | — |
| Asset & equipment register (client-owned) | — | ● | — |
| Employee asset custody (laptops, tools, uniforms) | ○ | ● | ● |

**Inventory is one module with profile-specific defaults.** It is not a separate product.
- **Security** issues uniforms and ID cards to guards, and recovers their cost through payroll deductions. This matches the benchmark screenshots.
- **Boiler engineering** runs a full stock ledger across warehouses and site stores, issues material to work orders for job costing, keeps tools in custody, applies reorder levels, and generates e-way bills.
- **IT** tracks laptops and access cards issued to employees and recovered when they leave.

---

## 6. Jurisdiction & location-rule resolution (India)

### 6.1 What varies by state or location
| Rule | Varies by | Resolved from |
|---|---|---|
| Public holidays | National + **state** + **location** (city or local festivals) | WorkLocation → HolidayCalendar |
| Minimum count of national/festival holidays | State National & Festival Holidays Act | State rule set (validation when publishing a calendar) |
| Weekly off, daily/weekly hours, spread-over, night work for women, OT rate | State Shops & Establishments Act / Factories Act / Labour Codes | State rule set + establishment type |
| Professional Tax slabs and frequency | State (some states have none) | WorkLocation PT jurisdiction |
| Labour Welfare Fund amounts and frequency | State (some states have none) | WorkLocation LWF jurisdiction |
| Minimum wages (basic + VDA) | State or central sphere × zone × skill × scheduled employment × effective date | WorkLocation min-wage zone + designation skill category |
| ESIC coverage and sub-code | Implemented area + location | WorkLocation ESIC sub-code |
| Leave entitlements (earned, casual, sick) | State S&E Act minimums vs tenant policy | State rule set as floor, tenant policy on top |
| Bonus, gratuity, PF, TDS | Central (uniform) | Central rule set |

### 6.2 Resolution algorithm
For an employee `E` on date `D`:
1. Find E's **primary work location** on D (from the effective-dated deployment or location assignment).
2. Read that location's jurisdiction attributes (state, zone, registrations, holiday calendar).
3. For each rule type, choose the **rule set version** effective on D for that state and sphere. Apply any tenant override that is no less favourable than the statutory floor.
4. If E worked at **several locations in the period** (relievers, transfers, engineers travelling between sites):
   - **Attendance and holidays** are evaluated per day at the location worked that day.
   - **PT and LWF** go to the jurisdiction of the location with the most paid days in the month. Ties go to the primary location. This is configurable per tenant.
   - **Minimum-wage checks** are made per day and per location, then aggregated.
5. Record the resolved rule-set IDs in the **payroll run snapshot**, so a later rule change cannot silently alter an approved payroll.

### 6.3 Holiday model
```
HolidayMaster (platform-maintained)
    national: Republic Day, Independence Day, Gandhi Jayanti (+ others notified)
    state lists per year (published by state governments)
HolidayCalendar (tenant, per year)
    ← built from template: national + state list for the location's state
    + location-specific additions (local festivals)
    + optional/restricted holidays with an annual quota (employee chooses N)
    assigned to: WorkLocation (default) | Client site (follows client's calendar) | PayGroup override
```
- In staffing, a **site may follow the client's holiday calendar**, not the agency's. Work on a declared holiday earns holiday pay or a compensatory off, according to the contract.
- Calendars are versioned per year. Publishing one validates it against the state's minimum holiday count.
- Attendance and payroll read holidays through a single `HolidayService.isHoliday(location, date)` that accounts for overrides.

### 6.4 Rule set storage (summary)
```
statutory_rule_set(id, rule_type, sphere CENTRAL|STATE, state_code NULL, effective_from, effective_to,
                   params JSONB, source_reference, version, status DRAFT|PUBLISHED)
tenant_rule_override(tenant_id, rule_set_id, params JSONB, approved_by, effective_from)
```
Rule sets are **platform-maintained data packs** (published by the compliance owner). Tenants see the published packs and may layer overrides that pass a validation hook (for example, a PT override is rejected, while a higher leave entitlement is accepted).

---

## 7. Provisioning flow with a profile

1. The platform admin creates the tenant: name, subdomain, **industry profile**, plan, default language(s).
2. The profile template seeds:
   - enabled modules and feature flags
   - the terminology map
   - default designations, components, leave types, attendance policy and strategies
   - report set and dashboards
   - document types and letter templates
3. The tenant admin completes the onboarding wizard:
   - legal entities and statutory registrations
   - branches and work locations (state, address, geo)
   - holiday calendars (pre-filled from national and state lists)
   - pay groups
4. Optional modules can be enabled later. Disabling a module hides it but **never deletes data**.

Profile templates are **versioned JSON/YAML packs** in the `tenant` module (e.g. `backend/modules/tenant/.../profiles/security-staffing/v1/`). Re-applying a newer template version is an explicit, previewed migration, never automatic.

---

## 8. Implications for engineering
- **No industry checks in business code.** Read capabilities instead: `tenantFeatures.isEnabled(Module.INVENTORY)`, `policy.attendanceMode()`. ArchUnit or a lint rule should forbid references to `IndustryProfile` outside the `tenant` module and the template loader.
- **APIs use canonical names** (`/clients`, `/sites`, `/engagements`, `/work-locations`). The UI relabels them per tenant.
- **Test matrix.** Every payroll, attendance and billing feature is tested against **all three reference tenants** (security, boiler engineering, IT), with seed data in `common-test`.
- **Effective dating everywhere location or pay can change:** deployment, work-location assignment, salary structure, statutory profile, holiday calendar version and rule sets.
- **Reporting** groups by `work_location`, `org_unit`, `client`, `site` and `engagement` interchangeably. Report definitions declare which dimensions they support, and each profile shows only relevant reports.
