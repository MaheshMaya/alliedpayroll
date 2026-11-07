package com.alliedpayroll.platform.core.error;

import java.util.Map;

/** The request contradicts current state: a duplicate code, a locked period, a stale version. */
public final class ConflictException extends DomainException {

  public ConflictException(String code, Map<String, Object> params) {
    super(code, params);
  }
}
