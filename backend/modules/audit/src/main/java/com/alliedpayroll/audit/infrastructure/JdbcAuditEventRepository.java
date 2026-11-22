package com.alliedpayroll.audit.infrastructure;

import com.alliedpayroll.audit.domain.AuditEvent;
import com.alliedpayroll.audit.domain.AuditEventRepository;
import com.alliedpayroll.audit.domain.AuditQuery;
import com.alliedpayroll.platform.core.audit.ActorType;
import com.alliedpayroll.platform.core.audit.AuditAction;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/** Keyset reads over the partitioned table; row-level security scopes them to the tenant. */
@Repository
class JdbcAuditEventRepository implements AuditEventRepository {

  private static final String SELECT =
      """
      SELECT id, occurred_at, action, entity_type, entity_id, entity_label, actor_type, actor_id,
             channel, host(client_ip) AS client_ip, changed_fields, old_values::text, new_values::text,
             reason_key, reason_params::text, note
      FROM audit.audit_event
      """;
  private static final String SECURITY_ACTIONS =
      "('LOGIN','LOGIN_FAILED','LOGOUT','OTP_SENT','OTP_FAILED','AUTHZ_DENIED','PII_REVEAL')";
  private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {};

  private final JdbcTemplate jdbc;
  private final ObjectMapper json;

  JdbcAuditEventRepository(JdbcTemplate jdbc, ObjectMapper json) {
    this.jdbc = jdbc;
    this.json = json;
  }

  @Override
  public List<AuditEvent> find(AuditQuery query) {
    StringBuilder sql = new StringBuilder(SELECT).append(" WHERE TRUE");
    List<Object> args = new ArrayList<>();
    if (query.action() != null) {
      sql.append(" AND action = ?");
      args.add(query.action().name());
    }
    if (query.actorId() != null) {
      sql.append(" AND actor_id = ?");
      args.add(query.actorId());
    }
    if (query.entityType() != null) {
      sql.append(" AND entity_type = ?");
      args.add(query.entityType());
    }
    if (query.entityId() != null) {
      sql.append(" AND entity_id = ?");
      args.add(query.entityId());
    }
    if (query.from() != null) {
      sql.append(" AND occurred_at >= ?");
      args.add(OffsetDateTime.ofInstant(query.from(), java.time.ZoneOffset.UTC));
    }
    if (query.to() != null) {
      sql.append(" AND occurred_at < ?");
      args.add(OffsetDateTime.ofInstant(query.to(), java.time.ZoneOffset.UTC));
    }
    if (!query.includeSecurity()) {
      sql.append(" AND action NOT IN ")
          .append(SECURITY_ACTIONS)
          .append(" AND (entity_type IS NULL OR entity_type NOT IN ('USER','ROLE'))");
    }
    if (query.after() != null) {
      sql.append(" AND (occurred_at, id) < (?, ?)");
      args.add(OffsetDateTime.ofInstant(query.after().occurredAt(), java.time.ZoneOffset.UTC));
      args.add(query.after().id());
    }
    sql.append(" ORDER BY occurred_at DESC, id DESC LIMIT ?");
    args.add(query.size());
    return jdbc.query(sql.toString(), this::toEvent, args.toArray());
  }

  private AuditEvent toEvent(ResultSet rs, int rowNum) throws SQLException {
    return new AuditEvent(
        rs.getObject("id", UUID.class),
        rs.getObject("occurred_at", OffsetDateTime.class).toInstant(),
        AuditAction.valueOf(rs.getString("action")),
        rs.getString("entity_type"),
        rs.getObject("entity_id", UUID.class),
        rs.getString("entity_label"),
        ActorType.valueOf(rs.getString("actor_type")),
        rs.getObject("actor_id", UUID.class),
        rs.getString("channel"),
        rs.getString("client_ip"),
        strings(rs.getArray("changed_fields")),
        map(rs.getString("old_values")),
        map(rs.getString("new_values")),
        rs.getString("reason_key"),
        Map.copyOf(
            java.util.Objects.requireNonNullElse(map(rs.getString("reason_params")), Map.of())),
        rs.getString("note"));
  }

  private static List<String> strings(@Nullable Array array) throws SQLException {
    if (array == null) {
      return List.of();
    }
    return Arrays.asList((String[]) array.getArray());
  }

  private @Nullable Map<String, Object> map(@Nullable String text) {
    return text == null ? null : json.readValue(text, MAP);
  }
}
