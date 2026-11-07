package com.alliedpayroll.tenant.api;

import java.util.List;
import java.util.UUID;

/** What any client needs to know about the tenant it signed in to. */
public record TenantSummary(
    UUID id,
    String slug,
    String name,
    String industryProfileCode,
    String defaultLocale,
    List<String> enabledLocales,
    String timezone) {}
