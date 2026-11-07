package com.alliedpayroll.platform.events;

import com.alliedpayroll.platform.core.ids.TenantId;
import java.time.Instant;
import java.util.UUID;

/**
 * What every module event carries. Events are records in the module's {@code api} package, named
 * {@code {Aggregate}{PastTenseVerb}}, published in the writing transaction and persisted by the
 * Modulith registry (the outbox).
 */
public interface DomainEvent {

  UUID eventId();

  TenantId tenantId();

  Instant occurredAt();
}
