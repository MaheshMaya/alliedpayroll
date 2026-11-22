package com.alliedpayroll.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alliedpayroll.platform.core.error.RuleViolationException;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RoleTest {

  private static final UUID ID = UUID.fromString("0190a000-0000-7000-8000-000000000001");

  @Test
  void tenantRoleChangesItsPermissions() {
    Role role = Role.custom(ID, "SITE_LEAD", "Site lead", null, Set.of("identity:users_read"));

    Role changed = role.granting(Set.of("audit:read"));

    assertThat(changed.permissions()).containsExactly("audit:read");
    assertThat(changed.system()).isFalse();
  }

  @Test
  void systemRoleRefusesNewPermissions() {
    Role admin = new Role(ID, "TENANT_ADMIN", "Administrator", null, true, Set.of());

    assertThatThrownBy(() -> admin.granting(Set.of("audit:read")))
        .isInstanceOf(RuleViolationException.class)
        .extracting("code")
        .isEqualTo("identity.system_role_locked");
  }

  @Test
  void systemRoleRefusesRename() {
    Role admin = new Role(ID, "TENANT_ADMIN", "Administrator", null, true, Set.of());

    assertThatThrownBy(() -> admin.renamed("Owner", null))
        .isInstanceOf(RuleViolationException.class);
  }

  @Test
  void codeMustBeCapitalsDigitsAndUnderscores() {
    assertThatThrownBy(() -> Role.custom(ID, "site lead", "Site lead", null, Set.of()))
        .isInstanceOf(RuleViolationException.class)
        .extracting("code")
        .isEqualTo("identity.role_code_invalid");
  }
}
