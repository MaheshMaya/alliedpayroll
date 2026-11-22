package com.alliedpayroll.audit.domain;

import com.alliedpayroll.platform.core.audit.AuditAction;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * Filters of a trail read. {@code includeSecurity} is decided by the caller's permissions, never by
 * the request.
 */
public record AuditQuery(
    @Nullable AuditAction action,
    @Nullable UUID actorId,
    @Nullable String entityType,
    @Nullable UUID entityId,
    @Nullable Instant from,
    @Nullable Instant to,
    boolean includeSecurity,
    @Nullable AuditCursor after,
    int size) {}
