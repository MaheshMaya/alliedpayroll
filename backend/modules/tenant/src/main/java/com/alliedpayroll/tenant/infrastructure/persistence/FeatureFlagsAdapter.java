package com.alliedpayroll.tenant.infrastructure.persistence;

import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.tenant.application.FeatureFlags;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
class FeatureFlagsAdapter implements FeatureFlags {

  private final FeatureFlagJpaRepository jpa;
  private final IdGenerator ids;
  private final Clock clock;

  FeatureFlagsAdapter(FeatureFlagJpaRepository jpa, IdGenerator ids, Clock clock) {
    this.jpa = jpa;
    this.ids = ids;
    this.clock = clock;
  }

  @Override
  public Map<String, Boolean> enabledFor(TenantId tenantId) {
    Map<String, Boolean> flags = new LinkedHashMap<>();
    for (FeatureFlagEntity flag : jpa.findAllByTenantId(tenantId.value())) {
      flags.put(flag.flagKey(), flag.enabled());
    }
    return Map.copyOf(flags);
  }

  @Override
  public void addMissing(Map<String, Boolean> flags, UserId actor) {
    Set<String> present =
        jpa.findAllByFlagKeyIn(flags.keySet()).stream()
            .map(FeatureFlagEntity::flagKey)
            .collect(Collectors.toSet());
    Instant now = clock.instant();
    flags.forEach((key, enabled) -> {
      if (!present.contains(key)) {
        jpa.save(new FeatureFlagEntity(ids.next(), key, enabled, now, actor.value()));
      }
    });
  }

  @Override
  public FlagChange set(String flagKey, boolean enabled, UserId actor) {
    Instant now = clock.instant();
    return jpa.findByFlagKey(flagKey)
        .map(flag -> {
          boolean before = flag.enabled();
          flag.enable(enabled, now, actor.value());
          return new FlagChange(flag.id(), before);
        })
        .orElseGet(() -> {
          FeatureFlagEntity created =
              jpa.save(new FeatureFlagEntity(ids.next(), flagKey, enabled, now, actor.value()));
          return new FlagChange(created.id(), null);
        });
  }
}
