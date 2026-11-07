package com.alliedpayroll.platform.security;

import org.springframework.stereotype.Component;

/**
 * Evaluated by {@link RequiresPermission}; the bean name is part of the annotation's expression.
 */
@Component("permissions")
public class Permissions {

  private final CurrentUserProvider users;

  public Permissions(CurrentUserProvider users) {
    this.users = users;
  }

  public boolean has(String permission) {
    return users.current().map(user -> user.has(permission)).orElse(false);
  }
}
