package com.alliedpayroll.identity.domain;

import com.alliedpayroll.platform.core.ids.UserId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

  List<Role> findAll();

  Optional<Role> findById(UUID id);

  Optional<Role> findByCode(String code);

  /** Creates a role as the system actor; the catalogue copy at provisioning. */
  Role save(Role role);

  Role create(Role role, UserId actor);

  /** Writes a changed name, description and permission set of an existing role. */
  Role update(Role role, UserId actor);

  /** Holds the tenant's role-seeding lock until the current transaction ends. */
  void lockSeeding();

  /** Adds permissions the role does not have yet. */
  void grant(UUID roleId, java.util.Set<String> permissions);
}
