package com.alliedpayroll.platform.core.ids;

import java.util.UUID;

/** Identity of an application user, a device, or the fixed system actor. */
public record UserId(UUID value) {

  public UserId {
    if (value == null) {
      throw new IllegalArgumentException("user id must not be null");
    }
  }

  public static UserId of(String raw) {
    return new UserId(UUID.fromString(raw));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
