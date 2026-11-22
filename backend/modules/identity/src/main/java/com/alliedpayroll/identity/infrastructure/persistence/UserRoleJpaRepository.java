package com.alliedpayroll.identity.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, UserRoleEntity.Key> {

  List<UserRoleEntity> findByKeyUserIdIn(Collection<UUID> userIds);
}
