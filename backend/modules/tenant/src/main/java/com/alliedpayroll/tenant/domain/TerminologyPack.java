package com.alliedpayroll.tenant.domain;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Labels by locale, then by key: the base words every tenant starts from, or a profile's own words
 * on top of them.
 */
public record TerminologyPack(Map<String, Map<String, String>> labelsByLocale) {

  public static final TerminologyPack EMPTY = new TerminologyPack(Map.of());

  public TerminologyPack {
    labelsByLocale =
        labelsByLocale.entrySet().stream()
            .collect(
                java.util.stream.Collectors.toUnmodifiableMap(
                    Map.Entry::getKey, entry -> Map.copyOf(entry.getValue())));
    for (Map<String, String> labels : labelsByLocale.values()) {
      for (String key : labels.keySet()) {
        if (!TerminologyConcepts.isKnownKey(key)) {
          throw new IllegalArgumentException("unknown terminology key " + key);
        }
      }
    }
  }

  public Optional<String> label(String locale, String key) {
    return Optional.ofNullable(labelsByLocale.getOrDefault(locale, Map.of()).get(key));
  }

  public Set<String> locales() {
    return labelsByLocale.keySet();
  }
}
