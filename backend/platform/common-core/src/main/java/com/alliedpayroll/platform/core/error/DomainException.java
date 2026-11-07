package com.alliedpayroll.platform.core.error;

import java.util.Map;

/**
 * A business outcome the caller must handle. Carries a stable code that becomes the problem+json
 * {@code code} and the message key; never a formatted English sentence.
 */
public abstract class DomainException extends RuntimeException {

  private final String code;
  private final transient Map<String, Object> params;

  protected DomainException(String code, Map<String, Object> params) {
    super(code);
    this.code = code;
    this.params = Map.copyOf(params);
  }

  public String code() {
    return code;
  }

  public Map<String, Object> params() {
    return params;
  }
}
