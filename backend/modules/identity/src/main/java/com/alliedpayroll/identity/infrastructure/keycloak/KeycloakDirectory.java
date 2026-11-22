package com.alliedpayroll.identity.infrastructure.keycloak;

import com.alliedpayroll.identity.application.IdentityDirectory;
import com.alliedpayroll.platform.core.error.ConflictException;
import com.alliedpayroll.platform.core.error.DependencyUnavailableException;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * The Keycloak Admin API behind {@link IdentityDirectory}. Authenticates with client credentials
 * and keeps the access token until shortly before it expires. Every failure to reach Keycloak
 * becomes {@code identity.directory_unavailable}, which rolls the tenant change back.
 */
final class KeycloakDirectory implements IdentityDirectory {

  static final String UNAVAILABLE = "identity.directory_unavailable";

  private static final ParameterizedTypeReference<Map<String, Object>> OBJECT =
      new ParameterizedTypeReference<>() {};
  private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST =
      new ParameterizedTypeReference<>() {};
  private static final long TOKEN_MARGIN_SECONDS = 30;

  private final KeycloakProperties properties;
  private final RestClient rest;
  private final Clock clock;
  private final String admin;
  private @Nullable String token;
  private Instant tokenExpiresAt = Instant.EPOCH;

  KeycloakDirectory(KeycloakProperties properties, RestClient.Builder builder, Clock clock) {
    this.properties = properties;
    this.rest = builder.baseUrl(Objects.requireNonNull(properties.baseUrl())).build();
    this.clock = clock;
    this.admin = "/admin/realms/" + properties.realm();
  }

  @Override
  public void invite(String organizationAlias, NewAccount account) {
    call(() -> {
      String organizationId = organizationId(organizationAlias);
      Optional<String> existing = userId(account.username());
      String userId = existing.orElseGet(() -> create(account));
      List<String> memberOf = organizationsOf(userId);
      if (memberOf.stream().anyMatch(alias -> !alias.equals(organizationAlias))) {
        throw new ConflictException(
            "identity.username_taken", Map.of("username", account.username()));
      }
      if (!memberOf.contains(organizationAlias)) {
        rest.post()
            .uri(admin + "/organizations/{org}/members", organizationId)
            .headers(headers -> headers.setBearerAuth(token()))
            .contentType(MediaType.APPLICATION_JSON)
            .body("\"" + userId + "\"")
            .retrieve()
            .toBodilessEntity();
      }
      // Credentials only for an account created here; an existing person keeps theirs.
      if (existing.isEmpty()) {
        setUpCredentials(userId);
      }
    });
  }

  @Override
  public void disable(String username) {
    call(() -> {
      userId(username)
          .ifPresent(id -> {
            setEnabled(id, false);
            rest.post()
                .uri(admin + "/users/{id}/logout", id)
                .headers(headers -> headers.setBearerAuth(token()))
                .retrieve()
                .toBodilessEntity();
          });
    });
  }

  @Override
  public void enable(String username) {
    call(() -> {
      userId(username).ifPresent(id -> setEnabled(id, true));
    });
  }

  private String create(NewAccount account) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("username", account.username());
    body.put("email", account.email());
    body.put("firstName", account.firstName());
    if (!account.lastName().isEmpty()) {
      body.put("lastName", account.lastName());
    }
    body.put("enabled", true);
    // A development password stands in for the emailed link that proves the address.
    body.put("emailVerified", properties.initialPassword() != null);
    try {
      URI location =
          rest.post()
              .uri(admin + "/users")
              .headers(headers -> headers.setBearerAuth(token()))
              .contentType(MediaType.APPLICATION_JSON)
              .body(body)
              .retrieve()
              .toBodilessEntity()
              .getHeaders()
              .getLocation();
      if (location != null) {
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
      }
    } catch (HttpClientErrorException e) {
      // Created by a concurrent request between the lookup and here; use that account.
      if (e.getStatusCode() != HttpStatus.CONFLICT) {
        throw e;
      }
    }
    return userId(account.username())
        .orElseThrow(() -> new IllegalStateException("account not found after create"));
  }

  private void setUpCredentials(String userId) {
    String password = properties.initialPassword();
    if (password != null) {
      rest.put()
          .uri(admin + "/users/{id}/reset-password", userId)
          .headers(headers -> headers.setBearerAuth(token()))
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("type", "password", "value", password, "temporary", false))
          .retrieve()
          .toBodilessEntity();
      return;
    }
    rest.put()
        .uri(admin + "/users/{id}/execute-actions-email", userId)
        .headers(headers -> headers.setBearerAuth(token()))
        .contentType(MediaType.APPLICATION_JSON)
        .body(List.of("VERIFY_EMAIL", "UPDATE_PASSWORD"))
        .retrieve()
        .toBodilessEntity();
  }

  private void setEnabled(String userId, boolean enabled) {
    rest.put()
        .uri(admin + "/users/{id}", userId)
        .headers(headers -> headers.setBearerAuth(token()))
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("enabled", enabled))
        .retrieve()
        .toBodilessEntity();
  }

  private Optional<String> userId(String username) {
    List<Map<String, Object>> found =
        rest.get()
            .uri(admin + "/users?username={u}&exact=true", username)
            .headers(headers -> headers.setBearerAuth(token()))
            .retrieve()
            .body(LIST);
    return Optional.ofNullable(found).orElse(List.of()).stream()
        .map(user -> String.valueOf(user.get("id")))
        .findFirst();
  }

  private String organizationId(String alias) {
    List<Map<String, Object>> found =
        rest.get()
            .uri(admin + "/organizations?search={alias}", alias)
            .headers(headers -> headers.setBearerAuth(token()))
            .retrieve()
            .body(LIST);
    return Optional.ofNullable(found).orElse(List.of()).stream()
        .filter(org -> alias.equals(org.get("alias")))
        .map(org -> String.valueOf(org.get("id")))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("no Keycloak organisation " + alias));
  }

  private List<String> organizationsOf(String userId) {
    List<Map<String, Object>> found =
        rest.get()
            .uri(admin + "/organizations/members/{id}/organizations", userId)
            .headers(headers -> headers.setBearerAuth(token()))
            .retrieve()
            .body(LIST);
    return Optional.ofNullable(found).orElse(List.of()).stream()
        .map(org -> String.valueOf(org.get("alias")))
        .toList();
  }

  private synchronized String token() {
    Instant now = clock.instant();
    String current = token;
    if (current != null && now.isBefore(tokenExpiresAt)) {
      return current;
    }
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "client_credentials");
    form.add("client_id", properties.clientId());
    form.add("client_secret", Objects.requireNonNull(properties.clientSecret()));
    Map<String, Object> response =
        Objects.requireNonNull(
            rest.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(OBJECT));
    String issued = String.valueOf(response.get("access_token"));
    long lifetime = response.get("expires_in") instanceof Number n ? n.longValue() : 60;
    token = issued;
    tokenExpiresAt = now.plusSeconds(Math.max(0, lifetime - TOKEN_MARGIN_SECONDS));
    return issued;
  }

  private static void call(Runnable work) {
    try {
      work.run();
    } catch (RestClientException | IllegalStateException e) {
      throw new DependencyUnavailableException(UNAVAILABLE, e);
    }
  }
}
