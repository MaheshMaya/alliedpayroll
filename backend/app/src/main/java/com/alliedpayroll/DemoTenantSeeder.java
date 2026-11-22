package com.alliedpayroll;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.api.UserAdministration.EnsureUser;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import com.alliedpayroll.tenant.api.TenantSummary;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * The two reference tenants every developer and test environment has, with one user per persona: a
 * security staffing agency and an IT company. Keycloak's side (organisations, logins) is seeded by
 * the compose stack; the usernames here must match it.
 */
@Component
@ConditionalOnProperty(name = "alliedpayroll.seed.demo-tenants", havingValue = "true")
class DemoTenantSeeder implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(DemoTenantSeeder.class);

  private final TenantProvisioning provisioning;
  private final UserAdministration users;

  DemoTenantSeeder(TenantProvisioning provisioning, UserAdministration users) {
    this.provisioning = provisioning;
    this.users = users;
  }

  @Override
  public void run(ApplicationArguments args) {
    seed(
        new ProvisionTenant("sentinel", "Sentinel Facility Services", "STAFFING", "sentinel"),
        List.of(
            user(
                "admin.sentinel",
                "Asha Varma",
                "admin@sentinel.example",
                UserType.ADMIN,
                "TENANT_ADMIN"),
            user(
                "payroll.sentinel",
                "Rohan Iyer",
                "payroll@sentinel.example",
                UserType.MANAGER,
                "PAYROLL"),
            user("hr.sentinel", "Meera Nair", "hr@sentinel.example", UserType.MANAGER, "HR"),
            user(
                "guard.sentinel",
                "Suresh Kumar",
                "guard@sentinel.example",
                UserType.EMPLOYEE,
                "EMPLOYEE")));
    seed(
        new ProvisionTenant("nimbus-it", "Nimbus IT Solutions", "CORPORATE", "nimbus-it"),
        List.of(
            user(
                "admin.nimbus",
                "Divya Menon",
                "admin@nimbus-it.example",
                UserType.ADMIN,
                "TENANT_ADMIN"),
            user(
                "payroll.nimbus",
                "Karthik Rao",
                "payroll@nimbus-it.example",
                UserType.MANAGER,
                "PAYROLL"),
            user(
                "staff.nimbus",
                "Priya Shetty",
                "staff@nimbus-it.example",
                UserType.EMPLOYEE,
                "EMPLOYEE")));
    log.info("demo tenants present: sentinel, nimbus-it");
  }

  private void seed(ProvisionTenant tenant, List<EnsureUser> members) {
    TenantSummary summary = provisioning.provision(tenant);
    TenantContext.runAs(
        new CurrentTenant(new TenantId(summary.id()), summary.slug()),
        () -> members.forEach(users::ensureUser));
  }

  private static EnsureUser user(
      String username, String displayName, String email, UserType type, String role) {
    return new EnsureUser(username, displayName, email, type, Set.of(role));
  }
}
