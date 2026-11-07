package com.alliedpayroll.platform.security;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/** Turns whatever Spring Security authenticated into the one principal type modules use. */
@Component
public class CurrentUserProvider {

  private final SecurityProperties properties;

  public CurrentUserProvider(SecurityProperties properties) {
    this.properties = properties;
  }

  public Optional<AuthenticatedUser> current() {
    return from(SecurityContextHolder.getContext().getAuthentication());
  }

  public AuthenticatedUser require() {
    return current().orElseThrow(() -> new IllegalStateException("no authenticated user"));
  }

  public Optional<AuthenticatedUser> from(@Nullable Authentication authentication) {
    return switch (authentication) {
      case JwtAuthenticationToken token -> Optional.of(fromJwt(token.getToken()));
      case OAuth2AuthenticationToken token when token.getPrincipal() instanceof OidcUser user ->
          Optional.of(fromSession(user));
      case null, default -> Optional.empty();
    };
  }

  private AuthenticatedUser fromJwt(Jwt jwt) {
    Map<String, Object> claims = jwt.getClaims();
    return new AuthenticatedUser(
        requireSubject(jwt.getSubject()),
        stringClaim(claims, "name"),
        stringClaim(claims, "email"),
        OrganizationClaims.aliases(claims.get(OrganizationClaims.CLAIM)),
        Set.of(),
        channelOf(stringClaim(claims, "azp")));
  }

  private AuthenticatedUser fromSession(OidcUser user) {
    Map<String, Object> claims = user.getClaims();
    return new AuthenticatedUser(
        requireSubject(user.getSubject()),
        user.getFullName(),
        user.getEmail(),
        OrganizationClaims.aliases(claims.get(OrganizationClaims.CLAIM)),
        Set.of(),
        Channel.WEB);
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
