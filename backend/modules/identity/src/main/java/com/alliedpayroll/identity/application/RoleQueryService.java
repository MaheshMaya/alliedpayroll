package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.domain.PermissionStore;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleQueryService {

  private final RoleRepository roles;
  private final PermissionStore permissions;

  RoleQueryService(RoleRepository roles, PermissionStore permissions) {
    this.roles = roles;
    this.permissions = permissions;
  }

  public List<Role> list() {
    return roles.findAll();
  }

  public Role get(UUID id) {
    return roles.findById(id).orElseThrow(() -> NotFoundException.of("role", id));
  }

  public List<PermissionDefinition> catalogue() {
    return permissions.findAll();
  }
}
