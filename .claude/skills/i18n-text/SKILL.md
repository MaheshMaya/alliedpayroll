---
name: i18n-text
description: Use whenever adding or changing user-facing text in Allied Payroll — UI labels, API error/validation messages, master-data names, industry terminology, PDF documents (payslips, letters, invoices), SMS/WhatsApp/email notifications, or Excel import/export headers. Enforces message keys, ICU formatting, recipient-locale notifications, English-by-default PDFs and DLT-registered SMS.
---

# Multilingual text

Background: `docs/LOCALIZATION_I18N.md`. The launch locales are `en-IN` (default and fallback), `hi-IN`, `ta-IN`, `te-IN`, `kn-IN` and `ml-IN`. Urdu (RTL) comes later, so layouts must already handle RTL.

## Rules by text type

| Text | Do |
|---|---|
| Backend message, error or validation | Add a key `{service}.{area}.{name}` to `i18n/messages.properties` (en-IN, UTF-8). Use ICU placeholders `{count, plural, …}`. Throw domain exceptions with a **code plus params**, never a literal message. The `problem+json` response returns `code`, the localized `title`/`detail`, and `params`. |
| UI label | Add it to the module namespace in the front-end `en-IN` bundle. No literal strings in components (lint enforced). |
| Industry noun (site, client, duty, engagement…) | Reference the terminology key, as in `"Add {term.site}"`. Never hard-code "Site", "Plant" or "Office". |
| Master-data name (designation, component, leave type, shift, document type…) | Store `name` plus `name_i18n JSONB`. The API resolves it to the requested locale, and admin endpoints expose `nameI18n` for editing. |
| Person names | Store as entered, NFC-normalised, with an optional Latin `name_transliterated` for statutory and bank files. Never translate them. |
| PDF documents | **English by default.** Render via the `PdfRenderer` port (OpenHTMLtoPDF). Use Latin-script name and address fields (`name_transliterated`). Amount in words uses English with Indian numbering (lakh/crore). Labels still come from message keys (en-IN), not literals. Regional PDFs are an optional per-tenant feature: they need the Chromium adapter, because OpenHTMLtoPDF breaks Indic conjuncts. |
| SMS | Use a template per event × locale, with its **DLT template ID** stored. Every new language variant must be registered on DLT before release. Unicode SMS fits 70 characters per segment, so keep it short. |
| WhatsApp / email / push | Use a template per event × locale. WhatsApp templates need approval per language. |
| Excel templates and exports | Use canonical column keys (hidden or second header row) plus localized labels. Imports map by key. Error files use the uploader's locale. |
| Statutory files (ECR, ESIC, 24Q, IRN JSON) | **Never translate.** Follow the government format exactly. |

## Locale to use
- For an interactive request: `Accept-Language` → the user's preference → the tenant default → `en-IN`.
- For notifications **to a person**: that person's preferred language → the work location's default → the tenant default → `en-IN`.
- For PDFs: `en-IN`, unless the tenant has enabled regional PDFs for that document type.
- Carry the locale in the request context and in the event header `x-locale`.

## Never
- Concatenate translated fragments, as in `msg("hello") + name`. Use one message with placeholders.
- Put user-facing text in enums, exception messages, or DB constraints.
- Use `toLowerCase()`/`toUpperCase()` or byte-order sorting on user text. Use ICU and locale-aware collation.
- Format numbers or dates on the server for API responses. Return ISO and raw numbers, and format at the edge.

## Before finishing
- Add the en-IN keys. Add the other locales, or put the keys on the pending-translation allowlist.
- ICU syntax is valid (CI check).
- Check the UI with the pseudo-locale build for truncation.
- Any new PDF has been rendered with a regional-script employee to confirm the Latin-script fields are used. If regional PDFs are enabled for it, also check Tamil and Hindi shaping.
