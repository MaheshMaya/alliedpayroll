package com.alliedpayroll.tenant.api;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.events.DomainEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** The tenant relabelled concepts in one locale; caches of that bundle are stale. */
public record TerminologyChanged(
    UUID eventId, TenantId tenantId, String locale, List<String> keys, Instant occurredAt)
    implements DomainEvent {

  public TerminologyChanged {
    keys = List.copyOf(keys);
  }
}
