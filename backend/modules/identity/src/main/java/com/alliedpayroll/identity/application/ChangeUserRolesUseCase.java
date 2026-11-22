package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserRolesChanged;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.IdentityPermissions;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.error.PreconditionFailedException;
import com.alliedpayroll.platform.core.error.RuleViolationException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.TenantContext;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Replaces a user's roles. Permissions are resolved per request, so the change applies from the
 * user's next call. An administrator cannot take away their own right to manage users.
 */
@Service
public class ChangeUserRolesUseCase {

  private final UserRepository users;
  private final RoleAssignments roles;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  ChangeUserRolesUseCase(
      UserRepository users,
      RoleAssignments roles,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.users = users;
    this.roles = roles;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  /**
   * Replaces the roles.
   *
   * @param expectedVersion the version the caller last read, from {@code If-Match}; null skips the
   *     check
   */
  @Transactional
  public void replace(
      UserId userId, Set<UUID> roleIds, @Nullable Long expectedVersion, Actor actor) {
    AppUser user = users.findById(userId).orElseThrow(() -> NotFoundException.of("user", userId));
    if (expectedVersion != null && expectedVersion != user.version()) {
      throw new PreconditionFailedException("user", userId.value());
    }
    List<Role> next = roles.resolve(roleIds);
    if (userId.equals(actor.userId())
        && !RoleAssignments.permissionsOf(next).contains(IdentityPermissions.USERS_MANAGE)) {
      throw RuleViolationException.of("identity.cannot_remove_own_access");
    }
    Set<UUID> before = users.roleIdsOf(List.of(userId)).getOrDefault(userId, Set.of());
    List<Role> previous = roles.resolve(before);
    users.replaceRoles(userId, roleIds, actor.userId());
    events.publishEvent(
        new UserRolesChanged(ids.next(), TenantContext.requireId(), userId, clock.instant()));
    audit.record(
        AuditEntry.change(
            AuditAction.UPDATE,
            actor.audit(),
            actor.channel(),
            UserAudit.ENTITY_TYPE,
            userId.value(),
            user.username(),
            List.of("roles"),
            Map.of("roles", RoleAssignments.codes(previous)),
            Map.of("roles", RoleAssignments.codes(next)),
            "identity.user_roles_changed"));
  }
}
