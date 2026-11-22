package com.alliedpayroll.tenant.domain;

import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;

/** The tenant module's entries in the permission catalogue. */
public final class TenantPermissions {

  public static final String MODULE = "TENANT";
  public static final String SETTINGS_MANAGE = "tenant:settings_manage";

  public static final List<PermissionDefinition> ALL =
      List.of(PermissionDefinition.of(SETTINGS_MANAGE, MODULE));

  private TenantPermissions() {}
}
