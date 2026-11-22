package com.alliedpayroll.platform.core.audit;

import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.platform.core.ids.UserId;
import java.util.UUID;

/**
 * Who did it. A person the tenant has not provisioned yet is still an actor: the fixed {@link
 * #UNKNOWN_USER} id with the identity provider's subject in the entry's reason parameters.
 */
public record AuditActor(ActorType type, UUID id) {

  public static final AuditActor SYSTEM = new AuditActor(ActorType.SYSTEM, SystemActor.ID.value());

  public static final AuditActor UNKNOWN_USER =
      new AuditActor(ActorType.USER, UUID.fromString("00000000-0000-0000-0000-000000000002"));

  public static AuditActor user(UserId id) {
    return new AuditActor(ActorType.USER, id.value());
  }
}
