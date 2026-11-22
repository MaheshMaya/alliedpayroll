package com.alliedpayroll.tenant.infrastructure.profiles;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.domain.TenantModule;
import com.alliedpayroll.tenant.domain.TerminologyConcepts;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

/** The packs shipped in the jar, read the way the application reads them. */
class ClasspathProfileTemplatesTest {

  private final ClasspathProfileTemplates templates =
      new ClasspathProfileTemplates(JsonMapper.builder().build());

  @Test
  void everyReferenceProfileIsPublished() {
    assertThat(templates.latest("STAFFING")).isPresent();
    assertThat(templates.latest("FIELD_SERVICE")).isPresent();
    assertThat(templates.latest("CORPORATE")).isPresent();
  }

  @Test
  void baseWordsExistForEveryKeyInEveryShippedLanguage() {
    var base = templates.baseTerminology();

    assertThat(base.locales()).containsExactlyInAnyOrder("en-IN", "hi", "ta");
    for (String locale : base.locales()) {
      for (String key : TerminologyConcepts.KEYS) {
        assertThat(base.label(locale, key)).as(locale + " " + key).isPresent();
      }
    }
  }

  @Test
  void profilesDifferInTheModulesTheyStartWith() {
    ProfileTemplate staffing = templates.latest("STAFFING").orElseThrow();
    ProfileTemplate corporate = templates.latest("CORPORATE").orElseThrow();

    assertThat(staffing.modules()).containsEntry(TenantModule.CUSTOMERS, true);
    assertThat(corporate.modules()).containsEntry(TenantModule.CUSTOMERS, false);
  }

  @Test
  void profileWordsAreTranslatedWhereverEnglishHasThem() {
    for (String code : List.of("STAFFING", "FIELD_SERVICE", "CORPORATE")) {
      var pack = templates.latest(code).orElseThrow().terminology();
      for (String key : TerminologyConcepts.KEYS) {
        if (pack.label("en-IN", key).isEmpty()) {
          continue;
        }
        if (code.equals("FIELD_SERVICE") && key.startsWith("client")) {
          // "Customer" and "Client" are the same word in Hindi and Tamil.
          continue;
        }
        assertThat(pack.label("hi", key)).as(code + " hi " + key).isPresent();
        assertThat(pack.label("ta", key)).as(code + " ta " + key).isPresent();
      }
    }
  }
}
