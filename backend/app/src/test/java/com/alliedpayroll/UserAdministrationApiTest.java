package com.alliedpayroll;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.api.UserAdministration.EnsureUser;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.identity.application.IdentityDirectory;
import com.alliedpayroll.identity.application.IdentityDirectory.NewAccount;
import com.alliedpayroll.platform.core.error.DependencyUnavailableException;
import com.alliedpayroll.platform.core.ids.TenantId;
import com.alliedpayroll.platform.core.ids.UserId;
import com.alliedpayroll.platform.tenancy.CurrentTenant;
import com.alliedpayroll.platform.tenancy.TenantContext;
import com.alliedpayroll.platform.test.PostgresTestDatabase;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import com.alliedpayroll.tenant.api.TenantSummary;
import com.jayway.jsonpath.JsonPath;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Inviting, re-roling, disabling and the tenant's own roles, through the API. Runs in a tenant of
 * its own, so the people it adds never change what the other API tests count. Keycloak is replaced
 * by a mock of the directory port; the adapter has its own test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAdministrationApiTest {

  private static final String HOST = "orbit.localhost";

  private static @Nullable UserId orbitAdmin;

  @Autowired private MockMvc mvc;
  @MockitoBean private JwtDecoder jwtDecoder;
  @MockitoBean private IdentityDirectory directory;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @BeforeAll
  static void seed(
      @Autowired TenantProvisioning provisioning, @Autowired UserAdministration users) {
    DemoPeople.seed(provisioning, users);
    TenantSummary orbit =
        provisioning.provision(new ProvisionTenant("orbit", "Orbit Security", "STAFFING", "orbit"));
    TenantContext.runAs(
        new CurrentTenant(new TenantId(orbit.id()), orbit.slug()),
        () -> {
          orbitAdmin =
              users.ensureUser(
                  new EnsureUser(
                      "admin.orbit",
                      "Kavya Pillai",
                      "admin@orbit.example",
                      UserType.ADMIN,
                      Set.of("TENANT_ADMIN")));
          users.ensureUser(
              new EnsureUser(
                  "payroll.orbit",
                  "Arjun Das",
                  "payroll@orbit.example",
                  UserType.MANAGER,
                  Set.of("PAYROLL")));
        });
  }

  @Test
  void invitedPersonStartsInvitedWithTheChosenRolesAndIsAudited() throws Exception {
    String hr = roleId("HR");

    MvcResult invited =
        mvc.perform(
                as("admin.orbit", post("/api/v1/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invitation("Nitya Rao", "Nitya.Rao@orbit.example", hr)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("ETag"))
            .andExpect(jsonPath("$.username").value("nitya.rao@orbit.example"))
            .andExpect(jsonPath("$.emailMasked").value("n***@orbit.example"))
            .andExpect(jsonPath("$.status").value("INVITED"))
            .andExpect(jsonPath("$.roles[*].code").value(containsInAnyOrder("HR")))
            .andReturn();

    verify(directory)
        .invite(
            "orbit",
            new NewAccount("nitya.rao@orbit.example", "nitya.rao@orbit.example", "Nitya", "Rao"));
    mvc.perform(
            as("admin.orbit", get("/api/v1/audit-events").param("filter[entityId]", idOf(invited))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].action").value("CREATE"))
        .andExpect(jsonPath("$.items[0].reasonKey").value("identity.user_invited"))
        .andExpect(jsonPath("$.items[0].newValues.roles").value(hasItem("HR")));
  }

  @Test
  void invitingTheSameEmailAgainIsRefused() throws Exception {
    String employee = roleId("EMPLOYEE");
    invite("Ravi Shankar", "ravi@orbit.example", employee);

    mvc.perform(
            as("admin.orbit", post("/api/v1/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(invitation("Ravi Shankar", "ravi@orbit.example", employee)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("identity.user_exists"));
  }

  @Test
  void keycloakFailureLeavesNoUserBehind() throws Exception {
    doThrow(new DependencyUnavailableException("identity.directory_unavailable", new Exception()))
        .when(directory)
        .invite(eq("orbit"), any());

    mvc.perform(
            as("admin.orbit", post("/api/v1/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(invitation("Lost Person", "lost@orbit.example", roleId("EMPLOYEE"))))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.code").value("identity.directory_unavailable"));
    mvc.perform(as("admin.orbit", get("/api/v1/users")).param("q", "lost"))
        .andExpect(jsonPath("$.totalCount").value(0));
  }

  @Test
  void payrollUserCannotInvite() throws Exception {
    mvc.perform(
            as("payroll.orbit", post("/api/v1/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(invitation("Someone", "someone@orbit.example", roleId("EMPLOYEE"))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("authz.denied"));
    verify(directory, never()).invite(anyString(), any());
  }

  @Test
  void newRolesApplyFromTheNextRequest() throws Exception {
    String id = invite("Leela Menon", "leela@orbit.example", roleId("EMPLOYEE"));
    mvc.perform(as("leela@orbit.example", get("/api/v1/users"))).andExpect(status().isForbidden());

    mvc.perform(
            as("admin.orbit", put("/api/v1/users/{id}/roles", id))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleIds\":[\"" + roleId("HR") + "\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roles[*].code").value(containsInAnyOrder("HR")));

    mvc.perform(as("leela@orbit.example", get("/api/v1/users"))).andExpect(status().isOk());
  }

  @Test
  void roleChangeFromStaleReadIsRefused() throws Exception {
    String id = invite("Gopal Iyer", "gopal@orbit.example", roleId("EMPLOYEE"));
    String etag =
        mvc.perform(as("admin.orbit", get("/api/v1/users/{id}", id)))
            .andReturn()
            .getResponse()
            .getHeader("ETag");
    mvc.perform(
            as("admin.orbit", put("/api/v1/users/{id}/roles", id))
                .header("If-Match", Objects.requireNonNull(etag))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleIds\":[\"" + roleId("HR") + "\"]}"))
        .andExpect(status().isOk());

    mvc.perform(
            as("admin.orbit", put("/api/v1/users/{id}/roles", id))
                .header("If-Match", etag)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleIds\":[\"" + roleId("PAYROLL") + "\"]}"))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.code").value("request.precondition_failed"));
  }

  @Test
  void administratorCannotRemoveTheirOwnUserManagement() throws Exception {
    mvc.perform(
            as("admin.orbit", put("/api/v1/users/{id}/roles", adminId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleIds\":[\"" + roleId("HR") + "\"]}"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("identity.cannot_remove_own_access"));
  }

  @Test
  void disabledUserIsSignedOutAndRefusedUntilEnabled() throws Exception {
    String id = invite("Farah Khan", "farah@orbit.example", roleId("HR"));
    mvc.perform(as("farah@orbit.example", get("/api/v1/users"))).andExpect(status().isOk());

    mvc.perform(as("admin.orbit", post("/api/v1/users/{id}/disable", id)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DISABLED"));

    verify(directory).disable("farah@orbit.example");
    mvc.perform(as("farah@orbit.example", get("/api/v1/bootstrap")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("identity.user_disabled"));

    mvc.perform(as("admin.orbit", post("/api/v1/users/{id}/enable", id)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"));
    mvc.perform(as("farah@orbit.example", get("/api/v1/users"))).andExpect(status().isOk());
  }

  @Test
  void administratorCannotDisableThemselves() throws Exception {
    mvc.perform(as("admin.orbit", post("/api/v1/users/{id}/disable", adminId())))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("identity.cannot_disable_self"));
  }

  @Test
  void anotherTenantCannotChangeThisTenantsUsers() throws Exception {
    mvc.perform(
            put("/api/v1/users/{id}/roles", adminId())
                .header("Host", "nimbus-it.localhost")
                .with(signedIn("nimbus-it", "admin.nimbus"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleIds\":[]}"))
        .andExpect(status().isNotFound());
    mvc.perform(
            post("/api/v1/users/{id}/disable", adminId())
                .header("Host", "nimbus-it.localhost")
                .with(signedIn("nimbus-it", "admin.nimbus")))
        .andExpect(status().isNotFound());
  }

  @Test
  void tenantDefinesItsOwnRoleAndChangesWhatItAllows() throws Exception {
    MvcResult created =
        mvc.perform(
                as("admin.orbit", post("/api/v1/roles"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"code\":\"SITE_LEAD\",\"name\":\"Site lead\","
                            + "\"permissions\":[\"identity:users_read\"]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.system").value(false))
            .andReturn();

    mvc.perform(
            as("admin.orbit", put("/api/v1/roles/{id}/permissions", idOf(created)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"permissions\":[\"identity:users_read\",\"audit:read\"]}"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.permissions")
                .value(containsInAnyOrder("identity:users_read", "audit:read")));

    mvc.perform(
            as("admin.orbit", post("/api/v1/roles"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"SITE_LEAD\",\"name\":\"Again\",\"permissions\":[]}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("identity.role_exists"));
  }

  @Test
  void builtInRolesAndUnknownPermissionsAreRefused() throws Exception {
    mvc.perform(
            as("admin.orbit", put("/api/v1/roles/{id}/permissions", roleId("HR")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"permissions\":[]}"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("identity.system_role_locked"));

    mvc.perform(
            as("admin.orbit", post("/api/v1/roles"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"code\":\"GHOST\",\"name\":\"Ghost\",\"permissions\":[\"payroll:fly\"]}"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("identity.permission_unknown"));
  }

  @Test
  void payrollUserCannotDefineRoles() throws Exception {
    mvc.perform(
            as("payroll.orbit", post("/api/v1/roles"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"NOPE\",\"name\":\"Nope\",\"permissions\":[]}"))
        .andExpect(status().isForbidden());
  }

  private String invite(String name, String email, String roleId) throws Exception {
    return idOf(
        mvc.perform(
                as("admin.orbit", post("/api/v1/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invitation(name, email, roleId)))
            .andExpect(status().isCreated())
            .andReturn());
  }

  private String roleId(String code) throws Exception {
    String body =
        mvc.perform(as("admin.orbit", get("/api/v1/roles")))
            .andReturn()
            .getResponse()
            .getContentAsString();
    return JsonPath.<java.util.List<String>>read(body, "$[?(@.code=='" + code + "')].id")
        .getFirst();
  }

  private static String adminId() {
    return Objects.requireNonNull(orbitAdmin).value().toString();
  }

  private static String idOf(MvcResult result) throws Exception {
    return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
  }

  private static String invitation(String name, String email, String roleId) {
    return "{\"displayName\":\""
        + name
        + "\",\"email\":\""
        + email
        + "\",\"userType\":\"MANAGER\",\"roleIds\":[\""
        + roleId
        + "\"]}";
  }

  private static MockHttpServletRequestBuilder as(
      String username, MockHttpServletRequestBuilder request) {
    return request.header("Host", HOST).with(signedIn("orbit", username));
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
