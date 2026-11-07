package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.ids.TenantId;
import java.util.Map;

/** Port: which flags are on for a tenant, keyed by flag key such as {@code module:CUSTOMERS}. */
public interface FeatureFlags {

  Map<String, Boolean> enabledFor(TenantId tenantId);
}
