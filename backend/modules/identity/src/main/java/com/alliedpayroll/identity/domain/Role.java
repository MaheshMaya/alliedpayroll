package com.alliedpayroll.identity.domain;

import com.alliedpayroll.platform.core.error.RuleViolationException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * A named set of permissions in one tenant. System roles come from the catalogue and stay as is; a
 * tenant changes only the roles it created.
 */
public record Role(
    UUID id,
    String code,
    String name,
    @Nullable String description,
    boolean system,
    Set<String> permissions) {

  private static final Pattern CODE = Pattern.compile("[A-Z][A-Z0-9_]{1,39}");

  public Role {
    permissions = Set.copyOf(permissions);
  }

  /** A role the tenant defines for itself. */
  public static Role custom(
      UUID id, String code, String name, @Nullable String description, Set<String> permissions) {
    if (!CODE.matcher(code).matches()) {
      throw new RuleViolationException("identity.role_code_invalid", Map.of("code", code));
    }
    return new Role(id, code, name, description, false, permissions);
  }

  public Role renamed(String newName, @Nullable String newDescription) {
    requireEditable();
    return new Role(id, code, newName, newDescription, false, permissions);
  }

  public Role granting(Set<String> newPermissions) {
    requireEditable();
    return new Role(id, code, name, description, false, newPermissions);
  }

  private void requireEditable() {
    if (system) {
      throw new RuleViolationException("identity.system_role_locked", Map.of("code", code));
    }
  }
}
