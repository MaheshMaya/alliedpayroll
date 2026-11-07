package com.alliedpayroll.platform.tenancy;

/** Tenant-scoped work was attempted without a bound tenant; the caller fails closed. */
public final class MissingTenantException extends IllegalStateException {

  public MissingTenantException() {
    super("no tenant bound to the current scope");
  }
}
