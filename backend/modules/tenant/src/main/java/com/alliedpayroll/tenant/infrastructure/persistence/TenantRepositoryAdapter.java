package com.alliedpayroll.tenant.infrastructure.persistence;

import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantDirectory;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the domain port and the platform's {@link TenantDirectory}, which the request filter
 * calls on every request; the directory is small and read-mostly, so no cache yet.
 */
@Repository
@Transactional(readOnly = true)
class TenantRepositoryAdapter implements TenantRepository, TenantDirectory {

  private final TenantJpaRepository jpa;
  private final Clock clock;

  TenantRepositoryAdapter(TenantJpaRepository jpa, Clock clock) {
    this.jpa = jpa;
    this.clock = clock;
  }

  @Override
  public Optional<Tenant> findById(TenantId id) {
    return jpa.findById(id.value()).map(TenantEntity::toDomain);
  }

  @Override
  public Optional<Tenant> findBySlug(String slug) {
    return jpa.findBySlug(slug).map(TenantEntity::toDomain);
  }

  @Override
  @Transactional
  public Tenant save(Tenant tenant) {
    Instant now = clock.instant();
    return jpa.save(new TenantEntity(tenant, now, SystemActor.ID.value())).toDomain();
  }

  @Override
  public Optional<CurrentTenant> findActiveBySlug(String slug) {
    return findBySlug(slug).filter(Tenant::isActive).map(TenantRepositoryAdapter::current);
  }

  @Override
  public Optional<CurrentTenant> findActive(TenantId id) {
    return findById(id).filter(Tenant::isActive).map(TenantRepositoryAdapter::current);
  }

  private static CurrentTenant current(Tenant tenant) {
    return new CurrentTenant(tenant.id(), tenant.slug());
  }
}
