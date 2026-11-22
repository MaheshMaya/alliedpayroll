package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.platform.security.IdentityClaims;
import com.alliedpayroll.platform.security.TenantPrincipal;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * What the tenant knows about the person a token describes. A provisioned user whose Keycloak
 * subject is not linked yet is linked on their first request, by username; a member the tenant
 * never provisioned gets no permissions.
 */
@Service
public class ResolvePrincipalUseCase {

  private final UserRepository users;
  private final RoleRepository roles;
  private final LinkSubjectUseCase linking;

  ResolvePrincipalUseCase(UserRepository users, RoleRepository roles, LinkSubjectUseCase linking) {
    this.users = users;
    this.roles = roles;
    this.linking = linking;
  }

  public TenantPrincipal resolve(CurrentTenant tenant, IdentityClaims claims) {
    Optional<AppUser> user = users.findBySubject(claims.subject()).or(() -> link(tenant, claims));
    return user.filter(AppUser::canSignIn)
        .map(found -> new TenantPrincipal(found.id(), permissionsOf(found)))
        .orElse(TenantPrincipal.none());
  }

  private Optional<AppUser> link(CurrentTenant tenant, IdentityClaims claims) {
    String username = claims.username();
    if (username == null) {
      return Optional.empty();
    }
    return users
        .findByUsername(username)
        .filter(user -> !user.isLinked() && user.canSignIn())
        .map(user -> linking.link(tenant, user, claims.subject(), claims.channel().name()));
  }

  private Set<String> permissionsOf(AppUser user) {
    Map<com.alliedpayroll.platform.core.ids.UserId, Set<UUID>> assigned =
        users.roleIdsOf(List.of(user.id()));
    Set<UUID> roleIds = assigned.getOrDefault(user.id(), Set.of());
    Set<String> union = new TreeSet<>();
    for (Role role : roles.findAll()) {
      if (roleIds.contains(role.id())) {
        union.addAll(role.permissions());
      }
    }
    return union;
  }
}
