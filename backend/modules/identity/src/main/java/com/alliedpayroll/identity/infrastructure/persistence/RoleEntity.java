package com.alliedpayroll.identity.infrastructure.persistence;

import com.alliedpayroll.identity.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;

@Entity
@Table(schema = "identity", name = "role")
class RoleEntity {

  @Id private UUID id;

  @TenantId
  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(nullable = false, length = 40)
  private String code;

  @Column(nullable = false, length = 80)
  private String name;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_i18n", nullable = false)
  private Map<String, String> nameI18n;

  @Column(length = 255)
  private @Nullable String description;

  @Column(nullable = false)
  private boolean system;

  @Column(nullable = false, length = 12)
  private String status;

  @Version private long version;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_by", nullable = false)
  private UUID updatedBy;

  @Column(name = "deleted_at")
  private @Nullable Instant deletedAt;

  protected RoleEntity() {
    // JPA
  }

  RoleEntity(Role role, Instant now, UUID actor) {
    this.id = role.id();
    this.code = role.code();
    this.name = role.name();
    this.nameI18n = Map.of();
    this.description = role.description();
    this.system = role.system();
    this.status = "ACTIVE";
    this.createdAt = now;
    this.createdBy = actor;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  void apply(Role role, Instant now, UUID actor) {
    this.name = role.name();
    this.description = role.description();
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  Role toDomain(Set<String> permissions) {
    return new Role(id, code, name, description, system, permissions);
  }

  UUID id() {
    return id;
  }
}
