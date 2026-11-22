package com.alliedpayroll.identity.web;

import com.alliedpayroll.identity.application.RoleAdministrationUseCase;
import com.alliedpayroll.identity.application.RoleQueryService;
import com.alliedpayroll.identity.domain.IdentityPermissions;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.platform.security.PermissionDefinition;
import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V1)
@Tag(name = "Roles", description = "Roles of the tenant and the permission catalogue")
@RequiresPermission(IdentityPermissions.USERS_READ)
class RolesController {

  private final RoleQueryService roles;
  private final RoleAdministrationUseCase administration;
  private final CurrentActor actor;

  RolesController(
      RoleQueryService roles, RoleAdministrationUseCase administration, CurrentActor actor) {
    this.roles = roles;
    this.administration = administration;
    this.actor = actor;
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

  record CreateRoleRequest(
      @NotBlank @Pattern(regexp = "[A-Z][A-Z0-9_]{1,39}") String code,
      @NotBlank @Size(max = 80) String name,
      @Nullable @Size(max = 255) String description,
      @NotNull Set<String> permissions) {}

  record UpdateRoleRequest(
      @NotBlank @Size(max = 80) String name, @Nullable @Size(max = 255) String description) {}

  record ReplaceRolePermissionsRequest(@NotNull Set<String> permissions) {}

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

  @PostMapping("/roles")
  @RequiresPermission(IdentityPermissions.ROLES_MANAGE)
  @Operation(operationId = "createRole", summary = "A role the tenant defines for itself")
  ResponseEntity<RoleView> create(@Valid @RequestBody CreateRoleRequest request) {
    Role role =
        administration.create(
            request.code(),
            request.name(),
            request.description(),
            request.permissions(),
            actor.require());
    return ResponseEntity.created(URI.create(ApiPaths.API_V1 + "/roles/" + role.id()))
        .body(RoleView.of(role));
  }

  @PutMapping("/roles/{id}")
  @RequiresPermission(IdentityPermissions.ROLES_MANAGE)
  @Operation(operationId = "updateRole", summary = "Rename a tenant-defined role")
  RoleView update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
    return RoleView.of(
        administration.rename(id, request.name(), request.description(), actor.require()));
  }

  @PutMapping("/roles/{id}/permissions")
  @RequiresPermission(IdentityPermissions.ROLES_MANAGE)
  @Operation(
      operationId = "replaceRolePermissions",
      summary = "Make these the permissions of a tenant-defined role")
  RoleView replacePermissions(
      @PathVariable UUID id, @Valid @RequestBody ReplaceRolePermissionsRequest request) {
    return RoleView.of(
        administration.replacePermissions(id, request.permissions(), actor.require()));
  }

  @GetMapping("/permissions")
  @Operation(operationId = "listPermissions", summary = "The permission catalogue, by module")
  List<PermissionDefinition> permissions() {
    return roles.catalogue();
  }
}
