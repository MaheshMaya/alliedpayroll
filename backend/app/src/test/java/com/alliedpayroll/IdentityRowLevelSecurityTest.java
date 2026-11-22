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

/** The identity tables are isolated by the database itself, like every tenant-owned table. */
@SpringBootTest
@ActiveProfiles("test")
class IdentityRowLevelSecurityTest {

  private static final CurrentTenant SENTINEL = TenantTesting.tenant("sentinel");
  private static final CurrentTenant NIMBUS = TenantTesting.tenant("nimbus-it");
  private static final String INSERT =
      """
      INSERT INTO identity.app_user (id, tenant_id, user_type, username, display_name, status,
        version, created_at, created_by, updated_at, updated_by)
      VALUES (?, ?, 'EMPLOYEE', ?, 'Row Owner', 'INVITED',
        0, ?, platform.system_actor(), ?, platform.system_actor())
      """;
  private static final String COUNT = "SELECT count(*) FROM identity.app_user WHERE username = ?";

  @Autowired private JdbcTemplate jdbc;
  @Autowired private TransactionTemplate transactions;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @Test
  void usersAreVisibleOnlyToTheirTenant() {
    String username = "user-" + UUID.randomUUID();
    insertAs(SENTINEL, username);
    assertThat(countAs(SENTINEL, username)).isEqualTo(1);
    assertThat(countAs(NIMBUS, username)).isZero();
  }

  @Test
  void theSameUsernameMayExistInTwoTenants() {
    String username = "shared-" + UUID.randomUUID();
    insertAs(SENTINEL, username);
    insertAs(NIMBUS, username);
    assertThat(countAs(SENTINEL, username)).isEqualTo(1);
    assertThat(countAs(NIMBUS, username)).isEqualTo(1);
  }

  @Test
  void tenantCannotWriteAnotherTenantsUser() {
    assertThatThrownBy(() ->
            TenantTesting.runAs(
                SENTINEL,
                () ->
                    transactions.executeWithoutResult(
                        status -> insert(NIMBUS, "user-" + UUID.randomUUID()))))
        .rootCause()
        .hasMessageContaining("row-level security");
  }

  private void insertAs(CurrentTenant tenant, String username) {
    TenantTesting.runAs(
        tenant, () -> transactions.executeWithoutResult(status -> insert(tenant, username)));
  }

  private int countAs(CurrentTenant tenant, String username) {
    return TenantTesting.callAs(
        tenant, () -> Objects.requireNonNull(transactions.execute(status -> count(username))));
  }

  private void insert(CurrentTenant owner, String username) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    jdbc.update(INSERT, UUID.randomUUID(), owner.id().value(), username, now, now);
  }

  private int count(String username) {
    return Objects.requireNonNull(jdbc.queryForObject(COUNT, Integer.class, username));
  }
}
