package com.alliedpayroll.tenant.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** An optional module was switched on or off. Switching off hides it; its data stays. */
public record ModuleToggled(
    UUID eventId, TenantId tenantId, String moduleCode, boolean enabled, Instant occurredAt)
    implements DomainEvent {}
