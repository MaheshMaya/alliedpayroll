package com.alliedpayroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.test.PostgresTestDatabase;
import com.alliedpayroll.platform.test.TenantTesting;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

/**
 * Proves the database itself isolates tenants for the application role, independent of any
 * application code: the migrations enabled RLS, the transaction listener binds the tenant, and
 * {@code app_user} cannot switch it off.
 */
@SpringBootTest
@ActiveProfiles("test")
class RowLevelSecurityTest {

  private static final CurrentTenant SENTINEL = TenantTesting.tenant("sentinel");
  private static final CurrentTenant NIMBUS = TenantTesting.tenant("nimbus-it");

  private static final String INSERT =
      """
      INSERT INTO tenant.feature_flag (id, tenant_id, flag_key, enabled, config, version,
        created_at, created_by, updated_at, updated_by)
      VALUES (?, ?, ?, true, '{}', 0, ?, platform.system_actor(), ?, platform.system_actor())
      """;
  private static final String COUNT = "SELECT count(*) FROM tenant.feature_flag WHERE flag_key = ?";

  @Autowired private JdbcTemplate jdbc;
  @Autowired private TransactionTemplate transactions;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @Test
  void rowsAreVisibleOnlyToTheTenantThatWroteThem() {
    String flag = "module:" + UUID.randomUUID();
    insertFlagAs(SENTINEL, flag);

    assertThat(countFlagsAs(SENTINEL, flag)).isEqualTo(1);
    assertThat(countFlagsAs(NIMBUS, flag)).isZero();
  }

  @Test
  void workOutsideATenantSeesNoTenantRows() {
    String flag = "module:" + UUID.randomUUID();
    insertFlagAs(SENTINEL, flag);

    int seen = transactions.execute(status -> count(flag));
    assertThat(seen).isZero();
  }

  @Test
  void aTenantCannotWriteAnotherTenantsRow() {
    assertThatThrownBy(() ->
            TenantTesting.runAs(
                SENTINEL,
                () ->
                    transactions.executeWithoutResult(
                        status -> insert(NIMBUS, "module:" + UUID.randomUUID()))))
        .rootCause()
        .hasMessageContaining("row-level security");
  }

  @Test
  void applicationRoleCannotDisableRowLevelSecurity() {
    assertThatThrownBy(
            () -> jdbc.execute("ALTER TABLE tenant.feature_flag DISABLE ROW LEVEL SECURITY"))
        .rootCause()
        .hasMessageContaining("must be owner");
  }

  private void insertFlagAs(CurrentTenant tenant, String flag) {
    TenantTesting.runAs(
        tenant, () -> transactions.executeWithoutResult(status -> insert(tenant, flag)));
  }

  private int countFlagsAs(CurrentTenant tenant, String flag) {
    return TenantTesting.callAs(
        tenant, () -> Objects.requireNonNull(transactions.execute(status -> count(flag))));
  }

  private void insert(CurrentTenant owner, String flag) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    jdbc.update(INSERT, UUID.randomUUID(), owner.id().value(), flag, now, now);
  }

  private int count(String flag) {
    return Objects.requireNonNull(jdbc.queryForObject(COUNT, Integer.class, flag));
  }
}
