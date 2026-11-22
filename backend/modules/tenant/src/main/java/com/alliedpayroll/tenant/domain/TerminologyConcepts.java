package com.alliedpayroll.tenant.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The canonical concepts a tenant may relabel (INDUSTRY_PROFILES_AND_STRUCTURE §3.4). Code, APIs
 * and the database use the concept; only the label changes. Each concept has a singular key and a
 * {@code .plural} key.
 */
public final class TerminologyConcepts {

  public static final String PLURAL_SUFFIX = ".plural";

  public static final List<String> CONCEPTS =
      List.of("client", "site", "site_sub_unit", "work_location", "engagement", "duty", "org_unit");

  /** Every label key, singular then plural, in concept order. */
  public static final List<String> KEYS = keys();

  public static final int MAX_LABEL_LENGTH = 80;

  private TerminologyConcepts() {}

  public static boolean isKnownKey(String key) {
    return KEYS.contains(key);
  }

  private static List<String> keys() {
    List<String> keys = new ArrayList<>();
    for (String concept : CONCEPTS) {
      keys.add(concept);
      keys.add(concept + PLURAL_SUFFIX);
    }
    return List.copyOf(keys);
  }
}
