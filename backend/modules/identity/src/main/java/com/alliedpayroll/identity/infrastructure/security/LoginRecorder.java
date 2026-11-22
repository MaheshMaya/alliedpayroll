package com.alliedpayroll.identity.infrastructure.security;

import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.security.UserSignedIn;
import java.time.Clock;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Stamps the user's last sign-in when the browser session is established. */
@Component
class LoginRecorder {

  private final UserRepository repository;
  private final Clock clock;

  LoginRecorder(UserRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  @EventListener
  void onSignedIn(UserSignedIn event) {
    UserId id = event.user().userId();
    if (id != null) {
      repository.recordLogin(id, clock.instant(), event.user().channel().name());
    }
  }
}
