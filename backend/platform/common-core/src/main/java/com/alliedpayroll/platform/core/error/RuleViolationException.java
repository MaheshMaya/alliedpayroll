package com.alliedpayroll.platform.core.error;

import java.util.Map;

/** The request is well formed but a business rule refuses it. */
public final class RuleViolationException extends DomainException {

  public RuleViolationException(String code, Map<String, Object> params) {
    super(code, params);
  }

  public static RuleViolationException of(String code) {
    return new RuleViolationException(code, Map.of());
  }
}
