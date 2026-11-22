package com.alliedpayroll.tenant.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The labels one tenant shows in one locale (LOCALIZATION_I18N §5): the tenant's own label, else
 * the profile's, else the base word in the locale, else the base word in English.
 *
 * @param overrides the tenant's labels in {@code locale}, by key
 */
public record Terminology(
    String locale, TerminologyPack base, TerminologyPack profile, Map<String, String> overrides) {

  public static final String FALLBACK_LOCALE = "en-IN";

  public Terminology {
    overrides = Map.copyOf(overrides);
  }

  /** Every key with the label to show. */
  public Map<String, String> labels() {
    Map<String, String> labels = new LinkedHashMap<>();
    for (String key : TerminologyConcepts.KEYS) {
      labels.put(key, label(key));
    }
    return labels;
  }

  public String label(String key) {
    String own = overrides.get(key);
    return own != null ? own : defaultLabel(key);
  }

  /** What the key shows when the tenant has not relabelled it. */
  public String defaultLabel(String key) {
    return profile
        .label(locale, key)
        .or(() -> base.label(locale, key))
        .or(() -> base.label(FALLBACK_LOCALE, key))
        .orElse(key);
  }

  public boolean isOverridden(String key) {
    return overrides.containsKey(key);
  }
}
