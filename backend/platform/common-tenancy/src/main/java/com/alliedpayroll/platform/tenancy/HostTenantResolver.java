package com.alliedpayroll.platform.tenancy;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Resolves the tenant from the first host label: {@code sentinel.app.example} and {@code
 * sentinel.localhost} both map to the slug {@code sentinel}. A bare host or an unknown slug
 * resolves to no tenant, which is the platform context.
 */
public final class HostTenantResolver {

  private static final Pattern SLUG = Pattern.compile("^[a-z0-9](?:[a-z0-9-]{1,38}[a-z0-9])?$");

  private final TenantDirectory directory;

  public HostTenantResolver(TenantDirectory directory) {
    this.directory = directory;
  }

  public Optional<CurrentTenant> resolve(HttpServletRequest request) {
    return slugOf(request.getServerName()).flatMap(directory::findActiveBySlug);
  }

  static Optional<String> slugOf(String host) {
    String lower = host.toLowerCase(Locale.ROOT);
    int dot = lower.indexOf('.');
    if (dot <= 0 || dot == lower.length() - 1) {
      return Optional.empty();
    }
    String first = lower.substring(0, dot);
    return SLUG.matcher(first).matches() ? Optional.of(first) : Optional.empty();
  }
}
