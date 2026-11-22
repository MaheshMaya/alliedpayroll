package com.alliedpayroll.platform.web;

import com.alliedpayroll.platform.core.error.PreconditionFailedException;
import org.jspecify.annotations.Nullable;

/**
 * Entity tags from optimistic-lock versions. A resource sends {@code ETag: "7"}; a client that
 * changes it sends the same value in {@code If-Match}, and a stale value is refused with 412.
 */
public final class EntityTags {

  private EntityTags() {}

  public static String of(long version) {
    return "\"" + version + "\"";
  }

  /**
   * The version an {@code If-Match} header names, or null when the header is absent. A header that
   * names no version this API issued can never match, so it is refused as stale.
   */
  public static @Nullable Long version(@Nullable String ifMatch, String resource, Object id) {
    if (ifMatch == null || ifMatch.isBlank()) {
      return null;
    }
    String tag = ifMatch.trim();
    if (tag.startsWith("W/")) {
      tag = tag.substring(2);
    }
    tag = tag.replace("\"", "");
    try {
      return Long.parseLong(tag);
    } catch (NumberFormatException e) {
      throw new PreconditionFailedException(resource, id);
    }
  }
}
