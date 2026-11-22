package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.error.RuleViolationException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.TenantProvisioned;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantSummary;
import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantRepository;
import com.alliedpayroll.tenant.domain.TenantStatus;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a tenant from the newest version of its industry profile, or finds it by slug. Either way
 * the profile version the tenant was made from is applied again, which only fills in what is
 * missing, so seeds and retries are safe.
 */
@Service
@Transactional
class ProvisionTenantUseCase implements TenantProvisioning {

  private final TenantRepository tenants;
  private final ProfileTemplates templates;
  private final ApplyProfileTemplateUseCase applyTemplate;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;

  ProvisionTenantUseCase(
      TenantRepository tenants,
      ProfileTemplates templates,
      ApplyProfileTemplateUseCase applyTemplate,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events) {
    this.tenants = tenants;
    this.templates = templates;
    this.applyTemplate = applyTemplate;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
  }

  @Override
  public TenantSummary provision(ProvisionTenant command) {
    Tenant tenant = tenants.findBySlug(command.slug()).orElseGet(() -> create(command));
    ProfileTemplate template =
        templates
            .find(tenant.industryProfileCode(), tenant.profileTemplateVersion())
            .orElseThrow(() -> unknownProfile(tenant.industryProfileCode()));
    TenantContext.runAs(
        new CurrentTenant(tenant.id(), tenant.slug()),
        () -> applyTemplate.applyToCurrentTenant(template));
    return BootstrapUseCase.summaryOf(tenant);
  }

  private Tenant create(ProvisionTenant command) {
    ProfileTemplate template =
        templates
            .latest(command.industryProfileCode())
            .orElseThrow(() -> unknownProfile(command.industryProfileCode()));
    Tenant tenant =
        tenants.save(
            new Tenant(
                new TenantId(ids.next()),
                command.slug(),
                command.name(),
                template.code(),
                template.version(),
                TenantStatus.ACTIVE,
                "en-IN",
                List.of("en-IN"),
                "Asia/Kolkata",
                command.keycloakOrganizationId()));
    events.publishEvent(
        new TenantProvisioned(
            ids.next(),
            tenant.id(),
            tenant.slug(),
            tenant.industryProfileCode(),
            tenant.profileTemplateVersion(),
            clock.instant()));
    return tenant;
  }

  private static RuleViolationException unknownProfile(String code) {
    return new RuleViolationException("tenant.profile_unknown", Map.of("code", code));
  }
}
