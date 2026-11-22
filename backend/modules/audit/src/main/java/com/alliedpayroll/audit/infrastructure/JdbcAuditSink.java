package com.alliedpayroll.audit.infrastructure;

import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import java.sql.Types;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

/**
 * Inserts the row in the caller's transaction, or in one of its own when the caller has none (a
 * denial, a sign-in). The tenant comes from the bound context through the transaction listener.
 */
@Component
class JdbcAuditSink implements AuditSink {

  private static final String INSERT =
      """
      INSERT INTO audit.audit_event (id, tenant_id, occurred_at, action, entity_type, entity_id,
        entity_label, actor_type, actor_id, channel, device_id, app_version, client_ip, trace_id,
        changed_fields, old_values, new_values, reason_key, reason_params, note)
      VALUES (?, platform.current_tenant(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::inet, ?, ?, ?::jsonb,
        ?::jsonb, ?, ?::jsonb, ?)
      """;

  private final JdbcTemplate jdbc;
  private final TransactionTemplate transactions;
  private final IdGenerator ids;
  private final Clock clock;
  private final ObjectMapper json;

  JdbcAuditSink(
      JdbcTemplate jdbc,
      TransactionTemplate transactions,
      IdGenerator ids,
      Clock clock,
      ObjectMapper json) {
    this.jdbc = jdbc;
    this.transactions = transactions;
    this.ids = ids;
    this.clock = clock;
    this.json = json;
  }

  @Override
  public void record(AuditEntry entry) {
    transactions.executeWithoutResult(status -> insert(entry));
  }

  private void insert(AuditEntry entry) {
    jdbc.update(
        INSERT,
        ps -> {
          ps.setObject(1, ids.next());
          ps.setObject(2, OffsetDateTime.now(clock));
          ps.setString(3, entry.action().name());
          ps.setString(4, entry.entityType());
          ps.setObject(5, entry.entityId());
          ps.setString(6, entry.entityLabel());
          ps.setString(7, entry.actor().type().name());
          ps.setObject(8, entry.actor().id());
          ps.setString(9, entry.channel());
          ps.setObject(10, entry.deviceId());
          ps.setString(11, entry.appVersion());
          ps.setString(12, entry.clientIp());
          ps.setString(13, entry.traceId());
          ps.setArray(
              14,
              ps.getConnection()
                  .createArrayOf("text", entry.changedFields().toArray(String[]::new)));
          ps.setString(15, toJson(entry.oldValues()));
          ps.setString(16, toJson(entry.newValues()));
          ps.setString(17, entry.reasonKey());
          ps.setString(18, toJson(entry.reasonParams()));
          if (entry.note() == null) {
            ps.setNull(19, Types.VARCHAR);
          } else {
            ps.setString(19, entry.note());
          }
        });
  }

  private @Nullable String toJson(@Nullable Map<String, Object> values) {
    return values == null ? null : json.writeValueAsString(values);
  }
}
