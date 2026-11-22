package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.platform.core.error.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Turns role ids from a request into the tenant's roles, refusing ids it does not have. */
@Component
class RoleAssignments {

  private final RoleRepository roles;

  RoleAssignments(RoleRepository roles) {
    this.roles = roles;
  }

  List<Role> resolve(Set<UUID> roleIds) {
    Map<UUID, Role> byId =
        roles.findAll().stream().collect(Collectors.toMap(Role::id, Function.identity()));
    return roleIds.stream()
        .map(id -> {
          Role role = byId.get(id);
          if (role == null) {
            throw NotFoundException.of("role", id);
          }
          return role;
        })
        .toList();
  }

  static Set<String> permissionsOf(Collection<Role> granted) {
    Set<String> union = new TreeSet<>();
    granted.forEach(role -> union.addAll(role.permissions()));
    return union;
  }

  /** Role codes in a stable order, for the audit trail. */
  static List<String> codes(Collection<Role> granted) {
    return granted.stream().map(Role::code).sorted().toList();
  }
}
