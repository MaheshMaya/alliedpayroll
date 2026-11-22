package com.alliedpayroll.tenant.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
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

  @Version private long version;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_by", nullable = false)
  private UUID updatedBy;

  protected FeatureFlagEntity() {
    // JPA
  }

  FeatureFlagEntity(UUID id, String flagKey, boolean enabled, Instant now, UUID actor) {
    this.id = id;
    this.flagKey = flagKey;
    this.enabled = enabled;
    this.createdAt = now;
    this.createdBy = actor;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  void enable(boolean value, Instant now, UUID actor) {
    this.enabled = value;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  UUID id() {
    return id;
  }

  String flagKey() {
    return flagKey;
  }

  boolean enabled() {
    return enabled;
  }
}
