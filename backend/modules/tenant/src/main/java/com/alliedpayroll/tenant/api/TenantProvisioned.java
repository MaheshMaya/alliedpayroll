package com.alliedpayroll.tenant.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** A tenant row was created. Modules that keep per-tenant defaults seed them on this. */
public record TenantProvisioned(
    UUID eventId, TenantId tenantId, String slug, String industryProfileCode, Instant occurredAt)
    implements DomainEvent {}
