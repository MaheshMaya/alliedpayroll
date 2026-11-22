package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** A disabled user may sign in again. */
public record UserEnabled(UUID eventId, TenantId tenantId, UserId userId, Instant occurredAt)
    implements DomainEvent {}
