package com.alliedpayroll.webchannel;

import com.alliedpayroll.platform.core.error.DomainException;
import java.util.Map;

/** The identity provider refused the sign-in: wrong organisation, cancelled consent, bad client. */
public final class SignInFailedException extends DomainException {

  public SignInFailedException() {
    super("auth.sign_in_failed", Map.of());
  }
}
