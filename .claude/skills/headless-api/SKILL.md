---
name: headless-api
description: Use when designing or implementing any REST endpoint, channel endpoint (web auth, mobile bootstrap/sync), webhook, sync endpoint, device/kiosk ingest, or public/partner API in Allied Payroll. Keeps APIs channel-neutral and headless so web, tablet, mobile, kiosk, biometric devices, client portal and partners all reuse the same contracts.
---

# Headless, multi-channel API

Background: `docs/MULTI_CHANNEL_API.md`. The backend is one application (ADR 0003), but its domain modules are **headless**. Business rules live only there. Channel modules (`web-channel`, `mobile-channel`, `device-ingest`, `partner-api`) only compose and shape the data. Channel apps only present it.

## Where does the code go?
| You're building… | Put it in |
|---|---|
| A business rule, validation or calculation | The owning **domain module** (never a channel module or app) |
| A canonical resource (CRUD, actions, lists) | Domain service, `/api/v1/{resources}` |
| A screen that needs several resources | The **client** composes canonical resources, `fields=` projections and `summary` endpoints. The backend has no screen endpoints (`docs/BACKEND_REQUIREMENTS.md` §3.3) |
| Offline sync, bootstrap, compact mobile or kiosk payloads | the **`mobile-channel` module** (`/api/v1/mobile/...`) |
| A biometric device protocol | **device-ingest adapter** → standard punch command |
| Integration for an external system | The **public API product** (scopes, quotas) plus webhooks; no private shortcuts |

## Endpoint checklist
1. **Contract first.** Update the service's OpenAPI spec, then implement. Use canonical, channel-neutral names; industry terms are client-side terminology keys.
2. **Versioning.** Additive changes only within `/api/v1`. A breaking change means `v2` running in parallel, plus `Deprecation` and `Sunset` headers on v1. Assume mobile apps stay installed for 12+ months.
3. **Lists.** Support `page`/`size` (or `cursor` for sync and large sets), `sort`, `q`, `filter[...]`, `fields=` projection, and the global context parameters (`legalEntityId`, `branchId`, `period`, `clientId`, `siteId`). Add a summary/count endpoint if a dashboard card needs it.
4. **Mutations.**
   - Accept `Idempotency-Key`. It is **mandatory** for punches, payments, imports and anything mobile, kiosk, device or partner clients call.
   - Support `ETag`/`If-Match` (optimistic lock) on updates.
5. **Long-running work** returns `202` with a `job` resource. Completion is signalled by event, push or webhook.
6. **Errors** are `problem+json` with a stable `code`, localized `title`/`detail`, `params`, `traceId` and field `errors[]` (see the `i18n-text` skill).
7. **Data types.** Money is a decimal string plus currency. Dates are ISO `date`; instants are `date-time` with offset. No formatted numbers or dates in responses.
8. **Files** use presigned upload and download URLs, never streamed bytes through services.
9. **Security.**
   - Add a permission annotation and data-scope filtering. The tenant always comes from context, never the payload.
   - Declare the **channel policy** if the action must be restricted (e.g. no payroll approval from `kiosk` or `device`).
   - Record the channel, device and app version in the audit trail.
10. **Forms.** If the UI is metadata-driven (custom fields, onboarding, visit forms), expose **form metadata** so web, mobile, tablet and kiosk all render it. Don't hard-code per-channel forms.
11. **Webhooks.** If partners need the event, add it to the webhook catalogue: an IDs-only payload, an HMAC signature, retries and a DLQ.
12. **Tests.**
    - Contract tests (consumer: the generated SDKs).
    - A tenant-isolation test.
    - An idempotency replay test.
    - A version-compatibility test when changing an existing response.

## Never
- Put pay, eligibility, statutory or pricing logic in a channel module or client app.
- Create channel-specific copies of a domain endpoint (`/mobile/employees`). `/api/v1/mobile/...` is only for bootstrap, sync and compact projections composed in the `mobile-channel` module.
- Return PII in webhook payloads or push notifications.
- Trust device time for attendance. Record server time and the device offset, and flag anomalies.
