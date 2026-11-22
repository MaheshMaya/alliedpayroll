package com.alliedpayroll.identity.domain;

import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;

/** The identity module's own entries in the permission catalogue. */
public final class IdentityPermissions {

  public static final String MODULE = "IDENTITY";
  public static final String USERS_READ = "identity:users_read";
  public static final String USERS_MANAGE = "identity:users_manage";
  public static final String ROLES_MANAGE = "identity:roles_manage";

  public static final List<PermissionDefinition> ALL =
      List.of(
          PermissionDefinition.of(USERS_READ, MODULE),
          PermissionDefinition.of(USERS_MANAGE, MODULE),
          PermissionDefinition.of(ROLES_MANAGE, MODULE));

  private IdentityPermissions() {}
}
