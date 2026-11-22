package com.alliedpayroll.identity.infrastructure.persistence;

import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.TenantContext;
import jakarta.persistence.EntityManager;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class RoleRepositoryAdapter implements RoleRepository {

  private final RoleJpaRepository roles;
  private final RolePermissionJpaRepository grants;
  private final Clock clock;
  private final EntityManager entityManager;

  RoleRepositoryAdapter(
      RoleJpaRepository roles,
      RolePermissionJpaRepository grants,
      Clock clock,
      EntityManager entityManager) {
    this.roles = roles;
    this.grants = grants;
    this.clock = clock;
    this.entityManager = entityManager;
  }

  @Override
  public List<Role> findAll() {
    return toDomain(roles.findByDeletedAtIsNullOrderByName());
  }

  @Override
  public Optional<Role> findById(UUID id) {
    return roles.findByIdAndDeletedAtIsNull(id).map(role -> toDomain(List.of(role)).getFirst());
  }

  @Override
  public Optional<Role> findByCode(String code) {
    return roles.findByCodeAndDeletedAtIsNull(code).map(role -> toDomain(List.of(role)).getFirst());
  }

  @Override
  @Transactional
  public Role save(Role role) {
    return create(role, SystemActor.ID);
  }

  @Override
  @Transactional
  public Role create(Role role, UserId actor) {
    Instant now = clock.instant();
    RoleEntity saved = roles.save(new RoleEntity(role, now, actor.value()));
    for (String permission : role.permissions()) {
      grants.save(new RolePermissionEntity(saved.id(), permission, now, actor.value()));
    }
    return saved.toDomain(role.permissions());
  }

  @Override
  @Transactional
  public Role update(Role role, UserId actor) {
    RoleEntity entity = roles.findByIdAndDeletedAtIsNull(role.id()).orElseThrow();
    entity.apply(role, clock.instant(), actor.value());
    for (RolePermissionEntity grant : grants.findByKeyRoleIdIn(List.of(role.id()))) {
      if (!role.permissions().contains(grant.key().permissionCode())) {
        grants.delete(grant);
      }
    }
    grant(role.id(), role.permissions(), actor);
    roles.flush();
    return findById(role.id()).orElseThrow();
  }

  @Override
  @Transactional
  public void grant(UUID roleId, Set<String> permissions) {
    grant(roleId, permissions, SystemActor.ID);
  }

  private void grant(UUID roleId, Set<String> permissions, UserId actor) {
    Instant now = clock.instant();
    for (String permission : permissions) {
      RolePermissionEntity.Key key = new RolePermissionEntity.Key(roleId, permission);
      if (!grants.existsById(key)) {
        grants.save(new RolePermissionEntity(roleId, permission, now, actor.value()));
      }
    }
  }

  @Override
  @Transactional
  public void lockSeeding() {
    entityManager
        .createNativeQuery("SELECT pg_advisory_xact_lock(hashtext(:key))")
        .setParameter("key", "identity.role:" + TenantContext.requireId())
        .getSingleResult();
  }

  private List<Role> toDomain(List<RoleEntity> entities) {
    Map<UUID, Set<String>> permissionsByRole = new HashMap<>();
    for (RolePermissionEntity grant :
        grants.findByKeyRoleIdIn(entities.stream().map(RoleEntity::id).toList())) {
      permissionsByRole
          .computeIfAbsent(grant.key().roleId(), id -> new HashSet<>())
          .add(grant.key().permissionCode());
    }
    return entities.stream()
        .map(role -> role.toDomain(permissionsByRole.getOrDefault(role.id(), Set.of())))
        .toList();
  }
}
