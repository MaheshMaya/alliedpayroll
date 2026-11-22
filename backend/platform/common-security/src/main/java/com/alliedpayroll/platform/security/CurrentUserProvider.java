package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.tenancy.TenantContext;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Turns whatever Spring Security authenticated into the one principal type modules use, enriched
 * with the tenant's user row and permissions when the person is a member of the bound tenant.
 */
@Component
public class CurrentUserProvider {

  private static final String REQUEST_ATTRIBUTE = AuthenticatedUser.class.getName();

  private final SecurityProperties properties;
  private final ObjectProvider<PrincipalEnricher> enricher;

  public CurrentUserProvider(
      SecurityProperties properties, ObjectProvider<PrincipalEnricher> enricher) {
    this.properties = properties;
    this.enricher = enricher;
  }

  public Optional<AuthenticatedUser> current() {
    return from(SecurityContextHolder.getContext().getAuthentication());
  }

  public AuthenticatedUser require() {
    return current().orElseThrow(() -> new IllegalStateException("no authenticated user"));
  }

  public Optional<AuthenticatedUser> from(@Nullable Authentication authentication) {
    Optional<IdentityClaims> claims =
        switch (authentication) {
          case JwtAuthenticationToken token -> Optional.of(fromJwt(token.getToken()));
          case OAuth2AuthenticationToken token when token.getPrincipal() instanceof OidcUser user ->
              Optional.of(fromSession(user));
          case null, default -> Optional.empty();
        };
    return claims.map(this::resolve);
  }

  // The authorization manager and every permission check ask for the principal within one request;
  // the tenant lookup happens once and the rest read the request attribute.
  private AuthenticatedUser resolve(IdentityClaims claims) {
    RequestAttributes request = RequestContextHolder.getRequestAttributes();
    if (request != null
        && request.getAttribute(REQUEST_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST)
            instanceof AuthenticatedUser cached
        && cached.subject().equals(claims.subject())) {
      return cached;
    }
    AuthenticatedUser user = enrich(claims);
    if (request != null) {
      request.setAttribute(REQUEST_ATTRIBUTE, user, RequestAttributes.SCOPE_REQUEST);
    }
    return user;
  }

  private AuthenticatedUser enrich(IdentityClaims claims) {
    AuthenticatedUser base = baseUser(claims);
    PrincipalEnricher identity = enricher.getIfAvailable();
    if (identity == null) {
      return base;
    }
    return TenantContext.current()
        .filter(tenant -> base.belongsTo(tenant.slug()))
        .map(tenant -> base.enrichedWith(identity.enrich(tenant, claims)))
        .orElse(base);
  }

  private IdentityClaims fromJwt(Jwt jwt) {
    Map<String, Object> claims = jwt.getClaims();
    return new IdentityClaims(
        requireSubject(jwt.getSubject()),
        stringClaim(claims, "preferred_username"),
        stringClaim(claims, "name"),
        stringClaim(claims, "email"),
        channelOf(stringClaim(claims, "azp")),
        OrganizationClaims.aliases(claims.get(OrganizationClaims.CLAIM)));
  }

  private IdentityClaims fromSession(OidcUser user) {
    return new IdentityClaims(
        requireSubject(user.getSubject()),
        user.getPreferredUsername(),
        user.getFullName(),
        user.getEmail(),
        Channel.WEB,
        OrganizationClaims.aliases(user.getClaims().get(OrganizationClaims.CLAIM)));
  }

  private static AuthenticatedUser baseUser(IdentityClaims claims) {
    return new AuthenticatedUser(
        claims.subject(),
        claims.username(),
        claims.displayName(),
        claims.email(),
        claims.organizations(),
        null,
        java.util.Set.of(),
        claims.channel());
  }

  private Channel channelOf(@Nullable String authorizedParty) {
    if (authorizedParty == null) {
      return Channel.PARTNER;
    }
    if (authorizedParty.equals(properties.mobileClientId())) {
      return Channel.MOBILE;
    }
    if (authorizedParty.equals(properties.kioskClientId())) {
      return Channel.KIOSK;
    }
    return Channel.PARTNER;
  }

  private static String requireSubject(@Nullable String subject) {
    if (subject == null || subject.isBlank()) {
      throw new IllegalStateException("token has no subject");
    }
    return subject;
  }

  private static @Nullable String stringClaim(Map<String, Object> claims, String name) {
    return claims.get(name) instanceof String s ? s : null;
  }
}
