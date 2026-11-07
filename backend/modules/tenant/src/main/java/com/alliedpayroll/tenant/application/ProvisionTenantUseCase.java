package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantSummary;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantRepository;
import com.alliedpayroll.tenant.domain.TenantStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ProvisionTenantUseCase implements TenantProvisioning {

  private final TenantRepository tenants;
  private final IdGenerator ids;

  ProvisionTenantUseCase(TenantRepository tenants, IdGenerator ids) {
    this.tenants = tenants;
    this.ids = ids;
  }

  @Override
  public TenantSummary provision(ProvisionTenant command) {
    Tenant tenant =
        tenants
            .findBySlug(command.slug())
            .orElseGet(() ->
                tenants.save(
                    new Tenant(
                        new TenantId(ids.next()),
                        command.slug(),
                        command.name(),
                        command.industryProfileCode(),
                        TenantStatus.ACTIVE,
                        "en-IN",
                        List.of("en-IN"),
                        "Asia/Kolkata",
                        command.keycloakOrganizationId())));
    return BootstrapUseCase.summaryOf(tenant);
  }
}
