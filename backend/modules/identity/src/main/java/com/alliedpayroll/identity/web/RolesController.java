package com.alliedpayroll.identity.web;

import com.alliedpayroll.identity.application.RoleQueryService;
import com.alliedpayroll.identity.domain.IdentityPermissions;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.platform.security.PermissionDefinition;
import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V1)
@Tag(name = "Roles", description = "Roles of the tenant and the permission catalogue")
@RequiresPermission(IdentityPermissions.USERS_READ)
class RolesController {

  private final RoleQueryService roles;

  RolesController(RoleQueryService roles) {
    this.roles = roles;
  }

  record RoleView(
      UUID id,
      String code,
      String name,
      @Nullable String description,
      boolean system,
      Set<String> permissions) {
    static RoleView of(Role role) {
      return new RoleView(
          role.id(),
          role.code(),
          role.name(),
          role.description(),
          role.system(),
          role.permissions());
    }
  }

  @GetMapping("/roles")
  @Operation(operationId = "listRoles", summary = "Roles of the tenant with their permissions")
  List<RoleView> list() {
    return roles.list().stream().map(RoleView::of).toList();
  }

  @GetMapping("/roles/{id}")
  @Operation(operationId = "getRole", summary = "One role")
  RoleView get(@PathVariable UUID id) {
    return RoleView.of(roles.get(id));
  }

  @GetMapping("/permissions")
  @Operation(operationId = "listPermissions", summary = "The permission catalogue, by module")
  List<PermissionDefinition> permissions() {
    return roles.catalogue();
  }
}
