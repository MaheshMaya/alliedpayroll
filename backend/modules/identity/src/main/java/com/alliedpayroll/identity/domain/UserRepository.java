package com.alliedpayroll.identity.domain;

import com.alliedpayroll.platform.core.ids.UserId;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Port owned by the domain; the JPA adapter in infrastructure implements it for the bound tenant.
 */
public interface UserRepository {

  Optional<AppUser> findById(UserId id);

  Optional<AppUser> findBySubject(String keycloakSubject);

  Optional<AppUser> findByUsername(String username);

  UserPage search(UserSearch search, int page, int size);

  /** Saves as the system actor; seeds and sign-in linking. */
  AppUser save(AppUser user);

  /** Saves a change a person made, flushed so the returned version is the new one. */
  AppUser save(AppUser user, UserId actor);

  /** Makes the given roles the user's only roles and moves the user's version. */
  void replaceRoles(UserId userId, Set<UUID> roleIds, UserId actor);

  Map<UserId, Set<UUID>> roleIdsOf(Collection<UserId> userIds);

  /** Assigns the role unless the user already has it. */
  void assignRole(UserId userId, UUID roleId, UserId actor);

  Map<UUID, String> displayNames(Collection<UUID> userIds);

  void recordLogin(UserId userId, Instant at, String channel);
}
