package com.alliedpayroll.platform.tenancy;

import com.alliedpayroll.platform.core.ids.TenantId;
import java.util.Optional;

/**
 * The tenant of the work in progress, bound for the duration of a request, a job step or a
 * listener. It is never taken from a request body or parameter: the filter sets it from the host
 * and the security layer checks it against the principal.
 */
public final class TenantContext {

  private static final ScopedValue<CurrentTenant> CURRENT = ScopedValue.newInstance();

  private TenantContext() {}

  public static Optional<CurrentTenant> current() {
    return CURRENT.isBound() ? Optional.of(CURRENT.get()) : Optional.empty();
  }

  public static CurrentTenant require() {
    if (!CURRENT.isBound()) {
      throw new MissingTenantException();
    }
    return CURRENT.get();
  }

  public static TenantId requireId() {
    return require().id();
  }

  public static void runAs(CurrentTenant tenant, Runnable work) {
    ScopedValue.where(CURRENT, tenant).run(work);
  }

  public static <T, X extends Throwable> T callAs(
      CurrentTenant tenant, ScopedValue.CallableOp<T, X> work) throws X {
    return ScopedValue.where(CURRENT, tenant).call(work);
  }
}
