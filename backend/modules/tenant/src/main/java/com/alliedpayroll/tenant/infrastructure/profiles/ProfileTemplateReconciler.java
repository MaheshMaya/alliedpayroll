package com.alliedpayroll.tenant.infrastructure.profiles;

import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.SystemActor;
import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.infrastructure.profiles.ClasspathProfileTemplates.LoadedTemplate;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Records every shipped pack in {@code tenant.industry_profile_template} as published, so the
 * database says which version each tenant was made from. A published version is immutable: a pack
 * whose content differs from the stored one stops startup, because tenants already made from it
 * would silently change.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ProfileTemplateReconciler implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(ProfileTemplateReconciler.class);

  private static final String INSERT =
      """
      INSERT INTO tenant.industry_profile_template
        (id, code, version, name, status, payload, published_at,
         created_at, created_by, updated_at, updated_by)
      VALUES (?, ?, ?, ?, 'PUBLISHED', ?::jsonb, ?, ?, ?, ?, ?)
      ON CONFLICT (code, version) DO NOTHING
      """;

  private static final String STORED =
      "SELECT payload::text FROM tenant.industry_profile_template WHERE code = ? AND version = ?";

  private final ClasspathProfileTemplates templates;
  private final JdbcTemplate jdbc;
  private final ObjectMapper json;
  private final IdGenerator ids;
  private final Clock clock;

  ProfileTemplateReconciler(
      ClasspathProfileTemplates templates,
      JdbcTemplate jdbc,
      ObjectMapper json,
      IdGenerator ids,
      Clock clock) {
    this.templates = templates;
    this.jdbc = jdbc;
    this.json = json;
    this.ids = ids;
    this.clock = clock;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    List<LoadedTemplate> loaded = templates.loaded();
    for (LoadedTemplate pack : loaded) {
      publish(pack);
    }
    log.info("industry profile templates reconciled: {}", loaded.size());
  }

  private void publish(LoadedTemplate pack) {
    ProfileTemplate template = pack.template();
    Timestamp now = Timestamp.from(clock.instant());
    UUID system = SystemActor.ID.value();
    jdbc.update(
        INSERT,
        ids.next(),
        template.code(),
        template.version(),
        template.name(),
        json.writeValueAsString(pack.payload()),
        now,
        now,
        system,
        now,
        system);
    String stored =
        Objects.requireNonNull(
            jdbc.queryForObject(STORED, String.class, template.code(), template.version()));
    JsonNode storedPayload = json.readTree(stored);
    if (!storedPayload.equals(pack.payload())) {
      throw new IllegalStateException(
          "profile "
              + template.code()
              + " v"
              + template.version()
              + " differs from the published version; ship the change as a new version");
    }
  }
}
