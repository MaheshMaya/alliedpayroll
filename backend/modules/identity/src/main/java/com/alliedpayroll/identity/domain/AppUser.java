package com.alliedpayroll.identity.domain;

import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.UserId;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * A person or service identity in the tenant. The row exists before the person signs in; the first
 * sign-in with a matching username links the Keycloak subject and activates it.
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
    @Nullable Instant lastLoginAt) {

  public boolean isLinked() {
    return keycloakSubject != null;
  }

  public boolean canSignIn() {
    return status == UserStatus.INVITED || status == UserStatus.ACTIVE;
  }

  public AppUser linkedTo(String subject, Instant now) {
    return new AppUser(
        id,
        username,
        displayName,
        emailMasked,
        userType,
        UserStatus.ACTIVE,
        scopeMode,
        subject,
        now);
  }
}
