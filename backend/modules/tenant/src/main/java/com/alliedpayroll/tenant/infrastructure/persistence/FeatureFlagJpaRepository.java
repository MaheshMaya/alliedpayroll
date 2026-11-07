package com.alliedpayroll.tenant.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface FeatureFlagJpaRepository extends JpaRepository<FeatureFlagEntity, UUID> {

  List<FeatureFlagEntity> findAllByTenantId(UUID tenantId);
}
