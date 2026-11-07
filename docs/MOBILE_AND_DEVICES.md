# Mobile App, Biometrics & Hardware Selection

Companion to [MULTI_CHANNEL_API.md](MULTI_CHANNEL_API.md). This document records the **Flutter decision**, the **biometric attendance strategy** (face vs fingerprint, on-device vs server), and **hardware recommendations** for kiosks and biometric terminals.

> Hardware models, prices and SDK capabilities change often. Treat named products as a **shortlist for a proof of concept (§6)**, not a purchase order. Confirm current specs, Android versions and licensing with each vendor before buying.

---

## 1. Decision: Flutter

**Flutter is the mobile and kiosk technology.** We build **one Flutter codebase** with three runtime modes:

| Mode | Who | Device | Login |
|---|---|---|---|
| **Employee** | Guards, technicians, office staff | Personal Android phone (iOS supported) | Mobile OTP / PKCE, then device-bound refresh token |
| **Manager / supervisor** | Field officers, site supervisors, engineers | Phone or tablet | Same as employee, with role-based features |
| **Kiosk** | Shared, at a site gate or office reception | Dedicated Android tablet or Android face terminal | Device credential bound to a work location (see [MULTI_CHANNEL_API.md](MULTI_CHANNEL_API.md) §4) |

Why Flutter:
- One codebase covers Android, iOS and tablet layouts.
- Rendering performs well on **low-end Android**, which most guards and field staff use.
- Good offline and local-database support.
- Mature internationalization through ARB/ICU with `gen_l10n`.
- Commercial face SDKs ship Flutter plugins.

### 1.1 Target platforms
- **Android first.** Minimum API 26 (Android 8.0), targeting the current Play Store target API. Test on a ₹8–12k class phone (2–3 GB RAM) as the performance baseline.
- **iOS** for office staff and managers. Minimum iOS 15 (check against the face SDK's minimum).
- **Tablets** use adaptive layouts for manager and kiosk modes, with a 10–11" landscape kiosk layout.

### 1.2 App architecture

| Concern | Choice (proposed) |
|---|---|
| Structure | Feature-first folders; clean layers (presentation → application → domain → data); `melos` monorepo for app and shared packages |
| State management | Riverpod (with code generation) |
| Navigation | go_router with role/mode-based guards |
| API client | **Generated from OpenAPI** (`openapi-generator`, `dart-dio`) into `api/sdk/dart`; dio interceptors for auth, tenant, `Accept-Language`, `Idempotency-Key`, ETag |
| Local database / offline queue | Drift (SQLite) **encrypted with SQLCipher**; punch queue, reference-data cache, delta-sync cursors |
| Secure storage | `flutter_secure_storage` (Keystore / Keychain) for refresh tokens and device keys |
| Background sync | WorkManager (Android) / BGTaskScheduler (iOS) via plugin; foreground sync when the app opens |
| Push notifications | Firebase Cloud Messaging (Android/iOS) |
| i18n | `flutter_localizations` + `intl` with ARB files (ICU plural/select); bundles refreshable from the server; Noto fonts for Indic scripts |
| Camera / face | `camera` plugin + **on-device face detection** (Google ML Kit) for framing and quality checks; **face matching and liveness through a commercial SDK behind a `FaceEngine` interface** (§3.3) |
| Location | `geolocator` with accuracy filtering; mock-location detection (Android) |
| QR / badge | `mobile_scanner` |
| Device integrity | Play Integrity API (Android), App Attest (iOS) |
| Device unlock | `local_auth`. Phone fingerprint or face unlock only **unlocks the app's stored token**. It does not prove which employee is present (see §2) |
| Kiosk lock-down | Android **dedicated-device / lock-task mode** via MDM (preferred), plus a platform channel to `startLockTask()` as a fallback |
| PDF viewing | In-app viewer for payslips and letters (English PDFs) |
| Testing | Unit and widget tests, golden tests for key screens, integration tests on a device farm (low-end Android, a tablet, iOS) |
| CI/CD | GitHub Actions + Fastlane; flavors `dev / staging / prod`; Play internal testing and TestFlight; forced-upgrade policy from bootstrap |
| Crash and performance | Firebase Crashlytics, or Sentry (Sentry can be self-hosted if data residency demands it) |

**Flavors:** one binary with the mode chosen at runtime (kiosk mode is entered only through device provisioning). Optionally, a separate **kiosk build** with a smaller footprint, if MDM-managed kiosks should not see the employee mode at all.

---

## 2. Biometric strategy

### 2.1 Face first, fingerprint where it's mandated
| Factor | Face (camera) | Fingerprint (dedicated terminal) |
|---|---|---|
| Works on phones and tablets we already target | **Yes** | No. Phone fingerprint sensors can't be matched against *our* enrolled templates; they only unlock the device |
| Manual workers (housekeeping, construction, boiler technicians) | Good | **Poor**: worn or damaged fingerprints cause frequent false rejects |
| Hygiene / touchless | Touchless | Contact |
| Offline at the kiosk | Yes, with on-device matching | Yes (terminal stores templates) |
| Spoofing risk | Needs **liveness detection** | Needs fake-finger detection (better terminals have it) |
| Hardware cost | ₹0 (employee phones) to a tablet per site | A terminal per site |
| Speed at shift change (100+ people) | Kiosk tablet is slower than a dedicated terminal | Dedicated terminals are fast |

**Decision:**
- **Face verification is the primary biometric** across the employee app, the manager app and kiosks.
- **Fingerprint, card and face terminals from third-party vendors** are supported as *integrated devices* through the device-ingest adapters. Use them where a client requires them, where throughput is high (factory gates, large offices), or where the client already owns devices.

### 2.2 Which verification for which situation

| Situation | Method | Notes |
|---|---|---|
| Employee self check-in on their own phone | **1:1 face verification** (the logged-in employee's selfie is compared with their enrolled template) + **passive liveness** + **geofence** + device integrity | Main method for security guards and field engineers. No hardware cost |
| Shared kiosk at a site (our Flutter app) | **1:N identification** over the roster of employees deployed at that location + liveness. **Or 1:1** after QR badge, employee-code or PIN entry | 1:N works for small rosters (up to about 300 per kiosk). Above that, or with poor lighting, use badge/PIN + 1:1 |
| Supervisor marks a guard who can't punch (phone dead, no network) | Supervisor's app captures a **photo** + geo; the punch is flagged for review | Fallback. The audit trail shows who marked it |
| Large factory or office gate with high throughput | Vendor **face/fingerprint/card terminal** → push protocol → device-ingest | ZKTeco/eSSL ADMS push, Matrix COSEC API, etc. (§4.3) |
| IT office | Existing **access-control / biometric terminals** (push integration) or web/mobile check-in; WFH via mobile with optional geo | Many IT offices already run Matrix/ZKTeco/HID systems |
| Onboarding identity proofing | Aadhaar e-KYC (OTP / DigiLocker) through a licensed KYC provider + **face enrolment** (selfie with liveness, optionally matched against the KYC photo) | Enrolment creates the face template used for attendance |

**Aadhaar biometric authentication is *not* used for daily attendance.** It requires AUA/KUA licensing, registered devices and a legal basis that a private payroll SaaS will not have. Aadhaar stays limited to onboarding e-KYC through a licensed provider.

### 2.3 On-device vs server-side matching

| | On-device (recommended for attendance) | Server-side |
|---|---|---|
| Works offline | **Yes**: essential for sites with poor network | No |
| Latency | ~sub-second on mid-range devices (validate in PoC) | Network dependent |
| Data exposure | The template is cached encrypted on the device (kiosk: roster templates only; phone: own template only) | Templates stay central |
| Tamper resistance | Lower. Mitigate with device integrity checks, liveness, a signed result plus a **selfie thumbnail uploaded for server re-verification and audit** | Higher |

**Hybrid decision:**
1. **Match on the device** so punches work instantly and offline.
2. **Upload the punch with a compressed face crop.** The server **re-verifies** it asynchronously (risk-based or sampled, e.g. 100% for flagged devices, 10% otherwise).
3. If server re-verification fails, the punch is **flagged** for the supervisor; attendance approval sees the flag.

### 2.4 Face SDK selection
- **Do not train or ship open-source research face models in production.** Popular pretrained models (e.g. the InsightFace/ArcFace weights) carry **non-commercial licences**. Production-grade **liveness / anti-spoofing** is hard to build safely.
- **Use a commercial SDK** with a Flutter plugin, behind our own `FaceEngine` interface. The interface covers enrolment, template extraction, 1:1, 1:N, liveness and quality. This lets us swap vendors without touching attendance code, and template-version fields allow re-enrolment on migration.
- **Shortlist for the PoC:**
  - [Regula Face SDK](https://pub-web-s1.flutter-io.cn/documentation/flutter_face_api/3.2.3), Flutter plugin `flutter_face_api`. It does matching, recognition and liveness. **Confirm that the current version runs matching and liveness fully on-device.** Older versions relied on Regula's web service.
  - [Innovatrics DOT](https://developers.innovatrics.com/digital-onboarding/technical/remote/flutter/documentation/), Flutter plugins. Face capture runs on-device. **Some liveness modes (MagnifEye, Smile) are evaluated on the server.** Check whether passive liveness can run locally.
  - At least one SDK that **advertises fully offline on-device liveness and matching**, e.g. [flutter_face_nova](https://pub.dev/packages/flutter_face_nova). This is a smaller vendor, so assess maturity, support and certification carefully.
  - Optionally, a large vendor quoted through India distributors (e.g. Paravision, Neurotechnology, IDEMIA) for comparison.
- **Evaluation criteria (weighted):**

  | Weight | Criterion |
  |---|---|
  | 25% | **On-device 1:1 and 1:N, plus passive liveness**, offline |
  | 20% | Accuracy on Indian demographics and conditions: FAR/FRR at our threshold, low light, outdoor glare, helmets/caps, beards, masks |
  | 15% | Liveness certification (**ISO/IEC 30107-3, iBeta Level 1/2**) and our own spoof tests: printed photo, screen replay, mask |
  | 15% | Performance on a low-end Android phone and the kiosk tablet (time to result; 1:N speed for 300 people) |
  | 10% | Licensing model and cost at scale (per device, per active user, or per transaction), plus offline licence validation |
  | 10% | Data handling: **no images or templates sent to the vendor cloud**; template portability; on-prem or India-hosted server option |
  | 5% | Flutter plugin quality, documentation, support in India |

### 2.5 Privacy and compliance for biometrics (DPDP Act)
- **Consent:**
  - Explicit and itemised consent at face enrolment, in the employee's language, recorded with a timestamp and version.
  - A **non-biometric alternative** (badge/PIN + supervisor confirmation) for employees who decline.
- **Purpose limitation:** templates are used only for attendance identity verification, never for analytics.
- **Storage:**
  - Templates are encrypted with the per-tenant key, and raw enrolment images are not kept after template creation (configurable).
  - Punch face crops are kept for a **short audit period** (e.g. 90 days), then purged.
- **Deletion:** templates are deleted on exit (after full and final settlement) or when consent is withdrawn. Kiosk caches are wiped on de-provisioning or roster removal.
- **Location:** captured **only at punch and visit events**. There is no continuous tracking (the "Lokator"-style tracking stays a separately consented, later feature).

---

## 3. Hardware recommendations

### 3.1 Option overview

| Option | What runs on it | Best for | Indicative cost per unit |
|---|---|---|---|
| **A. Employee's own phone** | Our Flutter app (employee mode) | Security guards, field engineers, distributed sites with 1–20 staff | ₹0 hardware |
| **B. Rugged Android tablet kiosk** | Our Flutter app (kiosk mode), MDM-locked | Outdoor gates, security posts, boiler plant sites, dusty or wet areas | ~₹50–57k (Galaxy Tab Active5 Enterprise Edition) + mount |
| **C. Standard Android tablet kiosk** | Our Flutter app (kiosk mode), MDM-locked | Indoor office reception, client site office, workshop | ~₹15–30k + enclosure |
| **D. Android face terminal** (open Android, purpose-built) | Our Flutter app, if the vendor allows custom APKs | Sites wanting terminal-grade hardware (IR/dual camera, relay for door) running *our* software | Vendor quote |
| **E. Vendor biometric terminal** (closed firmware) | Vendor firmware; punches pushed to our device-ingest | High-throughput gates, IT offices, clients that already own devices or mandate fingerprint/card | ~₹6–18k for common face terminals |

### 3.2 Kiosk tablets (options B and C)

**Recommended rugged kiosk:** **Samsung Galaxy Tab Active5 Enterprise Edition**.
- India launch price ₹49,999 (6/128 GB) and ₹56,999 (8/256 GB).
- IP68 and MIL-STD-810H.
- Includes a 12-month Knox Suite subscription and a 36-month warranty.
- [Samsung newsroom](https://news.samsung.com/in/samsung-launches-galaxy-tab-active5-in-india-rugged-5g-enabled-tablet-built-for-enterprise-workforce), [launch coverage](https://www.techlusive.in/news/samsung-galaxy-tab-active-5-enterprise-edition-launched-in-india-at-rs-49999-check-specs-features-1587545/).
- Knox gives us kiosk lock-down, remote management and a long security-update window, which suits unattended outdoor posts.

**Minimum spec for any kiosk tablet (including cheaper indoor ones):**

| Item | Minimum |
|---|---|
| OS | Android 13+ with **Google Mobile Services** (needed for FCM push and Play Integrity) and at least 3 years of security updates remaining; preferably **Android Enterprise Recommended** |
| Camera | Front camera ≥ 5 MP with good low-light performance; ideally on the long edge for landscape use |
| Compute | 4 GB RAM or more; mid-range SoC (benchmark 1:N matching for 300 templates in the PoC) |
| Connectivity | Wi-Fi + **4G/5G SIM** (most client sites have no reliable Wi-Fi for vendors) |
| Power | Permanent power with **battery-protection charging mode** (stops at 80–85%) to avoid swollen batteries; survives power cuts on battery |
| Durability | Outdoor: IP65+ and drop-rated (rugged). Indoor: standard tablet plus a lockable enclosure |
| Lighting | An **LED panel or ring light** for night shifts at gates. Face accuracy depends heavily on illumination |
| Mounting | Anti-theft wall or pole enclosure at face height (adjustable 1.4–1.6 m), angled to avoid direct sun behind people |

**MDM (mandatory for kiosks):** Android Enterprise **dedicated-device mode** (single-app lock-task), remote wipe and disable, OS update control, and remote app install.
- Samsung **Knox Manage / Knox Suite** for Samsung fleets.
- A vendor-neutral MDM (e.g. Esper, Scalefusion, ManageEngine) if the fleet is mixed.
- Choose one in the PoC.

### 3.3 Android face terminals (option D): run our app on terminal hardware
- Purpose-built Android terminals (e.g. the **Telpo TPS980** series) provide better cameras, sometimes dual/IR cameras for liveness, relays for door control, and wall mounting.
- Third-party face apps are already installed on them. For example, [3DiVi's client for the TPS980Pro](https://docs.facemachine.3divi.com/docs/telpo_overview) is installed as an APK. [Esper](https://esper.io/devices/telpo-tps980t) lists single-app lock-down for the TPS980T.
- **Caveats to check before choosing:**
  - The Android version is often old (some listings show Android 9). Our minimum is API 26, and security updates are a concern.
  - **Many lack Google Mobile Services**, which means no FCM push and no Play Integrity. We would need MQTT/WebSocket or polling for push, and an alternative device-attestation approach.
  - Get vendor confirmation that **custom APK installation is supported and warranted**, and whether the camera SDK or IR stream is accessible.
- ZKTeco India also sells **Android-based terminals** (e.g. the Horus and FaceKiosk lines, [zkteco.in](https://www.zkteco.in/android-terminal)). Ask whether they allow third-party apps.
- **Recommendation:** evaluate one Android terminal in the PoC. Choose this option only if it passes the GMS/push and update-policy checks. Otherwise use options B/C for our app and option E for terminal-grade hardware.

### 3.4 Vendor biometric terminals (option E): integrate, don't replace
These run vendor firmware and **push punches to our cloud endpoint**. Our device-ingest adapters normalise them into standard punches (see [MULTI_CHANNEL_API.md](MULTI_CHANNEL_API.md) §7).

| Vendor family | Why | Integration path |
|---|---|---|
| **ZKTeco / eSSL** | Most common in India, low cost, wide dealer network. eSSL AI-Face models are reported to use the ZKTeco SpeedFace platform | **ADMS / Push protocol**: the device is configured with our server address and pushes punches over HTTP ([ZKTeco Push SDK reference](https://github.com/xeechaun/ZKTecoPushSDK), [eSSL AiFace ADMS example](https://get.clockit.io/essl/aiface-neptune)) |
| **Matrix COSEC** | **Indian manufacturer**, enterprise-grade, common in factories and IT parks. Suits clients (PSUs, regulated industries) that prefer non-Chinese-origin devices | **Ready Device API** for third-party integration with AES-256 encrypted transfer ([Matrix COSEC face deck](https://www.marketing.matrixcomsec.com/wp-content/uploads/2023/09/Matrix-COSEC-Face-Recognition-Presentation.pdf)). Confirm the push model and licensing with Matrix |
| **Hikvision** (and similar) | Common face terminals (e.g. DS-K1T671, roughly ₹8–18k in Indian listings) | ISAPI / HTTP event push. **Check the client's procurement policy** on device origin |

Indicative market prices: common face terminals such as the ZKTeco MB360 are around ₹6–8k, and the Hikvision DS-K1T671 around ₹8–18k ([2026 buyers' guide](https://www.waggex.com/blog/best-face-recognition-attendance-systems/)). Confirm with local dealers.

**Selection checklist for terminals:**
- **Cloud push** (not LAN-pull only).
- **4G or Wi-Fi option** for sites without LAN.
- Face + card + fingerprint (choose per site).
- **Visible-light face with anti-spoofing**.
- User capacity above the site headcount × 1.5.
- Battery backup.
- IP65 for outdoor installation.
- Time sync (NTP).
- Local storage of punches during outages, with later upload.
- Vendor **API documentation and licensing in writing**.

**Phasing:** build the first two adapters in **Phase 3** (ZKTeco/eSSL ADMS first, as the largest installed base; then Matrix COSEC). Add others on demand.

### 3.5 Recommended mix by tenant type

| Tenant | Primary | Secondary | Fallback |
|---|---|---|---|
| **Security agency** (many small client sites) | Guard's phone: face 1:1 + liveness + geofence | Rugged kiosk tablet (Tab Active5-class) at large sites or where guards have no smartphone; client's existing terminals via push | Supervisor marking with photo |
| **Boiler engineering** (engineers moving between plant sites) | Engineer's phone: face 1:1 + geo at site check-in and check-out, timesheet against the work order | Kiosk tablet or terminal at the company's own workshop and warehouse | Supervisor or project manager marking |
| **IT company** (offices in several states) | Existing office access/biometric terminals via push (Matrix/ZKTeco), or web/mobile check-in | Mobile check-in for WFH and field staff (face optional per policy) | Manager regularisation workflow |

---

## 4. Proof of concept (recommended before Phase 2 build)

**Duration:** about 4 weeks.

**Hardware to buy (indicative):**
- 2 low-end Android phones (₹8–12k class)
- 1 mid-range Android phone
- 1 iPhone
- 1 Galaxy Tab Active5 Enterprise Edition
- 1 standard 10–11" Android tablet
- 1 Android face terminal (Telpo-class)
- 1 ZKTeco/eSSL ADMS face terminal
- 1 Matrix COSEC face terminal (or a loaner from Matrix)
- LED light panel and a test enclosure

**SDKs:** 2–3 face SDKs from the shortlist (§2.4), under evaluation licences.

| Test | Pass criteria (proposed) |
|---|---|
| 1:1 verification, 50 volunteers × 3 lighting conditions (daylight, indoor, night with LED) | FRR ≤ 2% at FAR ≤ 0.01% (vendor-reported threshold confirmed by our test) |
| 1:N identification, 300 enrolled, kiosk tablet | Correct identification ≥ 98%; ≤ 1.5 s to result |
| Liveness spoof set (printed photo, phone-screen replay, video, paper mask) | 0 accepted spoofs in 200 attempts |
| Low-end phone performance | Capture → result ≤ 2 s; no ANRs; app size and memory within limits |
| Offline: 8 h without network, 500 punches queued | All synced without duplicates; clock-skew flags work |
| Terminal push (ADMS, Matrix) | Punches arrive at device-ingest within 60 s, mapped correctly to employees; offline backlog uploads after restore |
| Kiosk MDM | Lock-task enforced, remote disable and update work, recovers after a power cut |

**Output:** choose the SDK, kiosk tablet, MDM and the first terminal vendors. Then fill in [MULTI_CHANNEL_API.md](MULTI_CHANNEL_API.md) §10 and write an ADR for each choice.

---

## 5. Decisions recorded / pending

| # | Decision | Status |
|---|---|---|
| 1 | Mobile and kiosk technology: **Flutter**, one codebase with three modes | **Decided** |
| 2 | Primary biometric: **face**, with liveness; fingerprint/card through integrated vendor terminals only | Recommended, to confirm |
| 3 | Matching: **hybrid**: on-device for speed and offline, server re-verification for audit | Recommended, to confirm |
| 4 | Face SDK vendor | Pending PoC |
| 5 | Kiosk tablet model and MDM | Recommended: Galaxy Tab Active5 EE (rugged) + Knox; pending PoC |
| 6 | Terminal vendors for adapters | Recommended: ZKTeco/eSSL (ADMS) then Matrix COSEC; pending PoC and pilot-customer inventory |
| 7 | Android face terminal for our own app | Pending PoC (GMS/push and update-policy checks) |
