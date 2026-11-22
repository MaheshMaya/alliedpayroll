package com.alliedpayroll.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PersonNameTest {

  @Test
  void lastWordIsTheFamilyName() {
    assertThat(PersonName.of("Nitya Lakshmi Rao"))
        .isEqualTo(new PersonName("Nitya Lakshmi", "Rao"));
  }

  @Test
  void singleNameHasNoFamilyName() {
    assertThat(PersonName.of("Murugan")).isEqualTo(new PersonName("Murugan", ""));
  }

  @Test
  void extraSpacesAreIgnored() {
    assertThat(PersonName.of("  Asha   Varma ")).isEqualTo(new PersonName("Asha", "Varma"));
  }
}
