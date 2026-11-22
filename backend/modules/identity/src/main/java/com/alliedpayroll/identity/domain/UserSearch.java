package com.alliedpayroll.identity.domain;

import java.util.Locale;
import org.jspecify.annotations.Nullable;

/** Filters of the user list; a blank search term means no term. */
public record UserSearch(@Nullable String term, @Nullable UserStatus status) {

  public static UserSearch of(@Nullable String term, @Nullable UserStatus status) {
    String trimmed = term == null ? null : term.trim().toLowerCase(Locale.ROOT);
    return new UserSearch(trimmed == null || trimmed.isEmpty() ? null : trimmed, status);
  }
}
