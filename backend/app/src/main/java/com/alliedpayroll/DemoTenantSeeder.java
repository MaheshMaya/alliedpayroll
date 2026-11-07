package com.alliedpayroll;

import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * The two reference tenants every developer and test environment has: a security staffing agency
 * and an IT company. Their Keycloak organisations and users are seeded by the compose stack.
 */
@Component
@ConditionalOnProperty(name = "alliedpayroll.seed.demo-tenants", havingValue = "true")
class DemoTenantSeeder implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(DemoTenantSeeder.class);

  private final TenantProvisioning provisioning;

  DemoTenantSeeder(TenantProvisioning provisioning) {
    this.provisioning = provisioning;
  }

  @Override
  public void run(ApplicationArguments args) {
    provisioning.provision(
        new ProvisionTenant("sentinel", "Sentinel Facility Services", "STAFFING", "sentinel"));
    provisioning.provision(
        new ProvisionTenant("nimbus-it", "Nimbus IT Solutions", "CORPORATE", "nimbus-it"));
    log.info("demo tenants present: sentinel, nimbus-it");
  }
}
