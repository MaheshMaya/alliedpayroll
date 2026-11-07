---
name: bulk-import
description: Use when adding an Excel/CSV bulk upload (create or update) or a large export for any entity — organizations, clients, sites, employees, statutory/bank updates, terminations, attendance (date-wise/type-wise), salary payments, products/stock. Implements the template → upload → validate → error file → re-upload → commit flow with the shared common-import library.
---

# Bulk import / export

The benchmark product uses one import flow everywhere, and users expect it:
1. Download the template.
2. Fill it in.
3. Upload it.
4. The server validates and returns row-level errors.
5. Fix and re-upload.
6. Submit.

Create and Update are **separate modes with separate templates**. Background: `docs/PROJECT_INITIATION.md` §6.

> These conventions are proposed. Use the actual `backend/platform/common-import` API once it exists, and update this skill when it differs.

## Steps
1. **Define an `ImportDefinition`** in the owning service:
   - `entity`, `mode` (`CREATE` | `UPDATE` | named special mode such as `STATUTORY_BANK_UPDATE`)
   - columns, each with: canonical `key`, label message key (see the `i18n-text` skill), type, required, and allowed values (enum/master lookup)
   - **Custom fields** are appended automatically from field-configuration metadata.
   - Required flags follow the tenant's field configuration.
   - The **industry profile** hides columns for disabled modules.
2. **Template generation.**
   - XLSX built with Apache POI.
   - Row 1 holds localized labels; a hidden row holds the canonical keys.
   - Data-validation dropdowns for small masters.
   - An instructions sheet in the user's locale.
   - Context such as client, contract, month and shifts is pre-filled for attendance templates.
3. **Upload.**
   - Accept `.xlsx` and `.csv` only, with a size limit.
   - Reject files containing formulas, macros or external links, and malware-scan them.
   - Store the file in object storage under `{tenant}/imports/…`.
   - Create an `import_job` (status `UPLOADED`).
4. **Validate (async).**
   - Parse by canonical key, normalise Unicode to NFC, trim, and parse dates in the declared format.
   - Validate types, required fields, master lookups (by code), uniqueness within the file and against the DB, business rules, and the user's **data scope**: users can't import into branches or clients they can't see.
   - Collect **all** errors per row. Don't stop at the first.
   - Produce an error file: the original rows plus an error column, in the uploader's locale.
   - Status becomes `VALIDATED` or `VALIDATION_FAILED`, with counts.
5. **Commit (async, on user submit).**
   - Process in chunks within transactions, and make it idempotent (the job ID plus row number is the idempotency key).
   - Publish domain events through the outbox exactly as single-record APIs do. Reuse the same application service, never a separate write path.
   - Status: `COMMITTED`, or `PARTIALLY_COMMITTED` with a report.
6. **API.**
   - `GET /api/v1/{resource}/imports/templates/{mode}`
   - `POST /api/v1/{resource}/imports` (multipart)
   - `GET /api/v1/{resource}/imports/{jobId}` (status, counts, error file link)
   - `POST /api/v1/{resource}/imports/{jobId}/commit`
7. **Audit.** Record who uploaded, the file hash and the row counts. Every created or updated record is audited as usual.
8. **Exports.**
   - Large exports are async jobs (the reporting module or the owning module) that produce XLSX with localized headers and a canonical key row.
   - PII stays masked unless the user holds the reveal permission. If they do, the reveal is audited.

## Tests
- Template round-trip: download, fill, upload, and it validates cleanly.
- Error cases produce the correct row and column messages in two locales.
- Cross-tenant and out-of-scope rows are rejected.
- Commit is idempotent when retried.
