package com.alliedpayroll.tenant.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface FeatureFlagJpaRepository extends JpaRepository<FeatureFlagEntity, UUID> {

  List<FeatureFlagEntity> findAllByTenantId(UUID tenantId);

  /** The bound tenant's row; Hibernate adds the tenant predicate. */
  Optional<FeatureFlagEntity> findByFlagKey(String flagKey);

  List<FeatureFlagEntity> findAllByFlagKeyIn(Collection<String> flagKeys);
}
