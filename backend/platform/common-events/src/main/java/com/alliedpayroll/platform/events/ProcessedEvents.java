package com.alliedpayroll.platform.events;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Idempotency guard for listeners: the first call for a (listener, event) pair records it inside
 * the listener's transaction and returns true; a redelivery returns false and the listener skips.
 */
@Component
public class ProcessedEvents {

  private static final String INSERT =
      """
      INSERT INTO platform.processed_event (listener_id, publication_id, processed_at)
      VALUES (?, ?, ?) ON CONFLICT DO NOTHING
      """;

  private final JdbcTemplate jdbc;
  private final Clock clock;

  public ProcessedEvents(JdbcTemplate jdbc, Clock clock) {
    this.jdbc = jdbc;
    this.clock = clock;
  }

  public boolean markProcessed(String listenerId, UUID eventId) {
    return jdbc.update(INSERT, listenerId, eventId, OffsetDateTime.now(clock)) == 1;
  }
}
