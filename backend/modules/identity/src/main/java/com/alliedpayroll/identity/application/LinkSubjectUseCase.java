package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserActivated;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditActor;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The first sign-in of a provisioned user: the Keycloak subject is recorded, the row activates. */
@Service
class LinkSubjectUseCase {

  static final String ENTITY_TYPE = "USER";

  private final UserRepository users;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  LinkSubjectUseCase(
      UserRepository users,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.users = users;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  @Transactional
  public AppUser link(CurrentTenant tenant, AppUser user, String subject, String channel) {
    AppUser linked = users.save(user.linkedTo(subject, clock.instant()));
    events.publishEvent(new UserActivated(ids.next(), tenant.id(), linked.id(), clock.instant()));
    audit.record(
        AuditEntry.change(
            AuditAction.STATE_CHANGE,
            AuditActor.user(linked.id()),
            channel,
            ENTITY_TYPE,
            linked.id().value(),
            linked.username(),
            List.of("status", "keycloakSubject"),
            Map.of("status", user.status().name()),
            Map.of("status", linked.status().name()),
            "identity.user_activated"));
    return linked;
  }
}
