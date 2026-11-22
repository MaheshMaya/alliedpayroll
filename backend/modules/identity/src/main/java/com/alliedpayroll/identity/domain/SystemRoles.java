package com.alliedpayroll.identity.domain;

import java.util.List;
import java.util.Set;

/**
 * The roles every tenant starts with. They are copied into the tenant at provisioning as {@code
 * system} roles; a tenant adds its own roles beside them. Permissions grow as modules ship: a
 * module's slice adds its codes to the roles that should have them.
 */
public final class SystemRoles {

  public record SystemRole(String code, String name, Set<String> permissions) {
    public SystemRole {
      permissions = Set.copyOf(permissions);
    }
  }

  public static final String TENANT_ADMIN = "TENANT_ADMIN";
  public static final String PAYROLL = "PAYROLL";
  public static final String HR = "HR";
  public static final String SUPERVISOR = "SUPERVISOR";
  public static final String EMPLOYEE = "EMPLOYEE";
  public static final String CLIENT_CONTACT = "CLIENT_CONTACT";

  private static final List<SystemRole> ALL =
      List.of(
          new SystemRole(
              TENANT_ADMIN,
              "Administrator",
              Set.of(
                  IdentityPermissions.USERS_READ,
                  IdentityPermissions.USERS_MANAGE,
                  IdentityPermissions.ROLES_MANAGE,
                  // Declared by the audit and tenant modules; identity only names them.
                  "audit:read",
                  "audit:security_read",
                  "tenant:settings_manage")),
          new SystemRole(PAYROLL, "Payroll", Set.of()),
          new SystemRole(HR, "HR", Set.of(IdentityPermissions.USERS_READ)),
          new SystemRole(SUPERVISOR, "Supervisor", Set.of()),
          new SystemRole(EMPLOYEE, "Employee", Set.of()),
          new SystemRole(CLIENT_CONTACT, "Client contact", Set.of()));

  private SystemRoles() {}

  public static List<SystemRole> all() {
    return ALL;
  }
}
