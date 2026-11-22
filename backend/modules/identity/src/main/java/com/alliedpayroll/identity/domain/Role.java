package com.alliedpayroll.identity.domain;

import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * A named set of permissions in one tenant. System roles come from the catalogue and stay as is.
 */
public record Role(
    UUID id,
    String code,
    String name,
    @Nullable String description,
    boolean system,
    Set<String> permissions) {

  public Role {
    permissions = Set.copyOf(permissions);
  }
}
