package com.alliedpayroll.platform.tenancy;

import com.alliedpayroll.platform.core.ids.TenantId;
import java.util.Optional;

/** Lookup of active tenants. Implemented by the tenant module; used by the filter and by jobs. */
public interface TenantDirectory {

  Optional<CurrentTenant> findActiveBySlug(String slug);

  Optional<CurrentTenant> findActive(TenantId id);
}
