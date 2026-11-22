package com.alliedpayroll.audit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuditCursorTest {

  @Test
  void roundTrips() {
    AuditCursor cursor =
        new AuditCursor(Instant.parse("2026-11-22T04:00:00.123456Z"), UUID.randomUUID());
    assertThat(AuditCursor.decode(cursor.encode())).contains(cursor);
  }

  @Test
  void rejectsWhatItDidNotProduce() {
    assertThat(AuditCursor.decode("not-a-cursor")).isEmpty();
    assertThat(AuditCursor.decode("")).isEmpty();
  }
}
