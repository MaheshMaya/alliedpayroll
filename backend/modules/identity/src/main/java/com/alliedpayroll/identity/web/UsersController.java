package com.alliedpayroll.identity.web;

import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.identity.application.ChangeUserRolesUseCase;
import com.alliedpayroll.identity.application.InviteUserUseCase;
import com.alliedpayroll.identity.application.InviteUserUseCase.InviteUser;
import com.alliedpayroll.identity.application.UserAccessUseCase;
import com.alliedpayroll.identity.application.UserQueryService;
import com.alliedpayroll.identity.domain.IdentityPermissions;
import com.alliedpayroll.identity.domain.UserSearch;
import com.alliedpayroll.identity.domain.UserStatus;
import com.alliedpayroll.identity.web.UserViews.UserDetail;
import com.alliedpayroll.identity.web.UserViews.UserSummary;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.platform.web.EntityTags;
import com.alliedpayroll.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiPaths.API_V1 + "/users")
@Tag(name = "Users", description = "The people and service identities of the tenant")
@RequiresPermission(IdentityPermissions.USERS_READ)
class UsersController {

  private final UserQueryService users;
  private final InviteUserUseCase invitations;
  private final ChangeUserRolesUseCase roleChanges;
  private final UserAccessUseCase access;
  private final CurrentActor actor;

  UsersController(
      UserQueryService users,
      InviteUserUseCase invitations,
      ChangeUserRolesUseCase roleChanges,
      UserAccessUseCase access,
      CurrentActor actor) {
    this.users = users;
    this.invitations = invitations;
    this.roleChanges = roleChanges;
    this.access = access;
    this.actor = actor;
  }

  record InviteUserRequest(
      @NotBlank @Size(max = 150) String displayName,
      @NotBlank @Email @Size(max = 80) String email,
      @NotNull UserType userType,
      @NotEmpty Set<UUID> roleIds) {}

  record ReplaceUserRolesRequest(@NotNull Set<UUID> roleIds) {}

  @GetMapping
  @Operation(operationId = "listUsers", summary = "Users of the tenant, with their roles")
  PageResponse<UserSummary> list(
      @Parameter(description = "Matches display name or username") @RequestParam(required = false)
          @Nullable String q,
      @Parameter(name = "filter[status]") @RequestParam(name = "filter[status]", required = false)
          @Nullable UserStatus status,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size) {
    UserQueryService.Listing listing = users.list(UserSearch.of(q, status), page, size);
    String next = (long) (page + 1) * size < listing.total() ? String.valueOf(page + 1) : null;
    return new PageResponse<>(
        listing.items().stream().map(UserViews::summary).toList(), next, listing.total());
  }

  @GetMapping("/{id}")
  @Operation(operationId = "getUser", summary = "One user with roles and effective permissions")
  ResponseEntity<UserDetail> get(@PathVariable UUID id) {
    return withEntityTag(ResponseEntity.ok(), new UserId(id));
  }

  @PostMapping
  @RequiresPermission(IdentityPermissions.USERS_MANAGE)
  @Operation(
      operationId = "inviteUser",
      summary = "Invite a person; they activate on first sign-in")
  ResponseEntity<UserDetail> invite(@Valid @RequestBody InviteUserRequest request) {
    UserId id =
        invitations.invite(
            new InviteUser(
                request.displayName(), request.email(), request.userType(), request.roleIds()),
            actor.require());
    return withEntityTag(
        ResponseEntity.created(URI.create(ApiPaths.API_V1 + "/users/" + id.value())), id);
  }

  @PutMapping("/{id}/roles")
  @RequiresPermission(IdentityPermissions.USERS_MANAGE)
  @Operation(
      operationId = "replaceUserRoles",
      summary = "Make these the user's roles; applies from their next request")
  ResponseEntity<UserDetail> replaceRoles(
      @PathVariable UUID id,
      @Parameter(description = "The user's ETag, to refuse a change based on a stale read")
          @RequestHeader(name = HttpHeaders.IF_MATCH, required = false)
          @Nullable String ifMatch,
      @Valid @RequestBody ReplaceUserRolesRequest request) {
    UserId userId = new UserId(id);
    roleChanges.replace(
        userId, request.roleIds(), EntityTags.version(ifMatch, "user", id), actor.require());
    return withEntityTag(ResponseEntity.ok(), userId);
  }

  @PostMapping("/{id}/disable")
  @RequiresPermission(IdentityPermissions.USERS_MANAGE)
  @Operation(
      operationId = "disableUser",
      summary = "Sign the user out everywhere and refuse their access")
  ResponseEntity<UserDetail> disable(@PathVariable UUID id) {
    access.disable(new UserId(id), actor.require());
    return withEntityTag(ResponseEntity.ok(), new UserId(id));
  }

  @PostMapping("/{id}/enable")
  @RequiresPermission(IdentityPermissions.USERS_MANAGE)
  @Operation(operationId = "enableUser", summary = "Let a disabled user sign in again")
  ResponseEntity<UserDetail> enable(@PathVariable UUID id) {
    access.enable(new UserId(id), actor.require());
    return withEntityTag(ResponseEntity.ok(), new UserId(id));
  }

  private ResponseEntity<UserDetail> withEntityTag(ResponseEntity.BodyBuilder response, UserId id) {
    UserDetail detail = UserViews.detail(users.get(id));
    return response.eTag(EntityTags.of(detail.version())).body(detail);
  }
}
