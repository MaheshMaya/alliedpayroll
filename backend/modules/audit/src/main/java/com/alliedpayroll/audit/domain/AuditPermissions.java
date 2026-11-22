package com.alliedpayroll.audit.domain;

import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;

public final class AuditPermissions {

  public static final String MODULE = "AUDIT";
  public static final String READ = "audit:read";
  public static final String SECURITY_READ = "audit:security_read";

  public static final List<PermissionDefinition> ALL =
      List.of(
          PermissionDefinition.of(READ, MODULE), PermissionDefinition.of(SECURITY_READ, MODULE));

  private AuditPermissions() {}
}
