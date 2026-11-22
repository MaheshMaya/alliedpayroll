package com.alliedpayroll.identity.infrastructure.persistence;

import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.ScopeMode;
import com.alliedpayroll.identity.domain.UserStatus;
import com.alliedpayroll.platform.core.ids.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.TenantId;
import org.jspecify.annotations.Nullable;

/**
 * Row of {@code identity.app_user}; the domain record is built from it and never exposed as JPA.
 */
@Entity
@Table(schema = "identity", name = "app_user")
class AppUserEntity {

  @Id private UUID id;

  @TenantId
  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "keycloak_subject", length = 64)
  private @Nullable String keycloakSubject;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type", nullable = false, length = 20)
  private UserType userType;

  @Column(nullable = false, length = 80)
  private String username;

  @Column(name = "display_name", nullable = false, length = 150)
  private String displayName;

  @Column(name = "email_masked", length = 80)
  private @Nullable String emailMasked;

  @Enumerated(EnumType.STRING)
  @Column(name = "scope_mode", nullable = false, length = 12)
  private ScopeMode scopeMode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 12)
  private UserStatus status;

  @Column(name = "invited_at")
  private @Nullable Instant invitedAt;

  @Column(name = "activated_at")
  private @Nullable Instant activatedAt;

  @Column(name = "last_login_at")
  private @Nullable Instant lastLoginAt;

  @Column(name = "last_login_channel", length = 12)
  private @Nullable String lastLoginChannel;

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

  protected AppUserEntity() {
    // JPA
  }

  AppUserEntity(AppUser user, Instant now, UUID actor) {
    this.id = user.id().value();
    this.keycloakSubject = user.keycloakSubject();
    this.userType = user.userType();
    this.username = user.username();
    this.displayName = user.displayName();
    this.emailMasked = user.emailMasked();
    this.scopeMode = user.scopeMode();
    this.status = user.status();
    this.invitedAt = now;
    this.activatedAt = user.status() == UserStatus.ACTIVE ? now : null;
    this.lastLoginAt = user.lastLoginAt();
    this.createdAt = now;
    this.createdBy = actor;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  void apply(AppUser user, Instant now, UUID actor) {
    if (keycloakSubject == null && user.keycloakSubject() != null) {
      activatedAt = now;
    }
    this.keycloakSubject = user.keycloakSubject();
    this.userType = user.userType();
    this.displayName = user.displayName();
    this.emailMasked = user.emailMasked();
    this.scopeMode = user.scopeMode();
    this.status = user.status();
    this.lastLoginAt = user.lastLoginAt();
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  void recordLogin(Instant at, String channel) {
    this.lastLoginAt = at;
    this.lastLoginChannel = channel;
    this.updatedAt = at;
  }

  String displayName() {
    return displayName;
  }

  AppUser toDomain() {
    return new AppUser(
        new UserId(id),
        username,
        displayName,
        emailMasked,
        userType,
        status,
        scopeMode,
        keycloakSubject,
        lastLoginAt);
  }

  UUID id() {
    return id;
  }
}
