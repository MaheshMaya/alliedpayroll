package com.alliedpayroll;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alliedpayroll.identity.api.UserAdministration;
import com.alliedpayroll.identity.api.UserAdministration.EnsureUser;
import com.alliedpayroll.identity.api.UserType;
import com.alliedpayroll.platform.core.ids.TenantId;
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
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * The industry profile applied at provisioning, module switches and the tenant's own labels,
 * through the API. Changes happen in a tenant of its own (Harbor, a field-service firm), so the
 * shared reference tenants keep the words and modules of their profile.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TenantConfigurationApiTest {

  private static final String HARBOR = "harbor";

  private static @Nullable TenantProvisioning provisioning;
  private static @Nullable String harborId;

  @Autowired private MockMvc mvc;
  @MockitoBean private JwtDecoder jwtDecoder;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @BeforeAll
  static void seed(@Autowired TenantProvisioning tenants, @Autowired UserAdministration users) {
    provisioning = tenants;
    DemoPeople.seed(tenants, users);
    TenantSummary harbor = tenants.provision(harbor());
    harborId = harbor.id().toString();
    TenantContext.runAs(
        new CurrentTenant(new TenantId(harbor.id()), harbor.slug()),
        () -> {
          users.ensureUser(
              new EnsureUser(
                  "admin.harbor",
                  "Lakshmi Raman",
                  "admin@harbor.example",
                  UserType.ADMIN,
                  Set.of("TENANT_ADMIN")));
          users.ensureUser(
              new EnsureUser(
                  "hr.harbor", "Farhan Ali", "hr@harbor.example", UserType.MANAGER, Set.of("HR")));
        });
  }

  @Test
  void staffingTenantStartsWithClientsAndSitesAndCallsSubUnitsPosts() throws Exception {
    mvc.perform(as("sentinel", "admin.sentinel", get("/api/v1/bootstrap")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.features['module:CUSTOMERS']").value(true))
        .andExpect(jsonPath("$.terminology['site_sub_unit']").value("Post"))
        .andExpect(jsonPath("$.terminology['site_sub_unit.plural']").value("Posts"));
  }

  @Test
  void corporateTenantStartsWithoutClientsAndSitesAndCallsLocationsOffices() throws Exception {
    mvc.perform(as("nimbus-it", "admin.nimbus", get("/api/v1/bootstrap")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.features['module:CUSTOMERS']").value(false))
        .andExpect(jsonPath("$.terminology['work_location']").value("Office"))
        .andExpect(jsonPath("$.terminology['site']").value("Site"));
  }

  @Test
  void bootstrapAnswersInTheRequestedLanguage() throws Exception {
    mvc.perform(
            as("sentinel", "admin.sentinel", get("/api/v1/bootstrap"))
                .header("Accept-Language", "ta-IN"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.terminology['site_sub_unit']").value("போஸ்ட்"));
  }

  @Test
  void renamedTermReachesTheBootstrapAndIsAudited() throws Exception {
    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/terminology/en-IN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"labels": {"site": "Location", "site.plural": " Locations "}}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.terms[?(@.key == 'site')].label").value("Location"))
        .andExpect(jsonPath("$.terms[?(@.key == 'site')].defaultLabel").value("Plant"))
        .andExpect(jsonPath("$.terms[?(@.key == 'site')].overridden").value(true));

    mvc.perform(asHarbor("admin.harbor", get("/api/v1/bootstrap")))
        .andExpect(jsonPath("$.terminology['site']").value("Location"))
        .andExpect(jsonPath("$.terminology['site.plural']").value("Locations"));
    mvc.perform(
            asHarbor("admin.harbor", get("/api/v1/audit-events"))
                .param("filter[entityId]", Objects.requireNonNull(harborId)))
        .andExpect(jsonPath("$.items[0].action").value("UPDATE"))
        .andExpect(jsonPath("$.items[0].entityType").value("TERMINOLOGY"))
        .andExpect(jsonPath("$.items[0].oldValues.site").value("Plant"))
        .andExpect(jsonPath("$.items[0].newValues.site").value("Location"));
    mvc.perform(as("nimbus-it", "admin.nimbus", get("/api/v1/bootstrap")))
        .andExpect(jsonPath("$.terminology['site']").value("Site"));
  }

  @Test
  void blankLabelGoesBackToTheProfileWord() throws Exception {
    relabel("en-IN", "{\"labels\": {\"duty\": \"Visit\"}}");

    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/terminology/en-IN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"labels\": {\"duty\": \"  \"}}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.terms[?(@.key == 'duty')].label").value("Man-day"))
        .andExpect(jsonPath("$.terms[?(@.key == 'duty')].overridden").value(false));
  }

  @Test
  void labelInOneLanguageLeavesTheOthersAlone() throws Exception {
    relabel("hi", "{\"labels\": {\"engagement\": \"ठेका\"}}");

    mvc.perform(
            asHarbor("admin.harbor", get("/api/v1/bootstrap")).header("Accept-Language", "hi-IN"))
        .andExpect(jsonPath("$.terminology['engagement']").value("ठेका"));
    mvc.perform(asHarbor("admin.harbor", get("/api/v1/bootstrap")))
        .andExpect(jsonPath("$.terminology['engagement']").value("Work order"));
  }

  @Test
  void switchedOffModuleLeavesTheBootstrapAndIsAudited() throws Exception {
    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/modules/INVENTORY"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enabled\": false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("INVENTORY"))
        .andExpect(jsonPath("$.enabled").value(false));

    mvc.perform(asHarbor("admin.harbor", get("/api/v1/bootstrap")))
        .andExpect(jsonPath("$.features['module:INVENTORY']").value(false));
    mvc.perform(
            asHarbor("admin.harbor", get("/api/v1/audit-events"))
                .param("filter[entityType]", "MODULE"))
        .andExpect(jsonPath("$.items[0].action").value("STATE_CHANGE"))
        .andExpect(jsonPath("$.items[0].entityLabel").value("INVENTORY"))
        .andExpect(jsonPath("$.items[0].reasonKey").value("tenant.module_disabled"));
  }

  @Test
  void provisioningAgainKeepsWhatTheTenantSwitched() throws Exception {
    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/modules/PROJECTS"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enabled\": false}"))
        .andExpect(status().isOk());

    Objects.requireNonNull(provisioning).provision(harbor());

    mvc.perform(asHarbor("admin.harbor", get("/api/v1/modules")))
        .andExpect(jsonPath("$[?(@.code == 'PROJECTS')].enabled").value(false))
        .andExpect(jsonPath("$[?(@.code == 'TIMESHEETS')].enabled").value(true));
  }

  @Test
  void personWithoutTheSettingsPermissionIsRefused() throws Exception {
    mvc.perform(asHarbor("hr.harbor", get("/api/v1/modules")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("authz.denied"));
    mvc.perform(
            asHarbor("hr.harbor", put("/api/v1/terminology/en-IN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"labels\": {\"site\": \"Yard\"}}"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("authz.denied"));
  }

  @Test
  void unknownModuleTermAndLanguageAreRefused() throws Exception {
    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/modules/PAYROLL"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enabled\": false}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("module.not_found"));
    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/terminology/en-IN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"labels\": {\"guard\": \"Watchman\"}}"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("tenant.terminology_key_unknown"));
    mvc.perform(asHarbor("admin.harbor", get("/api/v1/terminology/fr")))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("tenant.locale_unsupported"));
  }

  private void relabel(String locale, String body) throws Exception {
    mvc.perform(
            asHarbor("admin.harbor", put("/api/v1/terminology/" + locale))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }

  private static ProvisionTenant harbor() {
    return new ProvisionTenant(HARBOR, "Harbor Boiler Works", "FIELD_SERVICE", HARBOR);
  }

  private static MockHttpServletRequestBuilder asHarbor(
      String username, MockHttpServletRequestBuilder request) {
    return as(HARBOR, username, request);
  }

  private static MockHttpServletRequestBuilder as(
      String tenant, String username, MockHttpServletRequestBuilder request) {
    return request.header("Host", tenant + ".localhost").with(signedIn(tenant, username));
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
