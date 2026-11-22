package com.alliedpayroll.audit.domain;

import com.alliedpayroll.platform.core.audit.ActorType;
import com.alliedpayroll.platform.core.audit.AuditAction;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/** One stored row of the trail, as the API serves it. */
public record AuditEvent(
    UUID id,
    Instant occurredAt,
    AuditAction action,
    @Nullable String entityType,
    @Nullable UUID entityId,
    @Nullable String entityLabel,
    ActorType actorType,
    UUID actorId,
    String channel,
    @Nullable String clientIp,
    List<String> changedFields,
    @Nullable Map<String, Object> oldValues,
    @Nullable Map<String, Object> newValues,
    @Nullable String reasonKey,
    Map<String, Object> reasonParams,
    @Nullable String note) {

  /** Rows about people and permissions; reading them needs {@code audit:security_read}. */
  public boolean isSecuritySensitive() {
    return action.isSecurityEvent() || "USER".equals(entityType) || "ROLE".equals(entityType);
  }
}
