package com.alliedpayroll.tenant.infrastructure.persistence;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.tenant.application.FeatureFlags;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
class FeatureFlagsAdapter implements FeatureFlags {

  private final FeatureFlagJpaRepository jpa;

  FeatureFlagsAdapter(FeatureFlagJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Map<String, Boolean> enabledFor(TenantId tenantId) {
    Map<String, Boolean> flags = new LinkedHashMap<>();
    for (FeatureFlagEntity flag : jpa.findAllByTenantId(tenantId.value())) {
      flags.put(flag.flagKey(), flag.enabled());
    }
    return Map.copyOf(flags);
  }
}
