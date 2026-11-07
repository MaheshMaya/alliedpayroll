package com.alliedpayroll.tenant.api;

import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * The first response every client fetches after sign-in: who the user is, which tenant they are in,
 * what they may do and which modules are on.
 */
public record Bootstrap(
    TenantSummary tenant, User user, Set<String> permissions, Map<String, Boolean> features) {

  public record User(String subject, @Nullable String displayName, @Nullable String email) {}
}
