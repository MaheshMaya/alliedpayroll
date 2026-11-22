package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** The roles of a user were replaced; their permissions change from their next request. */
public record UserRolesChanged(UUID eventId, TenantId tenantId, UserId userId, Instant occurredAt)
    implements DomainEvent {}
