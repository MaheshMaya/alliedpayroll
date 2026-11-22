package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** A provisioned user signed in for the first time and is now linked to their Keycloak subject. */
public record UserActivated(UUID eventId, TenantId tenantId, UserId userId, Instant occurredAt)
    implements DomainEvent {}
