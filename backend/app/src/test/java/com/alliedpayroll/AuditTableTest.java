package com.alliedpayroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alliedpayroll.audit.application.AuditPartitionUpkeep;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditActor;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.test.PostgresTestDatabase;
import com.alliedpayroll.platform.test.TenantTesting;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

/** The table itself: append-only for the application role, isolated by tenant, routed by month. */
@SpringBootTest
@ActiveProfiles("test")
class AuditTableTest {

  private static final CurrentTenant SENTINEL = TenantTesting.tenant("sentinel");
  private static final CurrentTenant NIMBUS = TenantTesting.tenant("nimbus-it");

  @Autowired private AuditSink audit;
  @Autowired private JdbcTemplate jdbc;
  @Autowired private TransactionTemplate transactions;
  @Autowired private AuditPartitionUpkeep upkeep;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @Test
  void rowsBelongToTheTenantThatWroteThemAndLandInTheMonthsPartition() {
    String ip = "10.0.0." + (int) (Math.random() * 200);
    TenantTesting.runAs(SENTINEL, () -> audit.record(security(ip)));

    assertThat(countAs(SENTINEL, ip)).isEqualTo(1);
    assertThat(countAs(NIMBUS, ip)).isZero();
    String partition =
        TenantTesting.callAs(
            SENTINEL,
            () ->
                Objects.requireNonNull(
                    transactions.execute(status ->
                        Objects.requireNonNull(
                            jdbc.queryForObject(
                                "SELECT tableoid::regclass::text FROM audit.audit_event"
                                    + " WHERE host(client_ip) = ?",
                                String.class,
                                ip)))));
    assertThat(partition).startsWith("audit.audit_event_").doesNotContain("default");
  }

  @Test
  void applicationRoleCannotChangeOrRemoveRows() {
    assertThatThrownBy(() ->
            TenantTesting.runAs(
                SENTINEL,
                () ->
                    transactions.executeWithoutResult(
                        status -> jdbc.update("UPDATE audit.audit_event SET note = 'x'"))))
        .rootCause()
        .hasMessageContaining("permission denied");
    assertThatThrownBy(() ->
            TenantTesting.runAs(
                SENTINEL,
                () ->
                    transactions.executeWithoutResult(
                        status -> jdbc.update("DELETE FROM audit.audit_event"))))
        .rootCause()
        .hasMessageContaining("permission denied");
  }

  @Test
  void upkeepCreatesTheMonthsAheadAndRunsAgainWithoutError() {
    upkeep.run();
    upkeep.run();
    YearMonth next = YearMonth.now(ZoneOffset.UTC).plusMonths(3);
    Integer exists =
        jdbc.queryForObject(
            "SELECT count(*) FROM pg_tables WHERE schemaname = 'audit' AND tablename = ?",
            Integer.class,
            "audit_event_" + next.getYear() + "_" + String.format("%02d", next.getMonthValue()));
    assertThat(exists).isEqualTo(1);
  }

  private int countAs(CurrentTenant tenant, String ip) {
    return TenantTesting.callAs(
        tenant,
        () ->
            Objects.requireNonNull(
                transactions.execute(status ->
                    Objects.requireNonNull(
                        jdbc.queryForObject(
                            "SELECT count(*) FROM audit.audit_event WHERE host(client_ip) = ?",
                            Integer.class,
                            ip)))));
  }

  private static AuditEntry security(String ip) {
    return AuditEntry.security(
        AuditAction.LOGIN,
        AuditActor.UNKNOWN_USER,
        "WEB",
        ip,
        null,
        Map.of("subject", UUID.randomUUID()));
  }
}
