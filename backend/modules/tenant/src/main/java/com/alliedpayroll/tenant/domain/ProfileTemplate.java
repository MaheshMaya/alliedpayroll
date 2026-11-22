package com.alliedpayroll.tenant.domain;

import java.util.EnumMap;
import java.util.Map;

/**
 * A published industry profile: the modules a new tenant starts with and the words it uses. A
 * version never changes once published; a change is a new version.
 *
 * @param modules every optional module with whether the profile switches it on
 */
public record ProfileTemplate(
    String code,
    int version,
    String name,
    Map<TenantModule, Boolean> modules,
    TerminologyPack terminology) {

  public ProfileTemplate {
    if (version < 1) {
      throw new IllegalArgumentException("template version starts at 1: " + code);
    }
    if (modules.size() != TenantModule.values().length) {
      throw new IllegalArgumentException("template " + code + " must decide every module");
    }
    modules = Map.copyOf(new EnumMap<>(modules));
  }
}
