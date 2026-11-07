---
name: flutter-mobile
description: Use when building or changing the Allied Payroll Flutter app (employee, manager/supervisor and kiosk modes) — screens, offline punch queue, delta sync, face capture/verification, geofence, push, i18n, kiosk lock-down, or the generated Dart API client.
---

# Flutter mobile and kiosk app

Background: `docs/MOBILE_AND_DEVICES.md` and `docs/MULTI_CHANNEL_API.md`. The app lives in `mobile/`, with the generated SDK in `api/sdk/dart/`. It talks to the one backend's `/api/v1/mobile/...` endpoints (bootstrap, sync) and the canonical resources.

> These conventions are proposed. Follow the code once the app exists, and update this skill when it differs.

## Ground rules
1. **One codebase, three modes:** employee, manager and kiosk. Gate features by the **mode plus the permissions and enabled modules from `/bootstrap`**. Never hard-code by industry or tenant.
2. **No business logic in the app.** Pay, eligibility, statutory values, punch validity (window, geofence policy, roster) and approvals are decided by the server. The app only collects input and shows results. Client-side checks are UX hints only.
3. **API access only through the generated `dart-dio` client.**
   - Never hand-write DTOs for server resources.
   - Interceptors add: auth, tenant, `Accept-Language`, `X-Channel` (`mobile` | `kiosk`), app version, `Idempotency-Key` (on every mutation), and ETag/If-None-Match.
4. **State and navigation:** Riverpod (generated providers) and go_router with mode and role guards. Feature-first folders: `features/<feature>/{presentation,application,domain,data}`.

## Offline and sync
- Punches are written first to the **encrypted Drift (SQLCipher) queue**, with a client UUID, device wall-clock time, monotonic elapsed time, geo + accuracy, the face result (score, SDK version) and a face-crop reference. Only then are they sent.
- Sync in batches to `POST /sync/punches`. Handle the per-item `ACCEPTED | FLAGGED | REJECTED` results and show them to the user.
- Reference data (roster, shifts, holidays, leave balances, i18n bundles) uses `GET /sync/{dataset}?since=cursor` with tombstones. Never re-download everything.
- Background sync uses WorkManager / BGTaskScheduler. Also sync when the app comes to the foreground and when connectivity returns.

## Face and biometrics
- Use only the **`FaceEngine` interface**: `enroll`, `extractTemplate`, `verify1to1`, `identify1toN`, `checkLiveness`, `assessQuality`. The vendor SDK lives in one adapter package. Never call vendor APIs from features.
- Use ML Kit face detection for framing and quality guidance before calling the engine.
- Liveness is required on every biometric punch. Store the template version with every template.
- Kiosk:
  - 1:N only over the cached roster for that location.
  - Templates are encrypted at rest and wiped on de-provisioning.
  - Badge/PIN + 1:1 fallback.
- **Phone fingerprint or face unlock (`local_auth`) only unlocks stored tokens. It is never treated as proof of employee identity.**
- Show the consent screen at enrolment, in the user's language. Offer the non-biometric alternative path.

## Location and integrity
- Capture geo **only at punch or visit events**. No background tracking.
- Report mock-location and Play Integrity / App Attest results with the punch. The server decides whether to flag.

## i18n and UI
- All strings live in ARB files (ICU). Industry nouns come from terminology keys in bootstrap. Use Noto fonts. Test in `en`, `hi`, `ta` and the pseudo-locale.
- Field-staff screens are icon-first with large touch targets. The kiosk screen is full-screen, high-contrast, with audio and visual feedback.
- Format money and dates on the client with `intl` (`en_IN` grouping). Amounts arrive as decimal strings and are parsed with `Decimal`, never `double`.

## Kiosk mode
- Entered only by **device provisioning** (enrolment QR → device credential bound to a work location). No employee login on a kiosk.
- Lock-down through MDM dedicated-device mode, with `startLockTask()` via a platform channel as a fallback. Admin exit requires a server-issued one-time code.
- Handle 24×7 operation: power-cut recovery, battery-protection charging, a daily self-check, and a heartbeat to the device registry.

## Before finishing
- Widget and unit tests for new logic. Golden tests for new key screens.
- Run on a low-end Android device profile. Check offline behaviour (airplane mode → punch → reconnect → no duplicates).
- No PII in logs, crash reports or push payloads.
