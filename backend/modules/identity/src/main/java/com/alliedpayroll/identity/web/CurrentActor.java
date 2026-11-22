package com.alliedpayroll.identity.web;

import com.alliedpayroll.identity.application.Actor;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.security.AuthenticatedUser;
import com.alliedpayroll.platform.security.CurrentUserProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/** The signed-in person behind a write. Only a provisioned user can hold a write permission. */
@Component
class CurrentActor {

  private final CurrentUserProvider users;

  CurrentActor(CurrentUserProvider users) {
    this.users = users;
  }

  Actor require() {
    AuthenticatedUser user = users.require();
    UserId id = user.userId();
    if (id == null) {
      throw new AccessDeniedException("not a provisioned user");
    }
    return new Actor(id, user.channel().name());
  }
}
