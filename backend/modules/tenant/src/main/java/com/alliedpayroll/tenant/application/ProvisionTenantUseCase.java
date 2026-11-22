package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.tenant.api.TenantProvisioned;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantSummary;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantRepository;
import com.alliedpayroll.tenant.domain.TenantStatus;
import java.time.Clock;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ProvisionTenantUseCase implements TenantProvisioning {

  private final TenantRepository tenants;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;

  ProvisionTenantUseCase(
      TenantRepository tenants, IdGenerator ids, Clock clock, ApplicationEventPublisher events) {
    this.tenants = tenants;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
  }

  @Override
  public TenantSummary provision(ProvisionTenant command) {
    Tenant tenant = tenants.findBySlug(command.slug()).orElseGet(() -> create(command));
    return BootstrapUseCase.summaryOf(tenant);
  }

  private Tenant create(ProvisionTenant command) {
    Tenant tenant =
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
                command.keycloakOrganizationId()));
    events.publishEvent(
        new TenantProvisioned(
            ids.next(), tenant.id(), tenant.slug(), tenant.industryProfileCode(), clock.instant()));
    return tenant;
  }
}
