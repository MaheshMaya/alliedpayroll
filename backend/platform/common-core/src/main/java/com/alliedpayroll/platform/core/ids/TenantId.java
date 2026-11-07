package com.alliedpayroll.platform.core.ids;

import java.util.UUID;

/** Identity of a tenant row in the platform directory. */
public record TenantId(UUID value) {

  public TenantId {
    if (value == null) {
      throw new IllegalArgumentException("tenant id must not be null");
    }
  }

  public static TenantId of(String raw) {
    return new TenantId(UUID.fromString(raw));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
