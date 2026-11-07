package com.alliedpayroll.tenant.domain;

import com.alliedpayroll.platform.core.ids.TenantId;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** A customer organisation on the platform. Platform-owned: it carries no tenant id of its own. */
public record Tenant(
    TenantId id,
    String slug,
    String name,
    String industryProfileCode,
    TenantStatus status,
    String defaultLocale,
    List<String> enabledLocales,
    String timezone,
    @Nullable String keycloakOrganizationId) {

  public boolean isActive() {
    return status == TenantStatus.ACTIVE;
  }
}
