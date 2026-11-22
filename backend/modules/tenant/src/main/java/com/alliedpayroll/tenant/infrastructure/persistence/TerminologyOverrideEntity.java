package com.alliedpayroll.tenant.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.TenantId;

/** Row of {@code tenant.terminology_override}: one label the tenant chose, in one locale. */
@Entity
@Table(schema = "tenant", name = "terminology_override")
class TerminologyOverrideEntity {

  @Id private UUID id;

  @TenantId
  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "concept_key", nullable = false, length = 80)
  private String conceptKey;

  @Column(nullable = false, length = 16)
  private String locale;

  @Column(nullable = false, length = 80)
  private String label;

  @Version private long version;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_by", nullable = false)
  private UUID updatedBy;

  protected TerminologyOverrideEntity() {
    // JPA
  }

  TerminologyOverrideEntity(
      UUID id, String conceptKey, String locale, String label, Instant now, UUID actor) {
    this.id = id;
    this.conceptKey = conceptKey;
    this.locale = locale;
    this.label = label;
    this.createdAt = now;
    this.createdBy = actor;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  void relabel(String value, Instant now, UUID actor) {
    this.label = value;
    this.updatedAt = now;
    this.updatedBy = actor;
  }

  String conceptKey() {
    return conceptKey;
  }

  String label() {
    return label;
  }
}
