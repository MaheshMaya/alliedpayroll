package com.alliedpayroll.platform.test;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * One PostgreSQL 17 container per test JVM, bootstrapped with the same role script as
 * docker-compose, so migrations run as {@code migrate_user} and the application as {@code app_user}
 * with row-level security really enforced.
 */
public final class PostgresTestDatabase {

  private static final PostgreSQLContainer CONTAINER =
      new PostgreSQLContainer("postgres:17-alpine")
          .withDatabaseName("alliedpayroll")
          .withUsername("postgres")
          .withPassword("postgres")
          .withInitScript("db/init/01-roles.sql")
          .withReuse(false);

  static {
    CONTAINER.start();
  }

  private PostgresTestDatabase() {}

  public static void register(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", () -> "app_user");
    registry.add("spring.datasource.password", () -> "app");
    registry.add("spring.flyway.url", CONTAINER::getJdbcUrl);
    registry.add("spring.flyway.user", () -> "migrate_user");
    registry.add("spring.flyway.password", () -> "migrate");
  }

  public static String jdbcUrl() {
    return CONTAINER.getJdbcUrl();
  }
}
