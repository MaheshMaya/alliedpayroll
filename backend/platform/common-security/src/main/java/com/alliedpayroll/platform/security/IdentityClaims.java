package com.alliedpayroll.platform.security;

import java.util.Set;
import org.jspecify.annotations.Nullable;

/** The identity provider's description of a person, as the token or session carried it. */
public record IdentityClaims(
    String subject,
    @Nullable String username,
    @Nullable String displayName,
    @Nullable String email,
    Channel channel,
    Set<String> organizations) {

  public IdentityClaims {
    organizations = Set.copyOf(organizations);
  }
}
