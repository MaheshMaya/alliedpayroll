package com.alliedpayroll.platform.core.audit;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * One row of the trail as a module records it. Values in {@code oldValues} and {@code newValues}
 * are already masked by the caller; the trail never masks, so it never sees the clear value.
 *
 * @param channel the channel name (WEB, MOBILE, KIOSK, DEVICE, PARTNER, SYSTEM)
 * @param entityLabel a code or number for lists, never PII
 */
public record AuditEntry(
    AuditAction action,
    AuditActor actor,
    String channel,
    @Nullable String entityType,
    @Nullable UUID entityId,
    @Nullable String entityLabel,
    @Nullable String clientIp,
    @Nullable String traceId,
    @Nullable UUID deviceId,
    @Nullable String appVersion,
    List<String> changedFields,
    @Nullable Map<String, Object> oldValues,
    @Nullable Map<String, Object> newValues,
    @Nullable String reasonKey,
    Map<String, Object> reasonParams,
    @Nullable String note) {

  public AuditEntry {
    changedFields = List.copyOf(changedFields);
    reasonParams = Map.copyOf(reasonParams);
    oldValues = oldValues == null ? null : Map.copyOf(oldValues);
    newValues = newValues == null ? null : Map.copyOf(newValues);
  }

  /** A security event: no entity, a reason, the request's origin. */
  public static AuditEntry security(
      AuditAction action,
      AuditActor actor,
      String channel,
      @Nullable String clientIp,
      @Nullable String reasonKey,
      Map<String, Object> reasonParams) {
    return new AuditEntry(
        action,
        actor,
        channel,
        null,
        null,
        null,
        clientIp,
        null,
        null,
        null,
        List.of(),
        null,
        null,
        reasonKey,
        reasonParams,
        null);
  }

  /** A change to one entity, with the masked before and after of the fields that changed. */
  public static AuditEntry change(
      AuditAction action,
      AuditActor actor,
      String channel,
      String entityType,
      UUID entityId,
      @Nullable String entityLabel,
      List<String> changedFields,
      @Nullable Map<String, Object> oldValues,
      @Nullable Map<String, Object> newValues,
      @Nullable String reasonKey) {
    return new AuditEntry(
        action,
        actor,
        channel,
        entityType,
        entityId,
        entityLabel,
        null,
        null,
        null,
        null,
        changedFields,
        oldValues,
        newValues,
        reasonKey,
        Map.of(),
        null);
  }
}
