---
name: performance-and-scale
description: Use when writing list/query endpoints, batch jobs (payroll runs, imports, statutory files, reports), event listeners, caches, locks, scheduled jobs, or any screen that renders large tables or runs on low-end Android — and when a p95 or month-end load target is at stake. Covers PostgreSQL indexing and pagination, N+1 avoidance, Spring Batch partitioning, per-tenant fairness, Redis locks and idempotency, React table/render performance, Flutter low-end and offline performance, and the observability (metrics, traces, SLOs) that proves it.
---

# Performance, scale and observability

Targets from `docs/PROJECT_INITIATION.md` §5.6: 1,000 tenants, 500k employees, the largest tenant 50k; **10,000 paysheets in under 5 minutes**; **p95 < 300 ms** for list and detail APIs; 99.9% availability. Month-end is the load peak: attendance locks, payroll runs, bank files, invoices and payslip downloads all land in the same 72 hours.

## 1. Database access

- **Every tenant-owned index starts with `tenant_id`**, then the filter columns the list screen uses most (`tenant_id, legal_entity_id, period`; `tenant_id, site_id, date`). Composite indexes are designed with the screen, not after the slow query.
- Lists use **keyset (cursor) pagination** for anything that can exceed 10k rows (employees, punches, paysheets, audit) and offset pagination only for small masters. Cursor = `(sort column, id)`.
- **No N+1**: JPA fetch plans are explicit (`@EntityGraph` or JPQL `join fetch`) per use case; Hibernate statistics are on in tests and a test fails if a list endpoint issues more than *N+3* statements.
- Projections for lists: DTO queries (`select new …` or interface projections), never loading aggregates to render a table. Detail endpoints load the aggregate.
- Counts for KPI tiles come from **maintained summary tables or read models**, not `count(*)` over millions of rows at request time.
- Large tables are **partitioned** by `tenant_id` hash or by period: `punch`, `attendance_entry`, `paysheet_line`, `audit_event`, `outbox`. Partition pruning is verified with `EXPLAIN` in a migration test.
- JSONB columns (`*_i18n`, custom fields, settings) get GIN indexes only when queried; otherwise none.
- Migrations on big tables are online (`CREATE INDEX CONCURRENTLY`, batched backfills, no table rewrites in a single transaction).
- Connection pool sized per service from load tests (start: 20); payroll batch uses its own pool. `statement_timeout` 30 s for API, longer only inside batch steps.
- Read replicas serve the reporting module and exports; transactional modules never query replicas for writes-after-reads.

## 2. Batch and long-running work

- Anything over ~2 s returns `202` with a **job** resource (`/jobs/{id}`: status, progress, result link, error file). Clients poll with backoff or get a push/webhook. The UI shows it in the Background jobs panel.
- **Spring Batch** for payroll runs, imports, statutory files, bulk payslips and reports: chunk-oriented, **partitioned by employee range**, restartable from the last committed chunk, idempotent steps (re-running a step never double-posts).
- Payroll run: inputs **snapshotted first** (one pass), then compute in parallel partitions with virtual threads, then a single ordered write of lines and totals. Lock per `(tenant, group, period)` in Redis with a lease and fencing token; a second start returns `409` with the running job id.
- **Per-tenant fairness**: job queues are keyed by tenant and scheduled round-robin, with a cap on concurrent jobs per tenant, so one 50k-employee tenant can't starve others at month end.
- Event listeners (`@ApplicationModuleListener`) are idempotent (processed-event table), run off the request thread with bounded concurrency, and incomplete publications are resubmitted on startup; registry backlog is a paged alert. The same rules apply to Kafka consumers when Kafka arrives (Phase 9).
- Scheduled jobs (expiry alerts, retention purges, challan reminders) use ShedLock; they page through tenants and never load all tenants' data at once.

## 3. Caching, idempotency, locks

- Cache only what is read far more than written and tolerates staleness measured in minutes: tenant settings, feature flags, terminology bundles, rule sets (keyed by version), holiday calendars. Invalidate by event, with a TTL as backstop. Cache keys always include `tenantId`.
- Never cache per-user authorization decisions longer than the token lifetime; never cache PII.
- `Idempotency-Key` results are stored in Redis for 24–72 h with the response hash; a replay with a different body is `422`.
- Distributed locks only where correctness needs them (payroll run, number series, kiosk provisioning); everything else uses optimistic locking (`@Version` + `If-Match`).

## 4. API shape for speed

- `fields=` projection on mobile-facing lists; `If-None-Match` on bootstrap and reference data; gzip/brotli at the ingress.
- Delta sync endpoints return changes since a cursor plus tombstones; never full roster downloads after the first sync.
- Rate limits per tenant, channel and client in the backend's rate limiter (Bucket4j on Redis) plus coarse limits at the ingress, with burst allowance at shift change for kiosk sync.
- Cross-module calls on a request path go through the module's `api` and stay one level deep; payroll snapshots its inputs once at run start, and `backend-batch` keeps month-end load off the API pods.

## 5. Web (React)

- Tables over ~200 rows are **virtualised** (TanStack Virtual) with server-side sort/filter/pagination; column projections requested from the API.
- Route-level code splitting; the app shell loads first; heavy modules (reports, attendance grid) are lazy.
- TanStack Query with sensible `staleTime` per resource; mutations invalidate narrowly; optimistic updates only for low-risk edits (never approvals).
- Avoid re-render storms: memoise row renderers, keep derived values in selectors, no inline object props on hot paths. Profile with React DevTools before optimising.
- Budgets: initial JS < 250 kB gzipped for the shell; LCP < 2.5 s on a mid-range laptop over 4G; interaction to next paint < 200 ms on the attendance grid.

## 6. Mobile and kiosk (Flutter)

- Baseline device: ₹8–12k Android, 2–3 GB RAM. Targets: cold start < 2 s, check-in capture→result < 2 s, 1:N on the kiosk ≤ 1.5 s for 300 templates, no jank (> 16 ms frames) on list scroll.
- Lists are `ListView.builder`/slivers with `const` widgets; images downscaled and cached; no rebuilding whole screens on a provider change (select narrowly).
- Offline queue writes are batched in Drift transactions; sync is a single batched request; background sync respects battery and metered-network policy.
- App size budget < 40 MB download; face SDK models bundled lazily per mode.
- Measure with `flutter run --profile` and DevTools before and after; golden performance checks run on the device farm.

## 7. Observability (proving the above)

- **Metrics** (Micrometer → Prometheus): request latency histograms per endpoint and tenant tier, DB pool usage, consumer lag, job duration and throughput (paysheets/s), cache hit ratio, outbox backlog, OTP/SMS provider latency.
- **Traces** (OpenTelemetry): every request and job; `tenantId`, `channel`, `appVersion`, hashed `deviceId` as attributes; DB statements as spans (parameters off).
- **Logs**: structured, sampled at debug, correlated by `traceId`; no PII.
- **SLOs** per channel with error budgets: API p95 < 300 ms and availability 99.9%; payroll job success 99.9% within the time target; kiosk sync p95 < 2 s. Alerts page on burn rate, not on single thresholds.
- Month-end **load test** in CI nightly against `staging` with the 50k-employee synthetic tenant: payroll run, 5k concurrent payslip downloads, 20k punches/10 min. A regression over 20% blocks the release.

## 8. Review checklist

- [ ] New query has an index starting with `tenant_id` matching its filters; `EXPLAIN` checked on a realistic data volume
- [ ] List endpoint paginates (cursor for large sets), projects, and has an N+1 test
- [ ] Work over 2 s is a job; batch steps are restartable and idempotent
- [ ] Locks and caches keyed by tenant; invalidation on event; TTL backstop
- [ ] Mobile/kiosk endpoint supports ETag, `fields=`, delta cursor
- [ ] Metrics and trace attributes added for the new path; dashboard panel updated
- [ ] Budgets (§5, §6) measured, not assumed
