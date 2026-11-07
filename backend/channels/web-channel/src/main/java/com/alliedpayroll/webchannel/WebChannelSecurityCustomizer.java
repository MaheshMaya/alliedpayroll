package com.alliedpayroll.webchannel;

import com.alliedpayroll.platform.security.ApiSecurityCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;

/**
 * Adds the browser login to the shared chain: {@code /auth/login} starts the Authorization Code
 * flow with PKCE and the tenant's organisation scope, {@code /auth/callback} receives the code,
 * {@code /auth/logout} ends both the session and the Keycloak session.
 */
@Component
class WebChannelSecurityCustomizer implements ApiSecurityCustomizer {

  static final String AUTHORIZE_BASE = "/auth/authorize";
  static final String CALLBACK = "/auth/callback";
  static final String LOGOUT = "/auth/logout";

  private final ClientRegistrationRepository registrations;
  private final ReturnToSuccessHandler successHandler;
  private final OrganizationScopeCustomizer organizationScope;
  private final OidcLogoutHandlerFactory logoutHandlers;

  WebChannelSecurityCustomizer(
      ClientRegistrationRepository registrations,
      ReturnToSuccessHandler successHandler,
      OrganizationScopeCustomizer organizationScope,
      OidcLogoutHandlerFactory logoutHandlers) {
    this.registrations = registrations;
    this.successHandler = successHandler;
    this.organizationScope = organizationScope;
    this.logoutHandlers = logoutHandlers;
  }

  @Override
  public void customize(HttpSecurity http) throws Exception {
    var resolver = new DefaultOAuth2AuthorizationRequestResolver(registrations, AUTHORIZE_BASE);
    resolver.setAuthorizationRequestCustomizer(
        OAuth2AuthorizationRequestCustomizers.withPkce().andThen(organizationScope));
    http.authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers(AuthController.LOGIN, AUTHORIZE_BASE + "/**", CALLBACK)
                .permitAll())
        .oauth2Login(login ->
            login
                .loginPage(AuthController.LOGIN)
                .authorizationEndpoint(endpoint ->
                    endpoint.baseUri(AUTHORIZE_BASE).authorizationRequestResolver(resolver))
                .redirectionEndpoint(endpoint -> endpoint.baseUri(CALLBACK))
                .successHandler(successHandler))
        .logout(logout ->
            logout
                .logoutUrl(LOGOUT)
                .logoutSuccessHandler(logoutHandlers.oidcLogout())
                .deleteCookies("XSRF-TOKEN"))
        .exceptionHandling(handling ->
            handling.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                PathPatternRequestMatcher.withDefaults().matcher(AuthController.SESSION)));
  }
}
