package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.core.ids.UserId;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * What the tenant knows about an authenticated person: their user row and the permissions their
 * roles grant. {@link #none()} is a member of the organisation whom the tenant has not provisioned;
 * {@link #disabled(UserId)} is a person the tenant has switched off, who may call nothing.
 */
public record TenantPrincipal(@Nullable UserId userId, Set<String> permissions, boolean disabled) {

  private static final TenantPrincipal NONE = new TenantPrincipal(null, Set.of(), false);

  public TenantPrincipal {
    permissions = Set.copyOf(permissions);
  }

  public static TenantPrincipal active(UserId userId, Set<String> permissions) {
    return new TenantPrincipal(userId, permissions, false);
  }

  public static TenantPrincipal disabled(UserId userId) {
    return new TenantPrincipal(userId, Set.of(), true);
  }

  public static TenantPrincipal none() {
    return NONE;
  }
}
