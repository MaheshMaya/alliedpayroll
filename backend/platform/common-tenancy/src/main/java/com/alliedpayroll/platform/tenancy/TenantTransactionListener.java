package com.alliedpayroll.platform.tenancy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionExecutionListener;
import org.springframework.transaction.TransactionSystemException;

/**
 * Sets {@code app.tenant_id} with transaction scope on the connection of every transaction, so
 * PostgreSQL row-level security sees the bound tenant. Work outside a transaction sees no tenant
 * rows, which is the intended fail-closed behaviour.
 */
public final class TenantTransactionListener implements TransactionExecutionListener {

  private static final String SET_TENANT = "SELECT set_config('app.tenant_id', ?, true)";

  private final DataSource dataSource;

  public TenantTransactionListener(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void afterBegin(TransactionExecution transaction, @Nullable Throwable beginFailure) {
    if (beginFailure != null) {
      return;
    }
    TenantContext.current()
        .ifPresent(tenant -> {
          Connection connection = DataSourceUtils.getConnection(dataSource);
          try (PreparedStatement statement = connection.prepareStatement(SET_TENANT)) {
            statement.setString(1, tenant.id().toString());
            statement.execute();
          } catch (SQLException e) {
            throw new TransactionSystemException("could not bind tenant to transaction", e);
          } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
          }
        });
  }
}
