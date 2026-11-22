package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.core.ids.UserId;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * What the tenant knows about an authenticated person: their user row and the permissions their
 * roles grant. {@link #none()} is a member of the organisation whom the tenant has not provisioned.
 */
public record TenantPrincipal(@Nullable UserId userId, Set<String> permissions) {

  private static final TenantPrincipal NONE = new TenantPrincipal(null, Set.of());

  public TenantPrincipal {
    permissions = Set.copyOf(permissions);
  }

  public static TenantPrincipal none() {
    return NONE;
  }
}
