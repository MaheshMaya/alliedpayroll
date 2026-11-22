package com.alliedpayroll.identity.domain;

import org.jspecify.annotations.Nullable;

/** The display form of an email: first character of the local part, the domain in full. */
public final class EmailMask {

  private EmailMask() {}

  public static @Nullable String mask(@Nullable String email) {
    if (email == null) {
      return null;
    }
    int at = email.indexOf('@');
    if (at <= 0) {
      return "***";
    }
    return email.charAt(0) + "***" + email.substring(at);
  }
}
