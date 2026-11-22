package com.alliedpayroll.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserSearchTest {

  @Test
  void normalisesTheTermAndDropsBlanks() {
    assertThat(UserSearch.of("  Asha ", null).term()).isEqualTo("asha");
    assertThat(UserSearch.of("   ", UserStatus.ACTIVE).term()).isNull();
    assertThat(UserSearch.of(null, null).term()).isNull();
  }
}
