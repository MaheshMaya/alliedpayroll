package com.alliedpayroll.audit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliedpayroll.platform.core.audit.ActorType;
import com.alliedpayroll.platform.core.audit.AuditAction;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class AuditEventTest {

  @Test
  void signInsAndIdentityChangesAreSecuritySensitive() {
    assertThat(event(AuditAction.LOGIN, null).isSecuritySensitive()).isTrue();
    assertThat(event(AuditAction.STATE_CHANGE, "USER").isSecuritySensitive()).isTrue();
    assertThat(event(AuditAction.UPDATE, "EMPLOYEE").isSecuritySensitive()).isFalse();
  }

  private static AuditEvent event(AuditAction action, @Nullable String entityType) {
    return new AuditEvent(
        UUID.randomUUID(),
        Instant.EPOCH,
        action,
        entityType,
        null,
        null,
        ActorType.USER,
        UUID.randomUUID(),
        "WEB",
        null,
        List.of(),
        null,
        null,
        null,
        Map.of(),
        null);
  }
}
