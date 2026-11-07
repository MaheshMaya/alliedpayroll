package com.alliedpayroll;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alliedpayroll.platform.test.PostgresTestDatabase;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

/**
 * Writes the committed OpenAPI document that the web and mobile SDKs are generated from. A diff in
 * {@code api/openapi/backend.json} on a pull request is the API change review.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiExportTest {

  private static final Path TARGET = Path.of("..", "..", "api", "openapi", "backend.json");

  @Autowired private MockMvc mvc;

  @MockitoBean private JwtDecoder jwtDecoder;

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry registry) {
    PostgresTestDatabase.register(registry);
  }

  @Test
  void exportsTheApiDocument() throws Exception {
    String document =
        mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
    Files.createDirectories(TARGET.getParent());
    Files.writeString(TARGET, document + System.lineSeparator(), StandardCharsets.UTF_8);
  }
}
