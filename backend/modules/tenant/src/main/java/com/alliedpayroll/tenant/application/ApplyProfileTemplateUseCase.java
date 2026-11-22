package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.domain.TenantModule;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gives the bound tenant the module flags of its profile that it has no row for yet. Additive: a
 * module the tenant switched on or off keeps its value. Labels are not copied; the profile is a
 * layer under the tenant's own labels (see {@code Terminology}).
 *
 * <p>Runs in its own transaction so the tenant bound by the caller is on the connection, which
 * row-level security needs for the inserts.
 */
@Service
class ApplyProfileTemplateUseCase {

  private final FeatureFlags flags;

  ApplyProfileTemplateUseCase(FeatureFlags flags) {
    this.flags = flags;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void applyToCurrentTenant(ProfileTemplate template) {
    Map<String, Boolean> moduleFlags = new LinkedHashMap<>();
    for (Map.Entry<TenantModule, Boolean> module : template.modules().entrySet()) {
      moduleFlags.put(module.getKey().flagKey(), module.getValue());
    }
    flags.addMissing(moduleFlags, SystemActor.ID);
  }
}
