package com.alliedpayroll;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.api.UserAdministration.EnsureUser;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditActor;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.platform.test.PostgresTestDatabase;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import com.alliedpayroll.tenant.api.TenantSummary;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * The trail through the API: a denial is recorded with the actor, the administrator reads it, a
 * reader without the security permission sees no security rows, another tenant sees nothing.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditApiTest {

  private static @Nullable CurrentTenant sentinel;
  private static @Nullable UserId hr;

  @Autowired private MockMvc mvc;
  @Autowired private AuditSink audit;
  @Autowired private ApplicationEventPublisher events;
  @MockitoBean private JwtDecoder jwtDecoder;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @BeforeAll
  static void seed(
      @Autowired TenantProvisioning provisioning, @Autowired UserAdministration users) {
    TenantSummary summary =
        provisioning.provision(new ProvisionTenant("sentinel", "Sentinel", "STAFFING", "sentinel"));
    sentinel = new CurrentTenant(new TenantId(summary.id()), summary.slug());
    TenantContext.runAs(
        sentinel,
        () -> {
          users.ensureUser(
              new EnsureUser(
                  "admin.sentinel", "Asha Varma", null, UserType.ADMIN, Set.of("TENANT_ADMIN")));
          hr =
              users.ensureUser(
                  new EnsureUser(
                      "hr.sentinel", "Meera Nair", null, UserType.MANAGER, Set.of("HR")));
          users.ensureUser(
              new EnsureUser(
                  "guard.sentinel", "Suresh Kumar", null, UserType.EMPLOYEE, Set.of("EMPLOYEE")));
        });
    TenantSummary nimbus =
        provisioning.provision(
            new ProvisionTenant("nimbus-it", "Nimbus IT", "CORPORATE", "nimbus-it"));
    TenantContext.runAs(
        new CurrentTenant(new TenantId(nimbus.id()), nimbus.slug()),
        () ->
            users.ensureUser(
                new EnsureUser(
                    "admin.nimbus", "Divya Menon", null, UserType.ADMIN, Set.of("TENANT_ADMIN"))));
  }

  @Test
  void denialIsRecordedWithTheActorAndTheAdministratorReadsIt() throws Exception {
    mvc.perform(
            get("/api/v1/users")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "guard.sentinel")))
        .andExpect(status().isForbidden());

    mvc.perform(
            get("/api/v1/audit-events")
                .param("filter[action]", "AUTHZ_DENIED")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[*].actorDisplayName").value(hasItem("Suresh Kumar")))
        .andExpect(jsonPath("$.items[*].reasonKey").value(hasItem("authz.denied")))
        .andExpect(jsonPath("$.items[*].reasonParams.path").value(hasItem("/api/v1/users")))
        .andExpect(jsonPath("$.items[*].channel").value(hasItem("MOBILE")));
  }

  @Test
  void firstSignInLeavesStateChangeOnTheUser() throws Exception {
    mvc.perform(
            get("/api/v1/bootstrap")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "hr.sentinel")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/api/v1/audit-events")
                .param("filter[entityType]", "USER")
                .param("filter[entityId]", Objects.requireNonNull(hr).value().toString())
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].action").value("STATE_CHANGE"))
        .andExpect(jsonPath("$.items[0].entityLabel").value("hr.sentinel"))
        .andExpect(jsonPath("$.items[0].changedFields").value(hasItem("status")))
        .andExpect(jsonPath("$.items[0].newValues.status").value("ACTIVE"))
        .andExpect(jsonPath("$.items[0].reasonKey").value("identity.user_activated"));
  }

  @Test
  void administratorSeesChangesBesideSecurityRowsAndAnEmployeeIsRefused() throws Exception {
    mvc.perform(
            get("/api/v1/roles")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "guard.sentinel")))
        .andExpect(status().isForbidden());
    TenantContext.runAs(
        Objects.requireNonNull(sentinel),
        () ->
            audit.record(
                AuditEntry.change(
                    AuditAction.UPDATE,
                    AuditActor.SYSTEM,
                    "SYSTEM",
                    "EMPLOYEE",
                    UUID.randomUUID(),
                    "EMP-0001",
                    List.of("designation"),
                    Map.of("designation", "Guard"),
                    Map.of("designation", "Supervisor"),
                    null)));

    mvc.perform(
            get("/api/v1/audit-events")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[*].action").value(hasItem("UPDATE")))
        .andExpect(jsonPath("$.items[*].action").value(hasItem("AUTHZ_DENIED")));

    mvc.perform(
            get("/api/v1/audit-events")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "guard.sentinel")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("authz.denied"));
  }

  @Test
  void browserSignInIsRecordedAsLoginByTheSignedInPerson() throws Exception {
    // What Spring Security publishes when the OIDC callback succeeds, for a seeded person.
    OidcIdToken idToken =
        new OidcIdToken(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of(
                "sub", "kc-admin.sentinel",
                "preferred_username", "admin.sentinel",
                "organization", Map.of("sentinel", Map.of())));
    var session =
        new OAuth2AuthenticationToken(
            new DefaultOidcUser(List.of(), idToken), List.of(), "keycloak");
    TenantContext.runAs(
        Objects.requireNonNull(sentinel),
        () -> events.publishEvent(new AuthenticationSuccessEvent(session)));

    mvc.perform(
            get("/api/v1/audit-events")
                .param("filter[action]", "LOGIN")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].actorDisplayName").value("Asha Varma"))
        .andExpect(jsonPath("$.items[0].channel").value("WEB"));
  }

  @Test
  void anonymousRequestIsNotDenial() throws Exception {
    mvc.perform(
            get("/api/v1/roles/00000000-0000-0000-0000-00000000dead")
                .header("Host", "sentinel.localhost"))
        .andExpect(status().isUnauthorized());

    mvc.perform(
            get("/api/v1/audit-events")
                .param("filter[action]", "AUTHZ_DENIED")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.items[*].reasonParams.path")
                .value(not(hasItem("/api/v1/roles/00000000-0000-0000-0000-00000000dead"))));
  }

  @Test
  void pagesByCursorNewestFirst() throws Exception {
    String first =
        mvc.perform(
                get("/api/v1/audit-events")
                    .param("size", "1")
                    .header("Host", "sentinel.localhost")
                    .with(signedIn("sentinel", "admin.sentinel")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.nextCursor").isString())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String cursor = first.replaceAll(".*\"nextCursor\":\"([^\"]+)\".*", "$1");
    String firstId = first.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

    mvc.perform(
            get("/api/v1/audit-events")
                .param("size", "1")
                .param("cursor", cursor)
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].id").value(not(firstId)));
  }

  @Test
  void anotherTenantSeesNothing() throws Exception {
    mvc.perform(
            get("/api/v1/audit-events")
                .param("filter[action]", "AUTHZ_DENIED")
                .header("Host", "nimbus-it.localhost")
                .with(signedIn("nimbus-it", "admin.nimbus")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[*].actorDisplayName").value(not(hasItem("Suresh Kumar"))));
  }

  private static RequestPostProcessor signedIn(String organization, String username) {
    return jwt()
        .jwt(jwt ->
            jwt.subject("kc-" + username)
                .claim("preferred_username", username)
                .claim("organization", Map.of(organization, Map.of()))
                .claim("name", username)
                .claim("azp", "alliedpayroll-mobile"));
  }
}
