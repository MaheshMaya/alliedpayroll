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
@Table(schema = "identity", name = "role_permission")
class RolePermissionEntity {

  @Embeddable
  record Key(
      @Column(name = "role_id", nullable = false) UUID roleId,
      @Column(name = "permission_code", nullable = false, length = 60) String permissionCode)
      implements Serializable {}

  @EmbeddedId private Key key;

  @TenantId
  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "granted_at", nullable = false)
  private Instant grantedAt;

  @Column(name = "granted_by", nullable = false)
  private UUID grantedBy;

  protected RolePermissionEntity() {
    // JPA
  }

  RolePermissionEntity(UUID roleId, String permissionCode, Instant now, UUID actor) {
    this.key = new Key(roleId, permissionCode);
    this.grantedAt = now;
    this.grantedBy = actor;
  }

  Key key() {
    return key;
  }
}
