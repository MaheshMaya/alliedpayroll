package com.alliedpayroll.platform.core.error;

import java.util.Map;

/**
 * A system the request depends on (the identity provider, a statutory portal) did not answer. The
 * request changed nothing and can be retried.
 */
public final class DependencyUnavailableException extends DomainException {

  public DependencyUnavailableException(String code, Throwable cause) {
    super(code, Map.of());
    initCause(cause);
  }
}
