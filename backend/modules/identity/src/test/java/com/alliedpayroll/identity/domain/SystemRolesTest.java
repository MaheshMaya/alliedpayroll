package com.alliedpayroll.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SystemRolesTest {

  @Test
  void everyRolePermissionExistsInTheCatalogue() {
    Set<String> catalogue =
        IdentityPermissions.ALL.stream()
            .map(PermissionDefinition::code)
            .collect(java.util.stream.Collectors.toSet());
    for (SystemRoles.SystemRole role : SystemRoles.all()) {
      assertThat(role.permissions()).as(role.code()).isSubsetOf(catalogue);
    }
  }

  @Test
  void roleCodesAreUnique() {
    List<String> codes = SystemRoles.all().stream().map(SystemRoles.SystemRole::code).toList();
    assertThat(codes)
        .doesNotHaveDuplicates()
        .contains(SystemRoles.TENANT_ADMIN, SystemRoles.EMPLOYEE);
  }

  @Test
  void theAdministratorCanReadAndManageUsers() {
    SystemRoles.SystemRole admin =
        SystemRoles.all().stream()
            .filter(role -> role.code().equals(SystemRoles.TENANT_ADMIN))
            .findFirst()
            .orElseThrow();
    assertThat(admin.permissions())
        .contains(IdentityPermissions.USERS_READ, IdentityPermissions.USERS_MANAGE);
  }
}
