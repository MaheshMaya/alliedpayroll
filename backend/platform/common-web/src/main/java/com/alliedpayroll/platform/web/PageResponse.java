package com.alliedpayroll.platform.web;

import java.util.List;
import org.jspecify.annotations.Nullable;

/** The list contract every collection endpoint returns: items plus an opaque next cursor. */
public record PageResponse<T>(List<T> items, @Nullable String nextCursor, long totalCount) {

  public static <T> PageResponse<T> of(List<T> items, long totalCount) {
    return new PageResponse<>(List.copyOf(items), null, totalCount);
  }
}
