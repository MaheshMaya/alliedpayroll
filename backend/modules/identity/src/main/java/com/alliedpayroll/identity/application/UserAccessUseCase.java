package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserDisabled;
import com.alliedpayroll.identity.api.UserEnabled;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.error.RuleViolationException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.TenantContext;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Disables and re-enables a user. Disabling signs the person out of the identity provider, and the
 * API refuses any session or token they still hold. Both are no-ops when the user is already in the
 * requested state.
 */
@Service
public class UserAccessUseCase {

  private final UserRepository users;
  private final IdentityDirectory directory;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  UserAccessUseCase(
      UserRepository users,
      IdentityDirectory directory,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.users = users;
    this.directory = directory;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  @Transactional
  public void disable(UserId userId, Actor actor) {
    if (userId.equals(actor.userId())) {
      throw RuleViolationException.of("identity.cannot_disable_self");
    }
    AppUser user = load(userId);
    if (user.isDisabled()) {
      return;
    }
    AppUser disabled = users.save(user.disabled(), actor.userId());
    directory.disable(user.username());
    events.publishEvent(
        new UserDisabled(ids.next(), TenantContext.requireId(), userId, clock.instant()));
    record(user, disabled, actor, "identity.user_disabled");
  }

  @Transactional
  public void enable(UserId userId, Actor actor) {
    AppUser user = load(userId);
    if (!user.isDisabled()) {
      return;
    }
    AppUser enabled = users.save(user.enabled(), actor.userId());
    directory.enable(user.username());
    events.publishEvent(
        new UserEnabled(ids.next(), TenantContext.requireId(), userId, clock.instant()));
    record(user, enabled, actor, "identity.user_enabled");
  }

  private AppUser load(UserId userId) {
    return users.findById(userId).orElseThrow(() -> NotFoundException.of("user", userId));
  }

  private void record(AppUser before, AppUser after, Actor actor, String reasonKey) {
    audit.record(
        AuditEntry.change(
            AuditAction.STATE_CHANGE,
            actor.audit(),
            actor.channel(),
            UserAudit.ENTITY_TYPE,
            after.id().value(),
            after.username(),
            List.of("status"),
            Map.of("status", before.status().name()),
            Map.of("status", after.status().name()),
            reasonKey));
  }
}
