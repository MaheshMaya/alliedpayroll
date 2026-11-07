package com.alliedpayroll.tenant.infrastructure.persistence;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;

/** Row of {@code tenant.tenant}; the domain record is built from it and never exposed as JPA. */
@Entity
@Table(schema = "tenant", name = "tenant")
class TenantEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 40)
  private String slug;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(name = "industry_profile_code", nullable = false, length = 30)
  private String industryProfileCode;

  @Column(name = "profile_template_version", nullable = false)
  private int profileTemplateVersion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TenantStatus status;

  @Column(name = "default_locale", nullable = false, length = 16)
  private String defaultLocale;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "enabled_locales", nullable = false)
  private List<String> enabledLocales;

  @Column(nullable = false, length = 40)
  private String timezone;

  @Column(name = "keycloak_org_id", length = 80)
  private @Nullable String keycloakOrgId;

  @Column(name = "provisioned_at")
  private @Nullable Instant provisionedAt;

  @Version private long version;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_by", nullable = false)
  private UUID updatedBy;

  protected TenantEntity() {
    // JPA
  }

  TenantEntity(Tenant tenant, Instant now, UUID actor) {
    this.id = tenant.id().value();
    this.slug = tenant.slug();
    this.name = tenant.name();
    this.industryProfileCode = tenant.industryProfileCode();
    this.profileTemplateVersion = 1;
    this.status = tenant.status();
    this.defaultLocale = tenant.defaultLocale();
    this.enabledLocales = List.copyOf(tenant.enabledLocales());
    this.timezone = tenant.timezone();
    this.keycloakOrgId = tenant.keycloakOrganizationId();
    this.provisionedAt = tenant.isActive() ? now : null;
    this.createdAt = now;
    this.createdBy = actor;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  Tenant toDomain() {
    return new Tenant(
        new TenantId(id),
        slug,
        name,
        industryProfileCode,
        status,
        defaultLocale,
        List.copyOf(enabledLocales),
        timezone,
        keycloakOrgId);
  }

  UUID id() {
    return id;
  }
}
