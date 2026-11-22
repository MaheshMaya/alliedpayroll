package com.alliedpayroll.identity.infrastructure.persistence;

import com.alliedpayroll.platform.security.PermissionDefinition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Row of the platform-owned catalogue {@code identity.permission}; no tenant, no RLS. */
@Entity
@Table(schema = "identity", name = "permission")
class PermissionEntity {

  @Id
  @Column(length = 60)
  private String code;

  @Column(name = "module_code", nullable = false, length = 30)
  private String moduleCode;

  @Column(name = "description_key", nullable = false, length = 120)
  private String descriptionKey;

  @Column(name = "money_moving", nullable = false)
  private boolean moneyMoving;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "default_channels", nullable = false)
  private List<String> defaultChannels;

  @Column(nullable = false, length = 12)
  private String status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected PermissionEntity() {
    // JPA
  }

  PermissionEntity(PermissionDefinition definition, Instant now) {
    this.code = definition.code();
    this.moduleCode = definition.moduleCode();
    this.descriptionKey = definition.descriptionKey();
    this.moneyMoving = definition.moneyMoving();
    this.defaultChannels = List.of("WEB", "MOBILE");
    this.status = "ACTIVE";
    this.createdAt = now;
    this.updatedAt = now;
  }

  void apply(PermissionDefinition definition, Instant now) {
    this.moduleCode = definition.moduleCode();
    this.descriptionKey = definition.descriptionKey();
    this.moneyMoving = definition.moneyMoving();
    this.status = "ACTIVE";
    this.updatedAt = now;
  }

  PermissionDefinition toDefinition() {
    return new PermissionDefinition(code, moduleCode, descriptionKey, moneyMoving);
  }
}
