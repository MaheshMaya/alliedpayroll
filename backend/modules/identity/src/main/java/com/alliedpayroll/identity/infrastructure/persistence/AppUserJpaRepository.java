package com.alliedpayroll.identity.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface AppUserJpaRepository
    extends JpaRepository<AppUserEntity, UUID>, JpaSpecificationExecutor<AppUserEntity> {

  Optional<AppUserEntity> findByIdAndDeletedAtIsNull(UUID id);

  Optional<AppUserEntity> findByKeycloakSubjectAndDeletedAtIsNull(String subject);

  Optional<AppUserEntity> findByUsernameAndDeletedAtIsNull(String username);

  List<AppUserEntity> findByIdIn(Collection<UUID> ids);
}
