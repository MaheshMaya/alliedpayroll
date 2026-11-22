package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** A user was disabled: signed out of Keycloak and refused on every tenant API call. */
public record UserDisabled(UUID eventId, TenantId tenantId, UserId userId, Instant occurredAt)
    implements DomainEvent {}
