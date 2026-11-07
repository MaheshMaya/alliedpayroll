package com.alliedpayroll.platform.tenancy;

import java.util.UUID;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Second isolation layer: entities annotated {@code @TenantId} are filtered and stamped by
 * Hibernate with the bound tenant. Platform work without a tenant runs as Hibernate's root tenant,
 * which skips the entity filter; row-level security still shows it no tenant rows.
 */
public final class HibernateTenantResolver implements CurrentTenantIdentifierResolver<UUID> {

  static final UUID PLATFORM_ROOT = new UUID(0L, 0L);

  @Override
  public UUID resolveCurrentTenantIdentifier() {
    return TenantContext.current().map(tenant -> tenant.id().value()).orElse(PLATFORM_ROOT);
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return false;
  }

  @Override
  public boolean isRoot(UUID tenantId) {
    return PLATFORM_ROOT.equals(tenantId);
  }
}
