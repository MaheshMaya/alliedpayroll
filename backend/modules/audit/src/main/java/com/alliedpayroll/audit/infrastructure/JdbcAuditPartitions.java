package com.alliedpayroll.audit.infrastructure;

import com.alliedpayroll.audit.domain.AuditPartitions;
import java.time.YearMonth;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * The partition DDL runs as the owning role through a definer function; the app role only calls it.
 */
@Repository
class JdbcAuditPartitions implements AuditPartitions {

  private final JdbcTemplate jdbc;

  JdbcAuditPartitions(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public void ensureFrom(YearMonth month, int monthsAhead) {
    for (int offset = 0; offset <= monthsAhead; offset++) {
      jdbc.queryForObject(
          "SELECT audit.ensure_partition(?)", String.class, month.plusMonths(offset).atDay(1));
    }
  }

  @Override
  public long rowsInDefaultPartition() {
    return Objects.requireNonNull(
        jdbc.queryForObject("SELECT count(*) FROM ONLY audit.audit_event_default", Long.class));
  }
}
