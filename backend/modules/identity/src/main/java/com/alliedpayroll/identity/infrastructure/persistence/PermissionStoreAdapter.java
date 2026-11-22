package com.alliedpayroll.identity.infrastructure.persistence;

import com.alliedpayroll.identity.domain.PermissionStore;
import com.alliedpayroll.platform.security.PermissionDefinition;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class PermissionStoreAdapter implements PermissionStore {

  private final PermissionJpaRepository permissions;
  private final Clock clock;

  PermissionStoreAdapter(PermissionJpaRepository permissions, Clock clock) {
    this.permissions = permissions;
    this.clock = clock;
  }

  @Override
  @Transactional
  public void upsert(List<PermissionDefinition> definitions) {
    Instant now = clock.instant();
    for (PermissionDefinition definition : definitions) {
      permissions
          .findById(definition.code())
          .ifPresentOrElse(
              existing -> existing.apply(definition, now),
              () -> permissions.save(new PermissionEntity(definition, now)));
    }
  }

  @Override
  public List<PermissionDefinition> findAll() {
    return permissions.findAll(Sort.by("moduleCode", "code")).stream()
        .map(PermissionEntity::toDefinition)
        .toList();
  }
}
