package com.alliedpayroll.platform.core.error;

import java.util.Map;

/** The resource does not exist, or is outside the caller's tenant or data scope. */
public final class NotFoundException extends DomainException {

  public NotFoundException(String code, Map<String, Object> params) {
    super(code, params);
  }

  public static NotFoundException of(String resource, Object id) {
    return new NotFoundException(resource + ".not_found", Map.of("id", String.valueOf(id)));
  }
}
