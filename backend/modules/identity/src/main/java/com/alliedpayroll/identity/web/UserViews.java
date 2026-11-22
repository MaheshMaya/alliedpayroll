package com.alliedpayroll.identity.web;

import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.identity.application.UserView;
import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.ScopeMode;
import com.alliedpayroll.identity.domain.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/** Response shapes of the users resource. */
final class UserViews {

  private UserViews() {}

  record RoleRef(UUID id, String code, String name) {}

  record UserSummary(
      UUID id,
      String username,
      String displayName,
      @Nullable String emailMasked,
      UserType userType,
      UserStatus status,
      ScopeMode scopeMode,
      List<RoleRef> roles,
      @Nullable Instant lastLoginAt) {}

  record UserDetail(
      UUID id,
      String username,
      String displayName,
      @Nullable String emailMasked,
      UserType userType,
      UserStatus status,
      ScopeMode scopeMode,
      boolean linked,
      List<RoleRef> roles,
      Set<String> permissions,
      @Nullable Instant lastLoginAt) {}

  static UserSummary summary(UserView view) {
    AppUser user = view.user();
    return new UserSummary(
        user.id().value(),
        user.username(),
        user.displayName(),
        user.emailMasked(),
        user.userType(),
        user.status(),
        user.scopeMode(),
        refs(view.roles()),
        user.lastLoginAt());
  }

  static UserDetail detail(UserView view) {
    AppUser user = view.user();
    return new UserDetail(
        user.id().value(),
        user.username(),
        user.displayName(),
        user.emailMasked(),
        user.userType(),
        user.status(),
        user.scopeMode(),
        user.isLinked(),
        refs(view.roles()),
        view.permissions(),
        user.lastLoginAt());
  }

  static List<RoleRef> refs(List<Role> roles) {
    return roles.stream().map(role -> new RoleRef(role.id(), role.code(), role.name())).toList();
  }
}
