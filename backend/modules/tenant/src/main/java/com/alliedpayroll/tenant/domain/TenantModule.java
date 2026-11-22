package com.alliedpayroll.tenant.domain;

import java.util.Arrays;
import java.util.Optional;

/**
 * The modules a tenant can switch on or off. Every tenant has the core (organisation, employees,
 * attendance, payroll, statutory); these are the optional ones, stored as {@code module:CODE}
 * feature flags. Turning one off hides it and never deletes its data.
 */
public enum TenantModule {
  CUSTOMERS,
  CONTRACTS,
  PROJECTS,
  SHIFTS,
  TIMESHEETS,
  BILLING,
  INVENTORY,
  PURCHASE,
  FIELD_OPS,
  ORG_UNITS;

  private static final String FLAG_PREFIX = "module:";

  public String flagKey() {
    return FLAG_PREFIX + name();
  }

  public static Optional<TenantModule> fromCode(String code) {
    return Arrays.stream(values()).filter(module -> module.name().equals(code)).findFirst();
  }
}
