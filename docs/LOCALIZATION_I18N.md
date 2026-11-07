# Localization & Multilingual Design (i18n / l10n)

Companion to [PROJECT_INITIATION.md](PROJECT_INITIATION.md). Multilingual support is a **base capability**: it is built in from the first commit, not retrofitted. Users range from tenant admins and payroll staff, who usually work in English, to security guards, housekeeping staff and site technicians, who often prefer their regional language and may have limited literacy.

---

## 1. Scope

| Layer | Must be translatable | Examples |
|---|---|---|
| UI chrome | All labels, menus, buttons, validation messages, empty states | Web SPA, mobile app |
| Server messages | Error/validation messages, notification texts, import error reports | `problem+json` detail, Excel error file |
| Tenant master data | Names users see | Designations, salary component names, leave types, document types, incident types, shift names |
| Generated documents | **English by default** (payslips, letters, invoices, registers). Regional-language or bilingual PDFs are an optional, later capability (§4.6) | PDF |
| Notifications | SMS, WhatsApp, email, push | Payslip published, OTP, shift reminder |
| Terminology | Industry-specific nouns | Site / Plant / Office; Duty / Man-day |
| Formats | Numbers, currency, dates, amounts in words | ₹12,34,567.00; "Rupees Twelve Lakh …" |

**Not translated:** statutory return files (ECR, ESIC, 24Q, GST/IRN JSON). These follow the government's format specifications exactly, in English or ASCII as required. Canonical codes (`EARNING`, `PF_EMPLOYEE`) and API field names are also never translated.

## 2. Languages

**Launch set:**
- English (`en-IN`, the default and fallback)
- Hindi (`hi-IN`)
- Tamil (`ta-IN`)
- Telugu (`te-IN`)
- Kannada (`kn-IN`)
- Malayalam (`ml-IN`)

**Next wave:** Marathi (`mr-IN`), Bengali (`bn-IN`), Gujarati (`gu-IN`), Odia (`or-IN`), Punjabi (`pa-IN`), Assamese (`as-IN`), Urdu (`ur-IN`, **right-to-left**).

Locale tags are BCP 47. Adding a language must be **data plus translation files only**, with no code change. RTL support is designed in from the start (CSS logical properties, mirrored layouts), even though Urdu ships later.

## 3. Locale resolution

The locale is resolved in this order. The first match wins:
1. Explicit request: `Accept-Language` header, or `?lang=` for document downloads.
2. The user's saved preference (`user.preferred_locale`).
3. The employee's preferred language (onboarding field). This is used for documents and notifications sent to that employee.
4. The work location's default language. For example, a Chennai site defaults to `ta-IN` for guard-facing documents.
5. The tenant's default locale.
6. `en-IN`.

**Each tenant enables a subset of languages** (`tenant_setting i18n.enabled_locales`). The UI only offers enabled languages.

**Separate audiences in one transaction.** Notifications (SMS, WhatsApp, push) and in-app screens use the **recipient's locale**, not the current user's. One payroll run can notify one employee in Tamil and another in Hindi. **PDFs are generated in English** unless the tenant turns on regional PDFs (§4.6).

## 4. Implementation by layer

### 4.1 Backend (Spring Boot)
- **Message keys, not literal strings.** Every user-facing message is a key in `messages*.properties` (UTF-8) per service, resolved through `MessageSource` with ICU4J `MessageFormat` for plurals, gender and select.
  - Key format: `{service}.{area}.{name}`, e.g. `payroll.run.error.attendanceNotApproved`.
- **Error responses** (`problem+json`) carry a stable `code`, a localized `title` and `detail`, and `params`, so clients can re-render the message.
- **Validation** (Jakarta Bean Validation) uses message keys. Custom validators interpolate parameters through ICU.
- **Locale propagation:** the locale travels with the request context (alongside tenant), in domain-event metadata (`locale` of the initiator), and in async jobs. Consumers that produce user-facing output use the **recipient's** locale.
- **Missing keys** fail CI (see §8). At runtime they fall back to `en-IN`, never to the raw key.

### 4.2 Translatable master data
Tenant-defined names are stored with translations:
```
designation(id, tenant_id, code, name /* default-locale text */, name_i18n JSONB /* {"ta-IN": "...", "hi-IN": "..."} */)
```
- Use a `JSONB` column (`*_i18n`) for short labels on master tables. It is simple, needs no joins, and can be indexed with a GIN index where search is needed.
- Use a separate `translation(entity_type, entity_id, field, locale, value)` table **only** for long rich text, such as letter templates or policy documents.
- The API returns the resolved label (`name`) plus `nameI18n` on admin endpoints for editing.
- Search matches the default name **and** all translations.

### 4.3 Person names and free text
- Store names exactly as entered (Unicode, NFC-normalised).
- Optionally store `name_transliterated` (Latin script) to support search and English-only statutory files. EPFO/ESIC/bank files usually require Latin-script names matching the KYC records.
- Never auto-translate names.
- **Collation:** use PostgreSQL ICU collations (`und-x-icu` or locale-specific) for sorting, and do not rely on byte order.

### 4.4 Web front end
- An i18n library with ICU message syntax (e.g. FormatJS / react-intl or i18next with the ICU plugin), with **namespaces per module** and lazy-loaded bundles.
- `Intl.NumberFormat('en-IN', {style:'currency', currency:'INR'})` gives Indian digit grouping (₹12,34,567.00). Use `Intl.DateTimeFormat` with the user's locale, and keep the tenant's date-display preference (default `dd-MM-yyyy`).
- Use CSS logical properties (`margin-inline-start`) and `dir` switching for RTL.
- Fonts: **Noto Sans** families for Indic scripts, subset and lazy-loaded per script.
- No text inside images. Icons carry `aria-label` keys.

### 4.5 Mobile app (employees, guards, technicians)
- The language is chosen at first launch and changeable later. It defaults from the employee's profile.
- Use icon-first and colour-coded actions (check-in, payslip, leave) for low-literacy users. Text-to-speech for key screens is a later enhancement.
- Translation bundles ship in the app and can be updated over the air.

### 4.6 Generated documents (PDF)

**Default: English.** Payslips, invoices, credit notes, letters, statutory registers and reports are generated in English (`en-IN`).
- **Renderer:** Thymeleaf HTML → **OpenHTMLtoPDF** (pure Java, runs inside the service, no extra infrastructure). This is enough for Latin-script output.
- **Names and addresses** come from the Latin-script fields (`name_transliterated`, the English address). If a person's name exists only in a regional script, the PDF uses the transliterated value. Onboarding should therefore capture the Latin-script name, which is the one that matches the KYC, bank and EPFO records.
- **Amount in words** uses English with Indian numbering ("Rupees Twelve Lakh Thirty-Four Thousand …").
- **Templates** are per document type, and a tenant can upload its own branded template.

**Optional, later: regional-language PDFs.**
- Turned on per tenant and per document type (e.g. a Tamil appointment letter, a bilingual payslip), and only when a customer asks for it.
- **Indic scripts need complex text shaping** (conjuncts, matras). OpenHTMLtoPDF/PDFBox **does not shape Indic scripts correctly**. Regional PDFs must go through a **Chromium-based renderer** (e.g. a Gotenberg service), which uses HarfBuzz shaping, with Noto fonts embedded.
- Build the document service behind a `PdfRenderer` port, so the Chromium adapter can be added later without touching document templates or callers.
- Regional amount-in-words would use ICU4J `RuleBasedNumberFormat` spellout rules, verified per language. Invoices always keep the English amount in words.
- **Statutory registers** stay in English unless a state rule requires the regional language.

### 4.7 Notifications
- Templates exist per channel × event × locale.
- **India SMS requires DLT (TRAI) template registration.** Every SMS template in every language has to be registered on the DLT platform, and its template ID stored against the template. Sending an unregistered variant fails. Plan the registration effort for each new language.
- SMS in Indian scripts is sent as Unicode (UCS-2), which **reduces the characters per segment from 160 to 70**. This affects cost, so keep regional SMS short.
- WhatsApp templates also need approval per language.

### 4.8 Bulk import/export (Excel)
- Column headers are **canonical keys** (hidden row or second header row) plus localized display labels. Import maps by key, so templates downloaded in Tamil still import correctly.
- Error reports are produced in the uploader's locale.
- Accept Unicode content and normalise it to NFC. Reject mixed-script lookalike codes in identifier columns.

## 5. Industry terminology (tenant-specific labels)

Industry profiles relabel canonical concepts (see [INDUSTRY_PROFILES_AND_STRUCTURE.md](INDUSTRY_PROFILES_AND_STRUCTURE.md) §3.4). This is implemented as **an extra message-resolution layer**:
```
resolve(key, locale, tenant):
  1. tenant override for (key, locale)               e.g. term.site = "Plant"
  2. industry profile pack for (key, locale)          e.g. term.site (boiler) = "Plant / Project site"
  3. base bundle for (key, locale)                    e.g. term.site = "Site"
  4. base bundle for en-IN
```
- UI strings refer to terms through placeholders, e.g. `"Add {term.site}"`. They never hard-code "Site".
- Profile packs and tenant overrides each provide translations per locale.
- Tenant overrides are cached per tenant and locale, and are invalidated by event.

## 6. Dates, times, numbers and currency
- **Storage:** timestamps are `timestamptz` (UTC). Business dates (attendance date, pay period) are `date` with no timezone. Payroll periods are `YearMonth`.
- **Timezone:** each work location stores its timezone (IST today). Overnight shifts belong to a date based on the attendance setting (shift start or shift end), resolved in the location's timezone.
- **API:** ISO-8601 throughout. All formatting happens at the edge (UI and documents).
- **Currency:** INR only for now. Amounts are `BigDecimal`, and the currency code is stored on monetary aggregates so another currency can be added later.
- **Financial year:** April–March, labelled `2026-27` in the UI and `2627` in number series.

## 7. Accessibility (goes with i18n)
- WCAG 2.2 AA for the web portal, client portal, mobile app and kiosk.
- Readable font sizes for Indic scripts, which need more line height than Latin.
- Do not use colour alone to show status. Use text and icon labels as well.

## 8. Translation workflow & quality gates
1. Developers add keys in `en-IN` only, in the same PR as the feature.
2. CI checks:
   - **No hard-coded user-facing strings.** ESLint `no-literal-string` (or an equivalent) on the front end. On the back end, an ArchUnit/regex check stops literal messages in exceptions that reach the API.
   - **Key parity.** Every key in `en-IN` exists in every enabled locale, or is listed in a tracked "pending translation" allowlist. Releases are blocked if the allowlist contains keys that are user-facing in a GA locale.
   - **ICU syntax validation** for all bundles.
3. Keys are synced to a translation management system (e.g. Weblate, Tolgee or Crowdin; to be decided). Professional or agency translators review payroll and legal terminology. Machine translation is allowed for drafts only.
4. **Pseudo-localization build** (`qps-ploc`, which lengthens text by about 40%, adds accents and includes an RTL variant) runs in QA to catch truncation and concatenation bugs.
5. Glossary: a per-language glossary of payroll and statutory terms (PF, ESIC, gross, net, deduction, duty, reliever, arrears) keeps translations consistent. Statutory acronyms stay in Latin script unless a state form requires otherwise.

## 9. Engineering rules (summary)
- Never concatenate translated fragments. Use full ICU messages with placeholders.
- Never put user-facing text in enums, exceptions, or database `CHECK` messages. Use codes plus keys.
- Every new master entity with a user-visible name gets a `*_i18n` JSONB column.
- Notifications and in-app screens use the **recipient's** locale. PDFs default to English and use Latin-script name and address fields.
- Treat sorting, searching and case-folding as locale-aware: use ICU, not `toLowerCase()`.
- Test with at least `en-IN`, one Dravidian script (Tamil), one Devanagari script (Hindi), and pseudo-locale.
