package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.security.AuthenticatedUser;
import com.alliedpayroll.platform.security.CurrentUserProvider;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.Bootstrap;
import com.alliedpayroll.tenant.api.BootstrapService;
import com.alliedpayroll.tenant.api.TenantSummary;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantRepository;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class BootstrapUseCase implements BootstrapService {

  private final TenantRepository tenants;
  private final FeatureFlags featureFlags;
  private final CurrentUserProvider users;
  private final TerminologyUseCase terminology;

  BootstrapUseCase(
      TenantRepository tenants,
      FeatureFlags featureFlags,
      CurrentUserProvider users,
      TerminologyUseCase terminology) {
    this.tenants = tenants;
    this.featureFlags = featureFlags;
    this.users = users;
    this.terminology = terminology;
  }

  @Override
  public Bootstrap forCurrentUser() {
    var current = TenantContext.require();
    Tenant tenant =
        tenants
            .findById(current.id())
            .filter(Tenant::isActive)
            .orElseThrow(() -> NotFoundException.of("tenant", current.slug()));
    AuthenticatedUser user = users.require();
    Map<String, Boolean> features = featureFlags.enabledFor(tenant.id());
    UserId userId = user.userId();
    return new Bootstrap(
        summaryOf(tenant),
        new Bootstrap.User(
            userId == null ? null : userId.value(),
            user.subject(),
            user.displayName(),
            user.email()),
        user.permissions(),
        features,
        terminology.closestTo(LocaleContextHolder.getLocale()).labels());
  }

  static TenantSummary summaryOf(Tenant tenant) {
    return new TenantSummary(
        tenant.id().value(),
        tenant.slug(),
        tenant.name(),
        tenant.industryProfileCode(),
        tenant.defaultLocale(),
        tenant.enabledLocales(),
        tenant.timezone());
  }
}
