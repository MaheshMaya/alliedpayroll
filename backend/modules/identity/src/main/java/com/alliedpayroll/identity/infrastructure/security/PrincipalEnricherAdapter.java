package com.alliedpayroll.identity.infrastructure.security;

import com.alliedpayroll.identity.application.ResolvePrincipalUseCase;
import com.alliedpayroll.platform.security.IdentityClaims;
import com.alliedpayroll.platform.security.PrincipalEnricher;
import com.alliedpayroll.platform.security.TenantPrincipal;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import org.springframework.stereotype.Component;

@Component
class PrincipalEnricherAdapter implements PrincipalEnricher {

  private final ResolvePrincipalUseCase principals;

  PrincipalEnricherAdapter(ResolvePrincipalUseCase principals) {
    this.principals = principals;
  }

  @Override
  public TenantPrincipal enrich(CurrentTenant tenant, IdentityClaims claims) {
    return principals.resolve(tenant, claims);
  }
}
