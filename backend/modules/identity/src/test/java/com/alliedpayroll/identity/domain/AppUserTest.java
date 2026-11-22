package com.alliedpayroll.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.UserId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AppUserTest {

  private static final AppUser INVITED =
      AppUser.invited(
          new UserId(UUID.fromString("0190a000-0000-7000-8000-000000000002")),
          "nitya@orbit.example",
          "Nitya Rao",
          "n***@orbit.example",
          UserType.MANAGER);

  @Test
  void invitedPersonIsRestrictedUnlessAdministrator() {
    assertThat(INVITED.status()).isEqualTo(UserStatus.INVITED);
    assertThat(INVITED.scopeMode()).isEqualTo(ScopeMode.RESTRICTED);
  }

  @Test
  void disabledUserCannotSignIn() {
    AppUser disabled = INVITED.disabled();

    assertThat(disabled.isDisabled()).isTrue();
    assertThat(disabled.canSignIn()).isFalse();
  }

  @Test
  void enablingUnlinkedUserReturnsThemToInvited() {
    assertThat(INVITED.disabled().enabled().status()).isEqualTo(UserStatus.INVITED);
  }

  @Test
  void enablingLinkedUserReturnsThemToActive() {
    AppUser linked = INVITED.linkedTo("kc-nitya", Instant.parse("2026-11-22T09:00:00Z"));

    assertThat(linked.disabled().enabled().status()).isEqualTo(UserStatus.ACTIVE);
  }
}
