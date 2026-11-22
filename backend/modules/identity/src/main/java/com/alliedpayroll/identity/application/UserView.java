package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.Role;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** A user with the roles assigned to them, as lists and details show them. */
public record UserView(AppUser user, List<Role> roles) {

  public UserView {
    roles = List.copyOf(roles);
  }

  public Set<String> permissions() {
    Set<String> union = new TreeSet<>();
    for (Role role : roles) {
      union.addAll(role.permissions());
    }
    return Set.copyOf(union);
  }
}
