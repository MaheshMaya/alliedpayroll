package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.EmailMask;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.identity.domain.ScopeMode;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.identity.domain.UserStatus;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.platform.core.ids.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class EnsureUserUseCase implements UserAdministration {

  private final UserRepository users;
  private final RoleRepository roles;
  private final EnsureSystemRolesUseCase systemRoles;
  private final IdGenerator ids;

  EnsureUserUseCase(
      UserRepository users,
      RoleRepository roles,
      EnsureSystemRolesUseCase systemRoles,
      IdGenerator ids) {
    this.users = users;
    this.roles = roles;
    this.systemRoles = systemRoles;
    this.ids = ids;
  }

  @Override
  public UserId ensureUser(EnsureUser command) {
    systemRoles.ensureForCurrentTenant();
    AppUser user =
        users
            .findByUsername(command.username())
            .orElseGet(() ->
                users.save(
                    new AppUser(
                        new UserId(ids.next()),
                        command.username(),
                        command.displayName(),
                        EmailMask.mask(command.email()),
                        command.userType(),
                        UserStatus.INVITED,
                        command.userType() == com.alliedpayroll.identity.api.UserType.ADMIN
                            ? ScopeMode.ALL
                            : ScopeMode.RESTRICTED,
                        null,
                        null)));
    for (String code : command.roleCodes()) {
      Role role = roles.findByCode(code).orElseThrow(() -> NotFoundException.of("role", code));
      users.assignRole(user.id(), role.id(), SystemActor.ID);
    }
    return user.id();
  }
}
