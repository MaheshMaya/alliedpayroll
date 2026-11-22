package com.alliedpayroll.identity.infrastructure.events;

import com.alliedpayroll.identity.application.EnsureSystemRolesUseCase;
import com.alliedpayroll.platform.events.ProcessedEvents;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.TenantProvisioned;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** A new tenant gets the system roles, whoever provisioned it. */
@Component
class TenantProvisionedListener {

  private static final String LISTENER_ID = TenantProvisionedListener.class.getName();

  private final EnsureSystemRolesUseCase systemRoles;
  private final ProcessedEvents processed;

  TenantProvisionedListener(EnsureSystemRolesUseCase systemRoles, ProcessedEvents processed) {
    this.systemRoles = systemRoles;
    this.processed = processed;
  }

  @ApplicationModuleListener
  void on(TenantProvisioned event) {
    if (!processed.markProcessed(LISTENER_ID, event.eventId())) {
      return;
    }
    TenantContext.runAs(
        new CurrentTenant(event.tenantId(), event.slug()), systemRoles::ensureForCurrentTenant);
  }
}
