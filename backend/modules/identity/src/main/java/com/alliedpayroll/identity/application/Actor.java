package com.alliedpayroll.identity.application;

import com.alliedpayroll.platform.core.audit.AuditActor;
import com.alliedpayroll.platform.core.ids.UserId;

/** The signed-in person making a change, and the channel they made it from. */
public record Actor(UserId userId, String channel) {

  AuditActor audit() {
    return AuditActor.user(userId);
  }
}
