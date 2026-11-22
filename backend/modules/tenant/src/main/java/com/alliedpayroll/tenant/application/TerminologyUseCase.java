package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.error.RuleViolationException;
import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.tenant.api.TerminologyChanged;
import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.domain.Tenant;
import com.alliedpayroll.tenant.domain.TenantRepository;
import com.alliedpayroll.tenant.domain.Terminology;
import com.alliedpayroll.tenant.domain.TerminologyConcepts;
import com.alliedpayroll.tenant.domain.TerminologyPack;
import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The bound tenant's words for the canonical concepts, and relabelling them. */
@Service
public class TerminologyUseCase {

  static final String ENTITY_TYPE = "TERMINOLOGY";

  private final TenantRepository tenants;
  private final ProfileTemplates templates;
  private final TerminologyOverrides overrides;
  private final IdGenerator ids;
  private final Clock clock;
  private final ApplicationEventPublisher events;
  private final AuditSink audit;

  TerminologyUseCase(
      TenantRepository tenants,
      ProfileTemplates templates,
      TerminologyOverrides overrides,
      IdGenerator ids,
      Clock clock,
      ApplicationEventPublisher events,
      AuditSink audit) {
    this.tenants = tenants;
    this.templates = templates;
    this.overrides = overrides;
    this.ids = ids;
    this.clock = clock;
    this.events = events;
    this.audit = audit;
  }

  /** The labels in a locale the platform ships words for; any other locale is refused. */
  @Transactional(readOnly = true)
  public Terminology forLocale(String locale) {
    requireSupported(locale);
    return build(locale);
  }

  /**
   * The labels in the locale closest to the one the client asked for: the exact tag, then its
   * language, then English.
   */
  @Transactional(readOnly = true)
  public Terminology closestTo(Locale requested) {
    return build(closestSupported(requested));
  }

  /**
   * Sets the tenant's labels for the given keys in one locale. A blank label, or one equal to what
   * the key shows without a label of the tenant's own, removes the tenant's label.
   */
  @Transactional
  public Terminology relabel(String locale, Map<String, @Nullable String> labels, Actor actor) {
    requireSupported(locale);
    Terminology before = build(locale);
    Map<String, Object> oldValues = new LinkedHashMap<>();
    Map<String, Object> newValues = new LinkedHashMap<>();
    for (Map.Entry<String, @Nullable String> entry : labels.entrySet()) {
      String key = entry.getKey();
      if (!TerminologyConcepts.isKnownKey(key)) {
        throw new RuleViolationException("tenant.terminology_key_unknown", Map.of("key", key));
      }
      String wanted = normalised(entry.getValue());
      String target = wanted == null ? before.defaultLabel(key) : wanted;
      if (target.equals(before.defaultLabel(key))) {
        if (before.isOverridden(key)) {
          overrides.remove(locale, key);
        }
      } else if (!target.equals(before.label(key))) {
        overrides.put(locale, key, target, actor.userId());
      }
      if (!target.equals(before.label(key))) {
        oldValues.put(key, before.label(key));
        newValues.put(key, target);
      }
    }
    if (!newValues.isEmpty()) {
      recordChange(locale, new ArrayList<>(newValues.keySet()), oldValues, newValues, actor);
    }
    return build(locale);
  }

  private void recordChange(
      String locale,
      List<String> keys,
      Map<String, Object> oldValues,
      Map<String, Object> newValues,
      Actor actor) {
    TenantId tenantId = TenantContext.requireId();
    events.publishEvent(
        new TerminologyChanged(ids.next(), tenantId, locale, keys, clock.instant()));
    audit.record(
        AuditEntry.change(
            AuditAction.UPDATE,
            actor.audit(),
            actor.channel(),
            ENTITY_TYPE,
            tenantId.value(),
            locale,
            keys,
            oldValues,
            newValues,
            "tenant.terminology_changed"));
  }

  private Terminology build(String locale) {
    Tenant tenant =
        tenants
            .findById(TenantContext.requireId())
            .orElseThrow(() -> NotFoundException.of("tenant", TenantContext.requireId()));
    TerminologyPack profile =
        templates
            .find(tenant.industryProfileCode(), tenant.profileTemplateVersion())
            .map(ProfileTemplate::terminology)
            .orElse(TerminologyPack.EMPTY);
    return new Terminology(
        locale, templates.baseTerminology(), profile, overrides.forLocale(locale));
  }

  private void requireSupported(String locale) {
    if (!templates.baseTerminology().locales().contains(locale)) {
      throw new RuleViolationException("tenant.locale_unsupported", Map.of("locale", locale));
    }
  }

  private String closestSupported(Locale requested) {
    var supported = templates.baseTerminology().locales();
    String tag = requested.toLanguageTag();
    if (supported.contains(tag)) {
      return tag;
    }
    return supported.stream()
        .filter(candidate ->
            Locale.forLanguageTag(candidate).getLanguage().equals(requested.getLanguage()))
        .sorted()
        .findFirst()
        .orElse(Terminology.FALLBACK_LOCALE);
  }

  private static @Nullable String normalised(@Nullable String label) {
    if (label == null || label.isBlank()) {
      return null;
    }
    String trimmed = label.strip();
    if (trimmed.length() > TerminologyConcepts.MAX_LABEL_LENGTH) {
      throw new RuleViolationException(
          "tenant.terminology_label_too_long", Map.of("max", TerminologyConcepts.MAX_LABEL_LENGTH));
    }
    return trimmed;
  }
}
