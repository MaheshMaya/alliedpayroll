package com.alliedpayroll.identity.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

  List<RoleEntity> findByDeletedAtIsNullOrderByName();

  Optional<RoleEntity> findByIdAndDeletedAtIsNull(UUID id);

  Optional<RoleEntity> findByCodeAndDeletedAtIsNull(String code);
}
