package com.alliedpayroll.identity.domain;

import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.UserId;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * A person or service identity in the tenant. The row exists before the person signs in; the first
 * sign-in with a matching username links the Keycloak subject and activates it.
 *
 * @param version the optimistic-lock version, sent to clients as the ETag
 */
public record AppUser(
    UserId id,
    String username,
    String displayName,
    @Nullable String emailMasked,
    UserType userType,
    UserStatus status,
    ScopeMode scopeMode,
    @Nullable String keycloakSubject,
    @Nullable Instant lastLoginAt,
    long version) {

  /** A person an administrator has just invited: not linked, not signed in yet. */
  public static AppUser invited(
      UserId id,
      String username,
      String displayName,
      @Nullable String emailMasked,
      UserType userType) {
    ScopeMode scope = userType == UserType.ADMIN ? ScopeMode.ALL : ScopeMode.RESTRICTED;
    return new AppUser(
        id, username, displayName, emailMasked, userType, UserStatus.INVITED, scope, null, null, 0);
  }

  public boolean isLinked() {
    return keycloakSubject != null;
  }

  public boolean canSignIn() {
    return status == UserStatus.INVITED || status == UserStatus.ACTIVE;
  }

  public boolean isDisabled() {
    return status == UserStatus.DISABLED || status == UserStatus.LOCKED;
  }

  public AppUser linkedTo(String subject, Instant now) {
    return withStatus(UserStatus.ACTIVE, subject, now);
  }

  public AppUser disabled() {
    return withStatus(UserStatus.DISABLED, keycloakSubject, lastLoginAt);
  }

  /** Back to where the person was before: active once they have signed in, invited until then. */
  public AppUser enabled() {
    return withStatus(
        isLinked() ? UserStatus.ACTIVE : UserStatus.INVITED, keycloakSubject, lastLoginAt);
  }

  private AppUser withStatus(
      UserStatus next, @Nullable String subject, @Nullable Instant lastLogin) {
    return new AppUser(
        id,
        username,
        displayName,
        emailMasked,
        userType,
        next,
        scopeMode,
        subject,
        lastLogin,
        version);
  }
}
