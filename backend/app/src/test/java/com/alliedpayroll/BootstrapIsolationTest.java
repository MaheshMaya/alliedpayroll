package com.alliedpayroll;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alliedpayroll.platform.test.PostgresTestDatabase;
import com.alliedpayroll.tenant.api.TenantProvisioning;
import com.alliedpayroll.tenant.api.TenantProvisioning.ProvisionTenant;
import java.util.Map;
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
 * The isolation contract of the whole API: a valid token for one tenant is worthless on another
 * tenant's host, and nothing is served without a token or without a tenant.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BootstrapIsolationTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private JwtDecoder jwtDecoder;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @BeforeAll
  static void tenants(@Autowired TenantProvisioning provisioning) {
    provisioning.provision(new ProvisionTenant("sentinel", "Sentinel", "STAFFING", "sentinel"));
    provisioning.provision(new ProvisionTenant("nimbus-it", "Nimbus IT", "CORPORATE", "nimbus-it"));
  }

  @Test
  void memberOfTenantGetsItsBootstrap() throws Exception {
    mvc.perform(
            get("/api/v1/bootstrap")
                .header("Host", "sentinel.localhost")
                .with(memberOf("sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tenant.slug").value("sentinel"))
        .andExpect(jsonPath("$.tenant.industryProfileCode").value("STAFFING"))
        .andExpect(jsonPath("$.user.subject").value("user-sentinel"));
  }

  @Test
  void tokenOfOneTenantIsRefusedOnAnotherTenantsHost() throws Exception {
    mvc.perform(
            get("/api/v1/bootstrap")
                .header("Host", "nimbus-it.localhost")
                .with(memberOf("sentinel")))
        .andExpect(status().isForbidden());
  }

  @Test
  void unknownHostHasNoTenantAndIsRefused() throws Exception {
    mvc.perform(get("/api/v1/bootstrap").header("Host", "localhost").with(memberOf("sentinel")))
        .andExpect(status().isForbidden());
  }

  @Test
  void anonymousIsUnauthorized() throws Exception {
    mvc.perform(get("/api/v1/bootstrap").header("Host", "sentinel.localhost"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void mobileBootstrapUsesTheSameRules() throws Exception {
    mvc.perform(
            get("/api/v1/mobile/bootstrap")
                .header("Host", "sentinel.localhost")
                .with(memberOf("sentinel")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bootstrap.tenant.slug").value("sentinel"))
        .andExpect(jsonPath("$.sync.deltaInterval").exists());
  }

  private static RequestPostProcessor memberOf(String organization) {
    return jwt()
        .jwt(jwt ->
            jwt.subject("user-" + organization)
                .claim("organization", Map.of(organization, Map.of()))
                .claim("name", "Test User")
                .claim("azp", "alliedpayroll-mobile"));
  }
}
