package com.alliedpayroll.tenant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;

class TerminologyTest {

  private static final TerminologyPack BASE =
      new TerminologyPack(
          Map.of(
              "en-IN", Map.of("site", "Site", "site_sub_unit", "Sub-unit", "duty", "Duty"),
              "hi", Map.of("site", "साइट")));

  private static final TerminologyPack STAFFING =
      new TerminologyPack(Map.of("en-IN", Map.of("site_sub_unit", "Post")));

  @Test
  void tenantLabelWinsOverProfileAndBase() {
    Terminology terms =
        new Terminology("en-IN", BASE, STAFFING, Map.of("site_sub_unit", "Gate post"));

    assertThat(terms.label("site_sub_unit")).isEqualTo("Gate post");
    assertThat(terms.defaultLabel("site_sub_unit")).isEqualTo("Post");
    assertThat(terms.isOverridden("site_sub_unit")).isTrue();
  }

  @Test
  void profileWordReplacesTheBaseWord() {
    Terminology terms = new Terminology("en-IN", BASE, STAFFING, Map.of());

    assertThat(terms.label("site_sub_unit")).isEqualTo("Post");
    assertThat(terms.label("site")).isEqualTo("Site");
  }

  @Test
  void missingTranslationFallsBackToEnglish() {
    Terminology terms = new Terminology("hi", BASE, STAFFING, Map.of());

    assertThat(terms.label("site")).isEqualTo("साइट");
    assertThat(terms.label("duty")).isEqualTo("Duty");
  }

  @Test
  void englishOverrideDoesNotChangeOtherLanguages() {
    Terminology hindi = new Terminology("hi", BASE, TerminologyPack.EMPTY, Map.of());

    assertThat(hindi.label("site")).isEqualTo("साइट");
    assertThat(hindi.isOverridden("site")).isFalse();
  }

  @Test
  void labelsCoverEveryConceptInBothNumbers() {
    Terminology terms = new Terminology("en-IN", BASE, STAFFING, Map.of());

    assertThat(terms.labels().keySet()).containsExactlyElementsOf(TerminologyConcepts.KEYS);
  }

  @Test
  void packRefusesAnUnknownKey() {
    assertThatThrownBy(() -> new TerminologyPack(Map.of("en-IN", Map.of("guard", "Guard"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("guard");
  }
}
