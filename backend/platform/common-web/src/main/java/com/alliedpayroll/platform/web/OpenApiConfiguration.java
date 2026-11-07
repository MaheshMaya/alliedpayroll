package com.alliedpayroll.platform.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The single committed OpenAPI document that every client SDK is generated from. */
@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

  @Bean
  OpenAPI openApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Allied Payroll API")
                .version("v1")
                .description(
                    "Canonical headless API consumed by web, mobile, kiosk and partners."));
  }
}
