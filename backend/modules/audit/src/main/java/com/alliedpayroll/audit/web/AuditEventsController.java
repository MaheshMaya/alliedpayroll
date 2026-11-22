package com.alliedpayroll.audit.web;

import com.alliedpayroll.audit.application.AuditQueryService;
import com.alliedpayroll.audit.domain.AuditCursor;
import com.alliedpayroll.audit.domain.AuditEvent;
import com.alliedpayroll.audit.domain.AuditPermissions;
import com.alliedpayroll.audit.domain.AuditQuery;
import com.alliedpayroll.platform.core.audit.ActorType;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiPaths.API_V1 + "/audit-events")
@Tag(name = "Audit", description = "The trail of changes and security events, newest first")
class AuditEventsController {

  private final AuditQueryService audit;

  AuditEventsController(AuditQueryService audit) {
    this.audit = audit;
  }

  record AuditEventView(
      UUID id,
      Instant occurredAt,
      AuditAction action,
      @Nullable String entityType,
      @Nullable UUID entityId,
      @Nullable String entityLabel,
      ActorType actorType,
      UUID actorId,
      @Nullable String actorDisplayName,
      String channel,
      @Nullable String clientIp,
      List<String> changedFields,
      @Nullable Map<String, Object> oldValues,
      @Nullable Map<String, Object> newValues,
      @Nullable String reasonKey,
      Map<String, Object> reasonParams,
      @Nullable String note) {

    static AuditEventView of(AuditEvent event, Map<UUID, String> names) {
      return new AuditEventView(
          event.id(),
          event.occurredAt(),
          event.action(),
          event.entityType(),
          event.entityId(),
          event.entityLabel(),
          event.actorType(),
          event.actorId(),
          names.get(event.actorId()),
          event.channel(),
          event.clientIp(),
          event.changedFields(),
          event.oldValues(),
          event.newValues(),
          event.reasonKey(),
          event.reasonParams(),
          event.note());
    }
  }

  @GetMapping
  @RequiresPermission(AuditPermissions.READ)
  @Operation(
      operationId = "listAuditEvents",
      summary = "Trail of the tenant, newest first; security rows need audit:security_read")
  PageResponse<AuditEventView> list(
      @Parameter(name = "filter[action]") @RequestParam(name = "filter[action]", required = false)
          @Nullable AuditAction action,
      @Parameter(name = "filter[actorId]") @RequestParam(name = "filter[actorId]", required = false)
          @Nullable UUID actorId,
      @Parameter(name = "filter[entityType]")
          @RequestParam(name = "filter[entityType]", required = false)
          @Nullable String entityType,
      @Parameter(name = "filter[entityId]")
          @RequestParam(name = "filter[entityId]", required = false)
          @Nullable UUID entityId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          @Nullable Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          @Nullable Instant to,
      @RequestParam(required = false) @Nullable String cursor,
      @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
    AuditCursor after = cursor == null ? null : AuditCursor.decode(cursor).orElse(null);
    AuditQueryService.Listing listing =
        audit.list(
            new AuditQuery(action, actorId, entityType, entityId, from, to, false, after, size));
    List<AuditEventView> items =
        listing.items().stream()
            .map(event -> AuditEventView.of(event, listing.actorNames()))
            .toList();
    String next =
        listing.hasMore() && !items.isEmpty()
            ? new AuditCursor(items.getLast().occurredAt(), items.getLast().id()).encode()
            : null;
    return new PageResponse<>(items, next, items.size());
  }
}
