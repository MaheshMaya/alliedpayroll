package com.alliedpayroll.identity.infrastructure.keycloak;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.alliedpayroll.identity.application.IdentityDirectory.NewAccount;
import com.alliedpayroll.platform.core.error.ConflictException;
import com.alliedpayroll.platform.core.error.DependencyUnavailableException;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** The Admin API calls the directory makes, against a scripted Keycloak. */
class KeycloakDirectoryTest {

  private static final String ADMIN = "http://kc.test/admin/realms/alliedpayroll";
  private static final NewAccount NITYA =
      new NewAccount("nitya@orbit.example", "nitya@orbit.example", "Nitya", "Rao");

  private final RestClient.Builder builder = RestClient.builder();
  private final MockRestServiceServer keycloak = MockRestServiceServer.bindTo(builder).build();

  private KeycloakDirectory directory(@Nullable String initialPassword) {
    KeycloakProperties properties =
        new KeycloakProperties(
            "http://kc.test",
            "alliedpayroll",
            "alliedpayroll-backend",
            "secret",
            initialPassword,
            Duration.ofSeconds(5));
    return new KeycloakDirectory(
        properties, builder, Clock.fixed(Instant.parse("2026-11-22T09:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  void newPersonIsCreatedJoinedToTheOrganisationAndGivenThePassword() {
    final KeycloakDirectory directory = directory("Welcome@123");
    expectToken();
    expectOrganisation();
    expectGet(ADMIN + "/users?username=nitya%40orbit.example&exact=true", "[]");
    keycloak
        .expect(requestTo(ADMIN + "/users"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withCreatedEntity(URI.create(ADMIN + "/users/u-1")));
    expectGet(ADMIN + "/organizations/members/u-1/organizations", "[]");
    keycloak
        .expect(requestTo(ADMIN + "/organizations/org-1/members"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string("\"u-1\""))
        .andRespond(withCreatedEntity(URI.create(ADMIN + "/organizations/org-1/members/u-1")));
    keycloak
        .expect(requestTo(ADMIN + "/users/u-1/reset-password"))
        .andExpect(method(HttpMethod.PUT))
        .andRespond(withNoContent());

    directory.invite("orbit", NITYA);

    keycloak.verify();
  }

  @Test
  void personOfAnotherOrganisationIsRefusedAndKeepsTheirPassword() {
    final KeycloakDirectory directory = directory("Welcome@123");
    expectToken();
    expectOrganisation();
    expectGet(ADMIN + "/users?username=nitya%40orbit.example&exact=true", "[{\"id\":\"u-9\"}]");
    expectGet(ADMIN + "/organizations/members/u-9/organizations", "[{\"alias\":\"nimbus-it\"}]");

    assertThatThrownBy(() -> directory.invite("orbit", NITYA))
        .isInstanceOf(ConflictException.class)
        .extracting("code")
        .isEqualTo("identity.username_taken");
    keycloak.verify();
  }

  @Test
  void existingMemberIsLeftAsTheyAre() {
    final KeycloakDirectory directory = directory("Welcome@123");
    expectToken();
    expectOrganisation();
    expectGet(ADMIN + "/users?username=nitya%40orbit.example&exact=true", "[{\"id\":\"u-2\"}]");
    expectGet(ADMIN + "/organizations/members/u-2/organizations", "[{\"alias\":\"orbit\"}]");

    directory.invite("orbit", NITYA);

    keycloak.verify();
  }

  @Test
  void withoutDevelopmentPasswordThePersonIsEmailedTheSetUpLink() {
    final KeycloakDirectory directory = directory(null);
    expectToken();
    expectOrganisation();
    expectGet(ADMIN + "/users?username=nitya%40orbit.example&exact=true", "[]");
    keycloak
        .expect(requestTo(ADMIN + "/users"))
        .andRespond(withCreatedEntity(URI.create(ADMIN + "/users/u-1")));
    expectGet(ADMIN + "/organizations/members/u-1/organizations", "[]");
    keycloak.expect(requestTo(ADMIN + "/organizations/org-1/members")).andRespond(withNoContent());
    keycloak
        .expect(requestTo(ADMIN + "/users/u-1/execute-actions-email"))
        .andExpect(method(HttpMethod.PUT))
        .andExpect(content().json("[\"VERIFY_EMAIL\",\"UPDATE_PASSWORD\"]"))
        .andRespond(withNoContent());

    directory.invite("orbit", NITYA);

    keycloak.verify();
  }

  @Test
  void disablingSwitchesTheAccountOffAndEndsItsSessions() {
    final KeycloakDirectory directory = directory(null);
    expectToken();
    expectGet(ADMIN + "/users?username=nitya%40orbit.example&exact=true", "[{\"id\":\"u-3\"}]");
    keycloak
        .expect(requestTo(ADMIN + "/users/u-3"))
        .andExpect(method(HttpMethod.PUT))
        .andExpect(content().json("{\"enabled\":false}"))
        .andRespond(withNoContent());
    keycloak
        .expect(requestTo(ADMIN + "/users/u-3/logout"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withNoContent());

    directory.disable("nitya@orbit.example");

    keycloak.verify();
  }

  @Test
  void unreachableKeycloakIsReportedAsUnavailable() {
    final KeycloakDirectory directory = directory(null);
    keycloak
        .expect(requestTo(endsWith("/protocol/openid-connect/token")))
        .andRespond(withServerError());

    assertThatThrownBy(() -> directory.enable("nitya@orbit.example"))
        .isInstanceOf(DependencyUnavailableException.class)
        .extracting("code")
        .isEqualTo("identity.directory_unavailable");
  }

  private void expectToken() {
    keycloak
        .expect(requestTo("http://kc.test/realms/alliedpayroll/protocol/openid-connect/token"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().formDataContains(java.util.Map.of("grant_type", "client_credentials")))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"t-1\",\"expires_in\":300}", MediaType.APPLICATION_JSON));
  }

  private void expectOrganisation() {
    expectGet(
        ADMIN + "/organizations?search=orbit",
        "[{\"id\":\"org-2\",\"alias\":\"orbit-labs\"},{\"id\":\"org-1\",\"alias\":\"orbit\"}]");
  }

  private void expectGet(String url, String json) {
    keycloak
        .expect(requestTo(url))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
  }
}
