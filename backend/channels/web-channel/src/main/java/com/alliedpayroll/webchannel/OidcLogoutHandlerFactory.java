package com.alliedpayroll.webchannel;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Logging out of the app also ends the Keycloak session (RP-initiated logout), then lands on the
 * app root of the same host. Built from configuration so start-up never depends on discovery.
 */
@Component
@EnableConfigurationProperties(OidcLogoutHandlerFactory.OidcProperties.class)
class OidcLogoutHandlerFactory {

  private final OidcProperties properties;

  OidcLogoutHandlerFactory(OidcProperties properties) {
    this.properties = properties;
  }

  LogoutSuccessHandler oidcLogout() {
    return new SimpleUrlLogoutSuccessHandler() {
      @Override
      protected String determineTargetUrl(
          jakarta.servlet.http.HttpServletRequest request,
          jakarta.servlet.http.HttpServletResponse response,
          @Nullable Authentication authentication) {
        String home = request.getScheme() + "://" + request.getHeader("Host") + "/";
        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser user)) {
          return home;
        }
        return UriComponentsBuilder.fromUriString(properties.endSessionUri())
            .queryParam("id_token_hint", Objects.requireNonNull(user.getIdToken()).getTokenValue())
            .queryParam("post_logout_redirect_uri", home)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUriString();
      }
    };
  }

  /**
   * @param endSessionUri Keycloak's end-session endpoint for the realm
   */
  @ConfigurationProperties("alliedpayroll.oidc")
  record OidcProperties(String endSessionUri) {}
}
