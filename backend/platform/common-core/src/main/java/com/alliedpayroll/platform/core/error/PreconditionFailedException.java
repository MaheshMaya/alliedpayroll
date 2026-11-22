package com.alliedpayroll.platform.core.error;

import java.util.Map;

/** The caller's {@code If-Match} names a version that is no longer current. */
public final class PreconditionFailedException extends DomainException {

  public PreconditionFailedException(String resource, Object id) {
    super("request.precondition_failed", Map.of("resource", resource, "id", String.valueOf(id)));
  }
}
