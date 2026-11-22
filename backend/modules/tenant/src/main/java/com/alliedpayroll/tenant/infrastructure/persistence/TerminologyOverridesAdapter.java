package com.alliedpayroll.tenant.infrastructure.persistence;

import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.tenant.application.TerminologyOverrides;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
class TerminologyOverridesAdapter implements TerminologyOverrides {

  private final TerminologyOverrideJpaRepository jpa;
  private final IdGenerator ids;
  private final Clock clock;

  TerminologyOverridesAdapter(TerminologyOverrideJpaRepository jpa, IdGenerator ids, Clock clock) {
    this.jpa = jpa;
    this.ids = ids;
    this.clock = clock;
  }

  @Override
  public Map<String, String> forLocale(String locale) {
    return jpa.findAllByLocale(locale).stream()
        .collect(
            Collectors.toUnmodifiableMap(
                TerminologyOverrideEntity::conceptKey, TerminologyOverrideEntity::label));
  }

  @Override
  public void put(String locale, String key, String label, UserId actor) {
    Instant now = clock.instant();
    jpa.findByLocaleAndConceptKey(locale, key)
        .ifPresentOrElse(
            row -> row.relabel(label, now, actor.value()),
            () ->
                jpa.save(
                    new TerminologyOverrideEntity(
                        ids.next(), key, locale, label, now, actor.value())));
  }

  @Override
  public void remove(String locale, String key) {
    jpa.findByLocaleAndConceptKey(locale, key).ifPresent(jpa::delete);
  }
}
