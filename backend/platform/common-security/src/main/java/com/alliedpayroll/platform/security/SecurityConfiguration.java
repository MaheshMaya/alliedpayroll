package com.alliedpayroll.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * One filter chain for the API and the channel endpoints. Bearer tokens (mobile, kiosk, partners)
 * are validated as JWTs; the browser session is added by the web channel's customizer. Every {@code
 * /api/**} call must be authenticated and belong to the resolved tenant.
 */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfiguration {

  @Bean
  @Order(1)
  SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
    http.securityMatcher("/actuator/**")
        .authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info")
                .permitAll()
                .anyRequest()
                .denyAll())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.disable());
    return http.build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain apiSecurity(
      HttpSecurity http,
      ObjectProvider<ApiSecurityCustomizer> customizers,
      TenantMembershipAuthorizationManager tenantMembership,
      ApiAccessDeniedHandler accessDenied)
      throws Exception {
    for (ApiSecurityCustomizer customizer : customizers.orderedStream().toList()) {
      customizer.customize(http);
    }
    var csrfHandler = new CsrfTokenRequestAttributeHandler();
    csrfHandler.setCsrfRequestAttributeName(null);
    http.authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers("/v3/api-docs/**")
                .permitAll()
                .requestMatchers("/api/**")
                .access(tenantMembership)
                .anyRequest()
                .authenticated())
        .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
        .csrf(csrf ->
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfHandler)
                .ignoringRequestMatchers(SecurityConfiguration::carriesBearerToken))
        .exceptionHandling(handling ->
            handling
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    PathPatternRequestMatcher.withDefaults().matcher("/api/**"))
                .defaultAccessDeniedHandlerFor(
                    accessDenied, PathPatternRequestMatcher.withDefaults().matcher("/api/**")));
    return http.build();
  }

  @Bean
  AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
    return new AnnotationTemplateExpressionDefaults();
  }

  private static boolean carriesBearerToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    return header != null && header.startsWith("Bearer ");
  }
}
