package com.alliedpayroll.identity.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface RolePermissionJpaRepository
    extends JpaRepository<RolePermissionEntity, RolePermissionEntity.Key> {

  List<RolePermissionEntity> findByKeyRoleIdIn(Collection<UUID> roleIds);
}
