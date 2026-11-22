package com.alliedpayroll.identity.api;

import com.alliedpayroll.platform.core.ids.UserId;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Creates or completes a tenant's user record with its roles. Idempotent on username, so seeds and
 * retries are safe. Runs in the bound tenant; the Keycloak side is linked on first sign-in.
 */
public interface UserAdministration {

  UserId ensureUser(EnsureUser command);

  /**
   * The user to create or complete.
   *
   * @param username the login name in Keycloak, which links the row on first sign-in
   * @param roleCodes codes of existing roles, system or tenant-defined
   */
  record EnsureUser(
      String username,
      String displayName,
      @Nullable String email,
      UserType userType,
      Set<String> roleCodes) {
    public EnsureUser {
      roleCodes = Set.copyOf(roleCodes);
    }
  }
}
