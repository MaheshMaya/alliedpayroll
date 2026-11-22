package com.alliedpayroll;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.api.UserAdministration.EnsureUser;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import com.alliedpayroll.tenant.api.TenantSummary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The seeded people every API test shares. Test classes share one database, and a user row is
 * created once by whichever class seeds first, so every class must describe the same people.
 */
final class DemoPeople {

  record Tenants(CurrentTenant sentinel, CurrentTenant nimbus, Map<String, UserId> users) {
    UserId user(String username) {
      return java.util.Objects.requireNonNull(users.get(username), username);
    }
  }

  private DemoPeople() {}

  static Tenants seed(TenantProvisioning provisioning, UserAdministration users) {
    Map<String, UserId> ids = new HashMap<>();
    CurrentTenant sentinel =
        tenant(
            provisioning.provision(
                new ProvisionTenant("sentinel", "Sentinel", "STAFFING", "sentinel")));
    TenantContext.runAs(
        sentinel,
        () -> {
          ids.put(
              "admin.sentinel",
              users.ensureUser(
                  person(
                      "admin.sentinel",
                      "Asha Varma",
                      "admin@sentinel.example",
                      UserType.ADMIN,
                      "TENANT_ADMIN")));
          ids.put(
              "hr.sentinel",
              users.ensureUser(
                  person(
                      "hr.sentinel", "Meera Nair", "hr@sentinel.example", UserType.MANAGER, "HR")));
          ids.put(
              "guard.sentinel",
              users.ensureUser(
                  person(
                      "guard.sentinel",
                      "Suresh Kumar",
                      "guard@sentinel.example",
                      UserType.EMPLOYEE,
                      "EMPLOYEE")));
          ids.put(
              "ops.sentinel",
              users.ensureUser(
                  person(
                      "ops.sentinel",
                      "Nitya Rao",
                      "ops@sentinel.example",
                      UserType.EMPLOYEE,
                      "EMPLOYEE")));
          // Nobody signs this person in, so their sign-in fields stay empty in every test.
          ids.put(
              "payroll.sentinel",
              users.ensureUser(
                  person(
                      "payroll.sentinel",
                      "Rohan Iyer",
                      "payroll@sentinel.example",
                      UserType.MANAGER,
                      "PAYROLL")));
        });
    CurrentTenant nimbus =
        tenant(
            provisioning.provision(
                new ProvisionTenant("nimbus-it", "Nimbus IT", "CORPORATE", "nimbus-it")));
    TenantContext.runAs(
        nimbus,
        () ->
            ids.put(
                "admin.nimbus",
                users.ensureUser(
                    person(
                        "admin.nimbus",
                        "Divya Menon",
                        "admin@nimbus-it.example",
                        UserType.ADMIN,
                        "TENANT_ADMIN"))));
    return new Tenants(sentinel, nimbus, Map.copyOf(ids));
  }

  private static EnsureUser person(
      String username, String name, String email, UserType type, String role) {
    return new EnsureUser(username, name, email, type, Set.of(role));
  }

  private static CurrentTenant tenant(TenantSummary summary) {
    return new CurrentTenant(new TenantId(summary.id()), summary.slug());
  }
}
