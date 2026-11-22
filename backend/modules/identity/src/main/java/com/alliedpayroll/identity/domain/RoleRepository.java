package com.alliedpayroll.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

  List<Role> findAll();

  Optional<Role> findById(UUID id);

  Optional<Role> findByCode(String code);

  Role save(Role role);

  /** Holds the tenant's role-seeding lock until the current transaction ends. */
  void lockSeeding();
}
