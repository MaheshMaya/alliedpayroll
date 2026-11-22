package com.alliedpayroll.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailMaskTest {

  @Test
  void keepsTheFirstCharacterAndTheDomain() {
    assertThat(EmailMask.mask("asha@sentinel.example")).isEqualTo("a***@sentinel.example");
  }

  @Test
  void hidesEverythingWhenTheValueIsNotAnEmail() {
    assertThat(EmailMask.mask("not-an-email")).isEqualTo("***");
    assertThat(EmailMask.mask("@x")).isEqualTo("***");
  }

  @Test
  void passesNullThrough() {
    assertThat(EmailMask.mask(null)).isNull();
  }
}
