package com.alliedpayroll.platform.security;

import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * The principal as every module sees it, independent of whether it arrived through the browser
 * session or a bearer token.
 *
 * @param subject the identity provider's stable subject
 * @param organizations aliases of the organisations the identity provider says the user belongs to
 * @param permissions permission codes granted in the current tenant; empty until the identity
 *     module assigns roles
 */
public record AuthenticatedUser(
    String subject,
    @Nullable String displayName,
    @Nullable String email,
    Set<String> organizations,
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
}
