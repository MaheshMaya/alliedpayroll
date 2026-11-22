package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** A tenant-defined role was created or now grants a different set of permissions. */
public record RolePermissionsChanged(
    UUID eventId, TenantId tenantId, UUID roleId, Instant occurredAt) implements DomainEvent {}
