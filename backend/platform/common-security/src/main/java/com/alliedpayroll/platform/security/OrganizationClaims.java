package com.alliedpayroll.platform.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Reads Keycloak's {@code organization} claim, which arrives either as a map keyed by organisation
 * alias (the default) or as a plain list of aliases when the mapper is configured that way.
 */
final class OrganizationClaims {

  static final String CLAIM = "organization";

  private OrganizationClaims() {}

  static Set<String> aliases(@Nullable Object claim) {
    return switch (claim) {
      case Map<?, ?> map -> toStrings(map.keySet());
      case Collection<?> list -> toStrings(list);
      case String single -> Set.of(single);
      case null, default -> Set.of();
    };
  }

  private static Set<String> toStrings(Collection<?> values) {
    Set<String> out = new LinkedHashSet<>();
    for (Object value : values) {
      if (value instanceof String s && !s.isBlank()) {
        out.add(s);
      }
    }
    return Set.copyOf(out);
  }
}
