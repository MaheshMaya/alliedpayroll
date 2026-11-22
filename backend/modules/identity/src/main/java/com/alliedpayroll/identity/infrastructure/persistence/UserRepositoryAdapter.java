package com.alliedpayroll.identity.infrastructure.persistence;

import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.UserPage;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.identity.domain.UserSearch;
import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.platform.core.ids.UserId;
import jakarta.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class UserRepositoryAdapter implements UserRepository {

  private final AppUserJpaRepository users;
  private final UserRoleJpaRepository userRoles;
  private final Clock clock;

  UserRepositoryAdapter(AppUserJpaRepository users, UserRoleJpaRepository userRoles, Clock clock) {
    this.users = users;
    this.userRoles = userRoles;
    this.clock = clock;
  }

  @Override
  public Optional<AppUser> findById(UserId id) {
    return users.findByIdAndDeletedAtIsNull(id.value()).map(AppUserEntity::toDomain);
  }

  @Override
  public Optional<AppUser> findBySubject(String keycloakSubject) {
    return users
        .findByKeycloakSubjectAndDeletedAtIsNull(keycloakSubject)
        .map(AppUserEntity::toDomain);
  }

  @Override
  public Optional<AppUser> findByUsername(String username) {
    return users.findByUsernameAndDeletedAtIsNull(username).map(AppUserEntity::toDomain);
  }

  @Override
  public UserPage search(UserSearch search, int page, int size) {
    Specification<AppUserEntity> spec =
        (root, query, cb) -> {
          List<Predicate> where = new ArrayList<>();
          where.add(cb.isNull(root.get("deletedAt")));
          if (search.status() != null) {
            where.add(cb.equal(root.get("status"), search.status()));
          }
          if (search.term() != null) {
            String pattern = "%" + search.term() + "%";
            where.add(
                cb.or(
                    cb.like(cb.lower(root.get("displayName")), pattern),
                    cb.like(cb.lower(root.get("username")), pattern)));
          }
          return cb.and(where.toArray(Predicate[]::new));
        };
    Page<AppUserEntity> found =
        users.findAll(spec, PageRequest.of(page, size, Sort.by("displayName", "username")));
    return new UserPage(
        found.getContent().stream().map(AppUserEntity::toDomain).toList(),
        found.getTotalElements());
  }

  @Override
  @Transactional
  public AppUser save(AppUser user) {
    Instant now = clock.instant();
    UUID actor = SystemActor.ID.value();
    AppUserEntity entity =
        users
            .findById(user.id().value())
            .map(existing -> {
              existing.apply(user, now, actor);
              return existing;
            })
            .orElseGet(() -> users.save(new AppUserEntity(user, now, actor)));
    return entity.toDomain();
  }

  @Override
  public Map<UserId, Set<UUID>> roleIdsOf(Collection<UserId> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    Map<UserId, Set<UUID>> out = new HashMap<>();
    for (UserRoleEntity assignment :
        userRoles.findByKeyUserIdIn(userIds.stream().map(UserId::value).toList())) {
      out.computeIfAbsent(new UserId(assignment.key().userId()), id -> new java.util.HashSet<>())
          .add(assignment.key().roleId());
    }
    return out;
  }

  @Override
  public Map<UUID, String> displayNames(Collection<UUID> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    Map<UUID, String> names = new HashMap<>();
    for (AppUserEntity user : users.findByIdIn(userIds)) {
      names.put(user.id(), user.displayName());
    }
    return names;
  }

  @Override
  @Transactional
  public void recordLogin(UserId userId, Instant at, String channel) {
    users.findById(userId.value()).ifPresent(user -> user.recordLogin(at, channel));
  }

  @Override
  @Transactional
  public void assignRole(UserId userId, UUID roleId, UserId actor) {
    UserRoleEntity.Key key = new UserRoleEntity.Key(userId.value(), roleId);
    if (!userRoles.existsById(key)) {
      userRoles.save(new UserRoleEntity(userId.value(), roleId, clock.instant(), actor.value()));
    }
  }
}
