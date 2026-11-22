package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.RolePermissionsChanged;
import com.alliedpayroll.identity.domain.PermissionStore;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.error.ConflictException;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.error.RuleViolationException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.security.PermissionDefinition;
import com.alliedpayroll.platform.tenancy.TenantContext;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The roles a tenant defines for itself. System roles are read-only. */
@Service
public class RoleAdministrationUseCase {

  private final RoleRepository roles;
  private final PermissionStore catalogue;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  RoleAdministrationUseCase(
      RoleRepository roles,
      PermissionStore catalogue,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.roles = roles;
    this.catalogue = catalogue;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  @Transactional
  public Role create(
      String code,
      String name,
      @Nullable String description,
      Set<String> permissions,
      Actor actor) {
    requireKnown(permissions);
    if (roles.findByCode(code).isPresent()) {
      throw new ConflictException("identity.role_exists", Map.of("code", code));
    }
    Role role =
        roles.create(
            Role.custom(ids.next(), code, name.trim(), description, permissions), actor.userId());
    published(role);
    Map<String, Object> after = new HashMap<>();
    after.put("code", role.code());
    after.put("name", role.name());
    after.put("permissions", sorted(role.permissions()));
    record(AuditAction.CREATE, role, List.of("code", "name", "permissions"), null, after, actor);
    return role;
  }

  @Transactional
  public Role rename(UUID roleId, String name, @Nullable String description, Actor actor) {
    Role before = load(roleId);
    Role after = roles.update(before.renamed(name.trim(), description), actor.userId());
    record(
        AuditAction.UPDATE,
        after,
        List.of("name"),
        Map.of("name", before.name()),
        Map.of("name", after.name()),
        actor);
    return after;
  }

  @Transactional
  public Role replacePermissions(UUID roleId, Set<String> permissions, Actor actor) {
    requireKnown(permissions);
    Role before = load(roleId);
    Role after = roles.update(before.granting(permissions), actor.userId());
    published(after);
    record(
        AuditAction.UPDATE,
        after,
        List.of("permissions"),
        Map.of("permissions", sorted(before.permissions())),
        Map.of("permissions", sorted(after.permissions())),
        actor);
    return after;
  }

  private Role load(UUID roleId) {
    return roles.findById(roleId).orElseThrow(() -> NotFoundException.of("role", roleId));
  }

  private void requireKnown(Set<String> permissions) {
    Set<String> known =
        catalogue.findAll().stream().map(PermissionDefinition::code).collect(Collectors.toSet());
    Set<String> unknown = new TreeSet<>(permissions);
    unknown.removeAll(known);
    if (!unknown.isEmpty()) {
      throw new RuleViolationException(
          "identity.permission_unknown", Map.of("codes", String.join(", ", unknown)));
    }
  }

  private void published(Role role) {
    events.publishEvent(
        new RolePermissionsChanged(
            ids.next(), TenantContext.requireId(), role.id(), clock.instant()));
  }

  private void record(
      AuditAction action,
      Role role,
      List<String> fields,
      @Nullable Map<String, Object> before,
      Map<String, Object> after,
      Actor actor) {
    audit.record(
        AuditEntry.change(
            action,
            actor.audit(),
            actor.channel(),
            UserAudit.ROLE_ENTITY_TYPE,
            role.id(),
            role.code(),
            fields,
            before,
            after,
            null));
  }

  private static List<String> sorted(Set<String> codes) {
    return codes.stream().sorted().toList();
  }
}
