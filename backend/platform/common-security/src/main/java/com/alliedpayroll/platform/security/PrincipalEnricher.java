package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.tenancy.CurrentTenant;

/**
 * Port the identity module implements: turns the identity provider's claims into the tenant's view
 * of the person. Called once per request for a member of the bound tenant; without an
 * implementation every member has no user row and no permissions.
 */
@FunctionalInterface
public interface PrincipalEnricher {

  TenantPrincipal enrich(CurrentTenant tenant, IdentityClaims claims);
}
