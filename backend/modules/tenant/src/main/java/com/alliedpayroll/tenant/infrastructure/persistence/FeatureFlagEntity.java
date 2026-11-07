package com.alliedpayroll.tenant.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.TenantId;

/**
 * Row of {@code tenant.feature_flag}; tenant-owned, so Hibernate filters it by the bound tenant.
 */
@Entity
@Table(schema = "tenant", name = "feature_flag")
class FeatureFlagEntity {

  @Id private UUID id;

  @TenantId
  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "flag_key", nullable = false, length = 60)
  private String flagKey;

  @Column(nullable = false)
  private boolean enabled;

  protected FeatureFlagEntity() {
    // JPA
  }

  String flagKey() {
    return flagKey;
  }

  boolean enabled() {
    return enabled;
  }
}
