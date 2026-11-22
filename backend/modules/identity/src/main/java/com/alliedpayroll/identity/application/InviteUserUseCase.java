package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserInvited;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.identity.application.IdentityDirectory.NewAccount;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.EmailMask;
import com.alliedpayroll.identity.domain.PersonName;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.error.ConflictException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * An administrator invites a person by email. The tenant row starts {@code INVITED}, the account is
 * created in the identity provider as a member of the tenant's organisation, and the first sign-in
 * activates the row. The email is the username, so it is unique across the realm.
 */
@Service
public class InviteUserUseCase {

  private final UserRepository users;
  private final RoleAssignments roles;
  private final IdentityDirectory directory;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  InviteUserUseCase(
      UserRepository users,
      RoleAssignments roles,
      IdentityDirectory directory,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.users = users;
    this.roles = roles;
    this.directory = directory;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  /** The person to invite; {@code roleIds} must name roles of the tenant. */
  public record InviteUser(String displayName, String email, UserType userType, Set<UUID> roleIds) {
    public InviteUser {
      roleIds = Set.copyOf(roleIds);
    }
  }

  @Transactional
  public UserId invite(InviteUser command, Actor actor) {
    CurrentTenant tenant = TenantContext.require();
    String email = command.email().trim().toLowerCase(Locale.ROOT);
    if (users.findByUsername(email).isPresent()) {
      throw new ConflictException("identity.user_exists", Map.of("username", email));
    }
    final List<Role> granted = roles.resolve(command.roleIds());
    AppUser user =
        users.save(
            AppUser.invited(
                new UserId(ids.next()),
                email,
                command.displayName().trim(),
                EmailMask.mask(email),
                command.userType()),
            actor.userId());
    users.replaceRoles(user.id(), command.roleIds(), actor.userId());
    PersonName name = PersonName.of(user.displayName());
    // Last, so a refusal from the identity provider rolls the tenant row back with it.
    directory.invite(tenant.slug(), new NewAccount(email, email, name.first(), name.last()));
    events.publishEvent(new UserInvited(ids.next(), tenant.id(), user.id(), clock.instant()));
    audit.record(
        AuditEntry.change(
            AuditAction.CREATE,
            actor.audit(),
            actor.channel(),
            UserAudit.ENTITY_TYPE,
            user.id().value(),
            user.username(),
            List.of("displayName", "email", "userType", "roles"),
            null,
            Map.of(
                "displayName", user.displayName(),
                "email", String.valueOf(user.emailMasked()),
                "userType", user.userType().name(),
                "roles", RoleAssignments.codes(granted)),
            "identity.user_invited"));
    return user.id();
  }
}
