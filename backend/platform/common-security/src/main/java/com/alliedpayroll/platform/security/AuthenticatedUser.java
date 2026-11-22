package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.core.ids.UserId;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * The principal as every module sees it, independent of whether it arrived through the browser
 * session or a bearer token.
 *
 * @param subject the identity provider's stable subject
 * @param username the login name the identity provider shows, used to link a provisioned user
 * @param organizations aliases of the organisations the identity provider says the user belongs to
 * @param userId the tenant's user row, once the identity module knows the person
 * @param permissions permission codes granted in the current tenant through the person's roles
 */
public record AuthenticatedUser(
    String subject,
    @Nullable String username,
    @Nullable String displayName,
    @Nullable String email,
    Set<String> organizations,
    @Nullable UserId userId,
    Set<String> permissions,
    Channel channel) {

  public AuthenticatedUser {
    organizations = Set.copyOf(organizations);
    permissions = Set.copyOf(permissions);
  }

  public boolean belongsTo(String organizationAlias) {
    return organizations.contains(organizationAlias);
  }

  public boolean has(String permission) {
    return permissions.contains(permission);
  }

  /** The same person with what the tenant knows about them. */
  public AuthenticatedUser enrichedWith(TenantPrincipal principal) {
    return new AuthenticatedUser(
        subject,
        username,
        displayName,
        email,
        organizations,
        principal.userId(),
        principal.permissions(),
        channel);
  }
}
