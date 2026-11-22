package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/** Port: which flags are on for a tenant, keyed by flag key such as {@code module:CUSTOMERS}. */
public interface FeatureFlags {

  Map<String, Boolean> enabledFor(TenantId tenantId);

  /** Adds the flags the bound tenant has no row for; an existing row keeps its value. */
  void addMissing(Map<String, Boolean> flags, UserId actor);

  /** Sets one flag of the bound tenant, creating its row when there is none. */
  FlagChange set(String flagKey, boolean enabled, UserId actor);

  /** What a set changed; {@code before} is null when the tenant had no row for the flag. */
  record FlagChange(UUID id, @Nullable Boolean before) {}
}
