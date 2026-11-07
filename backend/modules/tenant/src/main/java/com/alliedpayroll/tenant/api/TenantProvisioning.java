package com.alliedpayroll.tenant.api;

/** Creates tenants. Idempotent on slug so seeds and retries are safe. */
public interface TenantProvisioning {

  TenantSummary provision(ProvisionTenant command);

  record ProvisionTenant(
      String slug, String name, String industryProfileCode, String keycloakOrganizationId) {}
}
