package com.alliedpayroll.identity.web;

import com.alliedpayroll.identity.application.UserQueryService;
import com.alliedpayroll.identity.domain.IdentityPermissions;
import com.alliedpayroll.identity.domain.UserSearch;
import com.alliedpayroll.identity.domain.UserStatus;
import com.alliedpayroll.identity.web.UserViews.UserDetail;
import com.alliedpayroll.identity.web.UserViews.UserSummary;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  UsersController(UserQueryService users) {
    this.users = users;
  }

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
  UserDetail get(@PathVariable UUID id) {
    return UserViews.detail(users.get(new UserId(id)));
  }
}
