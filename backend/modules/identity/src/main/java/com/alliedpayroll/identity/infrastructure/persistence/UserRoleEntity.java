package com.alliedpayroll.identity.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.TenantId;

@Entity
@Table(schema = "identity", name = "user_role")
class UserRoleEntity {

  @Embeddable
  record Key(
      @Column(name = "user_id", nullable = false) UUID userId,
      @Column(name = "role_id", nullable = false) UUID roleId)
      implements Serializable {}

  @EmbeddedId private Key key;

  @TenantId
  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "assigned_at", nullable = false)
  private Instant assignedAt;

  @Column(name = "assigned_by", nullable = false)
  private UUID assignedBy;

  protected UserRoleEntity() {
    // JPA
  }

  UserRoleEntity(UUID userId, UUID roleId, Instant now, UUID actor) {
    this.key = new Key(userId, roleId);
    this.assignedAt = now;
    this.assignedBy = actor;
  }

  Key key() {
    return key;
  }
}
