package com.alliedpayroll.tenant.domain;

import com.alliedpayroll.platform.core.ids.TenantId;
import java.util.Optional;

/** Port owned by the domain; the JPA adapter in infrastructure implements it. */
public interface TenantRepository {

  Optional<Tenant> findById(TenantId id);

  Optional<Tenant> findBySlug(String slug);

  Tenant save(Tenant tenant);
}
