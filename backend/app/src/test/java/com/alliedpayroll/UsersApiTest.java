package com.alliedpayroll;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.api.UserAdministration.EnsureUser;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.platform.test.PostgresTestDatabase;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import com.alliedpayroll.tenant.api.TenantSummary;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * The users resource through the API: the administrator sees the tenant's people with their roles,
 * an employee is refused, a member the tenant never provisioned has nothing, another tenant's
 * administrator sees none of it, and the first request of a provisioned person links them.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsersApiTest {

  // Filled by the seed before any test runs.
  private static @Nullable UserId sentinelAdmin;
  private static @Nullable UserId sentinelGuard;
  // Signed in by this class only, so its first sign-in is observed here.
  private static @Nullable UserId sentinelOps;

  @Autowired private MockMvc mvc;
  @MockitoBean private JwtDecoder jwtDecoder;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @BeforeAll
  static void seed(
      @Autowired TenantProvisioning provisioning, @Autowired UserAdministration users) {
    TenantSummary sentinel =
        provisioning.provision(new ProvisionTenant("sentinel", "Sentinel", "STAFFING", "sentinel"));
    TenantContext.runAs(
        tenant(sentinel),
        () -> {
          sentinelAdmin =
              users.ensureUser(
                  new EnsureUser(
                      "admin.sentinel",
                      "Asha Varma",
                      "admin@sentinel.example",
                      UserType.ADMIN,
                      Set.of("TENANT_ADMIN")));
          users.ensureUser(
              new EnsureUser(
                  "hr.sentinel",
                  "Meera Nair",
                  "hr@sentinel.example",
                  UserType.MANAGER,
                  Set.of("HR")));
          sentinelGuard =
              users.ensureUser(
                  new EnsureUser(
                      "guard.sentinel",
                      "Suresh Kumar",
                      "guard@sentinel.example",
                      UserType.EMPLOYEE,
                      Set.of("EMPLOYEE")));
          sentinelOps =
              users.ensureUser(
                  new EnsureUser(
                      "ops.sentinel",
                      "Nitya Rao",
                      "ops@sentinel.example",
                      UserType.EMPLOYEE,
                      Set.of("EMPLOYEE")));
        });
    TenantSummary nimbus =
        provisioning.provision(
            new ProvisionTenant("nimbus-it", "Nimbus IT", "CORPORATE", "nimbus-it"));
    TenantContext.runAs(
        tenant(nimbus),
        () ->
            users.ensureUser(
                new EnsureUser(
                    "admin.nimbus",
                    "Divya Menon",
                    "admin@nimbus-it.example",
                    UserType.ADMIN,
                    Set.of("TENANT_ADMIN"))));
  }

  @Test
  void administratorListsThePeopleOfTheTenantWithTheirRoles() throws Exception {
    mvc.perform(
            get("/api/v1/users")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(4))
        .andExpect(jsonPath("$.items", hasSize(4)))
        .andExpect(
            jsonPath("$.items[*].username")
                .value(
                    containsInAnyOrder(
                        "admin.sentinel", "hr.sentinel", "guard.sentinel", "ops.sentinel")))
        .andExpect(
            jsonPath("$.items[?(@.username=='admin.sentinel')].roles[0].code")
                .value(hasItem("TENANT_ADMIN")))
        .andExpect(
            jsonPath("$.items[?(@.username=='admin.sentinel')].emailMasked")
                .value(hasItem("a***@sentinel.example")))
        // Absent, not null: the SDKs type optional fields as missing.
        .andExpect(jsonPath("$.items[?(@.username=='hr.sentinel')].lastLoginAt").isEmpty());
  }

  @Test
  void theFirstRequestLinksTheKeycloakSubjectAndActivatesTheUser() throws Exception {
    String ops = Objects.requireNonNull(sentinelOps).value().toString();
    mvc.perform(
            get("/api/v1/users/" + ops)
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("INVITED"))
        .andExpect(jsonPath("$.linked").value(false));

    mvc.perform(
            get("/api/v1/bootstrap")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "ops.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.permissions", hasSize(0)));

    mvc.perform(
            get("/api/v1/users/" + ops)
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.linked").value(true))
        .andExpect(jsonPath("$.roles[0].code").value("EMPLOYEE"));
  }

  @Test
  void bootstrapCarriesTheEffectivePermissions() throws Exception {
    mvc.perform(
            get("/api/v1/bootstrap")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.permissions").value(hasItem("identity:users_read")))
        .andExpect(jsonPath("$.permissions").value(hasItem("identity:users_manage")));
  }

  @Test
  void employeeWithoutThePermissionIsRefusedWithTheProblemCode() throws Exception {
    mvc.perform(
            get("/api/v1/users")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "guard.sentinel")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("authz.denied"));
    mvc.perform(
            get("/api/v1/roles")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "guard.sentinel")))
        .andExpect(status().isForbidden());
  }

  @Test
  void memberTheTenantNeverProvisionedHasNoPermissions() throws Exception {
    mvc.perform(
            get("/api/v1/bootstrap")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "someone.new")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.permissions", hasSize(0)));
    mvc.perform(
            get("/api/v1/users")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "someone.new")))
        .andExpect(status().isForbidden());
  }

  @Test
  void anotherTenantsAdministratorSeesNothingOfThisTenant() throws Exception {
    mvc.perform(
            get("/api/v1/users")
                .header("Host", "sentinel.localhost")
                .with(signedIn("nimbus-it", "admin.nimbus")))
        .andExpect(status().isForbidden());

    mvc.perform(
            get("/api/v1/users")
                .header("Host", "nimbus-it.localhost")
                .with(signedIn("nimbus-it", "admin.nimbus")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.items[0].username").value("admin.nimbus"));

    mvc.perform(
            get("/api/v1/users/" + Objects.requireNonNull(sentinelAdmin).value())
                .header("Host", "nimbus-it.localhost")
                .with(signedIn("nimbus-it", "admin.nimbus")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("user.not_found"));
  }

  @Test
  void listFiltersBySearchTermAndStatus() throws Exception {
    mvc.perform(
            get("/api/v1/users")
                .param("q", "meera")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].username").value("hr.sentinel"));

    // Other test classes sign some of these people in, so the filter is checked by shape, not by
    // name.
    mvc.perform(
            get("/api/v1/users")
                .param("filter[status]", "ACTIVE")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[*].username").value(hasItem("admin.sentinel")))
        .andExpect(jsonPath("$.items[?(@.status!='ACTIVE')]").isEmpty());

    mvc.perform(
            get("/api/v1/users")
                .param("size", "2")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(2)))
        .andExpect(jsonPath("$.nextCursor").value("1"))
        .andExpect(jsonPath("$.totalCount").value(4));
  }

  @Test
  void rolesAndTheCatalogueAreListed() throws Exception {
    mvc.perform(
            get("/api/v1/roles")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(6)))
        .andExpect(
            jsonPath("$[?(@.code=='TENANT_ADMIN')].permissions[*]")
                .value(hasItem("identity:roles_manage")))
        .andExpect(jsonPath("$[?(@.code=='TENANT_ADMIN')].system").value(hasItem(true)));

    mvc.perform(
            get("/api/v1/permissions")
                .header("Host", "sentinel.localhost")
                .with(signedIn("sentinel", "admin.sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].code").value(hasItem("identity:users_read")))
        .andExpect(
            jsonPath("$[?(@.code=='identity:users_read')].moduleCode").value(hasItem("IDENTITY")));
  }

  private static CurrentTenant tenant(TenantSummary summary) {
    return new CurrentTenant(new TenantId(summary.id()), summary.slug());
  }

  /** A Keycloak token of a member of the organisation, as the mobile client would present it. */
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
