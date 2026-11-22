package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserActivated;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import java.time.Clock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The first sign-in of a provisioned user: the Keycloak subject is recorded, the row activates. */
@Service
class LinkSubjectUseCase {

  private final UserRepository users;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;

  LinkSubjectUseCase(
      UserRepository users, IdGenerator ids, Clock clock, ApplicationEventPublisher events) {
    this.users = users;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
  }

  @Transactional
  public AppUser link(CurrentTenant tenant, AppUser user, String subject) {
    AppUser linked = users.save(user.linkedTo(subject, clock.instant()));
    events.publishEvent(new UserActivated(ids.next(), tenant.id(), linked.id(), clock.instant()));
    return linked;
  }
}
