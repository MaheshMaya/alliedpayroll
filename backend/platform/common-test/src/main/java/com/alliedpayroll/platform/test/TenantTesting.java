package com.alliedpayroll.platform.test;

import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Runs test code as a given tenant, the way the filter does for a request. */
public final class TenantTesting {

  private TenantTesting() {}

  public static CurrentTenant tenant(String slug) {
    return new CurrentTenant(
        new TenantId(UUID.nameUUIDFromBytes(slug.getBytes(StandardCharsets.UTF_8))), slug);
  }

  public static void runAs(CurrentTenant tenant, Runnable work) {
    TenantContext.runAs(tenant, work);
  }

  public static <T> T callAs(
      CurrentTenant tenant, ScopedValue.CallableOp<T, RuntimeException> work) {
    return TenantContext.callAs(tenant, work);
  }
}
