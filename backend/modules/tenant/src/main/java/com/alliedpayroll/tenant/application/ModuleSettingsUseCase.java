package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.ModuleToggled;
import com.alliedpayroll.tenant.application.FeatureFlags.FlagChange;
import com.alliedpayroll.tenant.domain.TenantModule;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The optional modules of the bound tenant and switching them. Data is never deleted. */
@Service
public class ModuleSettingsUseCase {

  static final String ENTITY_TYPE = "MODULE";

  public record ModuleState(TenantModule module, boolean enabled) {}

  private final FeatureFlags flags;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  ModuleSettingsUseCase(
      FeatureFlags flags,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.flags = flags;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  @Transactional(readOnly = true)
  public List<ModuleState> list() {
    Map<String, Boolean> current = flags.enabledFor(TenantContext.requireId());
    return Arrays.stream(TenantModule.values())
        .map(module -> new ModuleState(module, current.getOrDefault(module.flagKey(), false)))
        .toList();
  }

  @Transactional
  public ModuleState setEnabled(String code, boolean enabled, Actor actor) {
    TenantModule module =
        TenantModule.fromCode(code).orElseThrow(() -> NotFoundException.of("module", code));
    FlagChange change = flags.set(module.flagKey(), enabled, actor.userId());
    boolean before = Boolean.TRUE.equals(change.before());
    if (change.before() != null && before == enabled) {
      return new ModuleState(module, enabled);
    }
    TenantId tenantId = TenantContext.requireId();
    events.publishEvent(
        new ModuleToggled(ids.next(), tenantId, module.name(), enabled, clock.instant()));
    audit.record(
        AuditEntry.change(
            AuditAction.STATE_CHANGE,
            actor.audit(),
            actor.channel(),
            ENTITY_TYPE,
            change.id(),
            module.name(),
            List.of("enabled"),
            Map.of("enabled", before),
            Map.of("enabled", enabled),
            enabled ? "tenant.module_enabled" : "tenant.module_disabled"));
    return new ModuleState(module, enabled);
  }
}
