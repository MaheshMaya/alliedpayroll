package com.alliedpayroll.identity.infrastructure.keycloak;

import java.time.Duration;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * How the backend reaches the Keycloak Admin API, as the confidential client {@code
 * alliedpayroll-backend} with a service account.
 *
 * @param baseUrl Keycloak root, without {@code /realms}; unset leaves the directory unavailable
 * @param initialPassword development only: invited accounts get this password instead of an email,
 *     so a local stack without mail can sign them in
 */
@ConfigurationProperties("alliedpayroll.identity.keycloak")
public record KeycloakProperties(
    @Nullable String baseUrl,
    @DefaultValue("alliedpayroll") String realm,
    @DefaultValue("alliedpayroll-backend") String clientId,
    @Nullable String clientSecret,
    @Nullable String initialPassword,
    @DefaultValue("5s") Duration timeout) {

  boolean configured() {
    return baseUrl != null && !baseUrl.isBlank() && clientSecret != null && !clientSecret.isBlank();
  }
}
