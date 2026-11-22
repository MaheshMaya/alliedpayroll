package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.audit.AuditActor;
import com.alliedpayroll.platform.core.ids.UserId;

/** The signed-in person changing the tenant's configuration, and the channel they used. */
public record Actor(UserId userId, String channel) {

  AuditActor audit() {
    return AuditActor.user(userId);
  }
}
