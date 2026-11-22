package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** An administrator invited a person; they exist in Keycloak and activate on first sign-in. */
public record UserInvited(UUID eventId, TenantId tenantId, UserId userId, Instant occurredAt)
    implements DomainEvent {}
