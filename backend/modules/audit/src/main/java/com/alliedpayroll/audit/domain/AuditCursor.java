package com.alliedpayroll.audit.domain;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/** Keyset position in the trail: the last row's time and id, opaque to clients. */
public record AuditCursor(Instant occurredAt, UUID id) {

  public String encode() {
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString((occurredAt + "|" + id).getBytes(StandardCharsets.UTF_8));
  }

  public static Optional<AuditCursor> decode(String encoded) {
    try {
      String raw = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
      int bar = raw.indexOf('|');
      if (bar <= 0) {
        return Optional.empty();
      }
      return Optional.of(
          new AuditCursor(
              Instant.parse(raw.substring(0, bar)), UUID.fromString(raw.substring(bar + 1))));
    } catch (IllegalArgumentException | java.time.format.DateTimeParseException e) {
      return Optional.empty();
    }
  }
}
