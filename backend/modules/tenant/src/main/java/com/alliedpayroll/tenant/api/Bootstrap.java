package com.alliedpayroll.tenant.api;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * The first response every client fetches after sign-in: who the user is, which tenant they are in,
 * what they may do, which modules are on and the tenant's words for the canonical concepts.
 *
 * @param terminology label by concept key ({@code site}, {@code site.plural}, ...) in the locale
 *     closest to the request's
 */
public record Bootstrap(
    TenantSummary tenant,
    User user,
    Set<String> permissions,
    Map<String, Boolean> features,
    Map<String, String> terminology) {

  /**
   * The signed-in person.
   *
   * @param id the tenant user record; absent for a member the tenant has not provisioned
   */
  public record User(
      @Nullable UUID id, String subject, @Nullable String displayName, @Nullable String email) {}
}
