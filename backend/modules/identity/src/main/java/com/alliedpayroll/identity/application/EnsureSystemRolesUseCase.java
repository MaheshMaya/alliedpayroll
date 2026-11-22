package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.identity.domain.SystemRoles;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gives the bound tenant every system role it does not have yet. Runs in its own transaction so a
 * caller that bound the tenant after its transaction began, such as an event listener, still writes
 * with the tenant on the connection.
 */
@Service
public class EnsureSystemRolesUseCase {

  private final RoleRepository roles;
  private final IdGenerator ids;

  EnsureSystemRolesUseCase(RoleRepository roles, IdGenerator ids) {
    this.roles = roles;
    this.ids = ids;
  }

  private static java.util.Set<String> missing(Role existing, SystemRoles.SystemRole template) {
    java.util.Set<String> wanted = new java.util.HashSet<>(template.permissions());
    wanted.removeAll(existing.permissions());
    return wanted;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ensureForCurrentTenant() {
    roles.lockSeeding();
    for (SystemRoles.SystemRole template : SystemRoles.all()) {
      roles
          .findByCode(template.code())
          .ifPresentOrElse(
              // A system role gains what newer modules added to its template; it never loses.
              existing -> roles.grant(existing.id(), missing(existing, template)),
              () ->
                  roles.save(
                      new Role(
                          ids.next(),
                          template.code(),
                          template.name(),
                          null,
                          true,
                          template.permissions())));
    }
  }
}
