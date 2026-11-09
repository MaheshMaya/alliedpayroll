package com.alliedpayroll.webchannel;

import com.alliedpayroll.platform.tenancy.TenantContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

/**
 * Pins the login to the tenant of the host: Keycloak's {@code organization:<alias>} scope makes it
 * authenticate the user as a member of that organisation and fail otherwise.
 */
@Component
class OrganizationScopeCustomizer implements Consumer<OAuth2AuthorizationRequest.Builder> {

  private final ClientRegistrationRepository registrations;

  private static final String ORGANIZATION_SCOPE = "organization";

  OrganizationScopeCustomizer(ClientRegistrationRepository registrations) {
    this.registrations = registrations;
  }

  @Override
  public void accept(OAuth2AuthorizationRequest.Builder builder) {
    // Fails closed: the authorization endpoint is never reached from a host without a tenant.
    var tenant = TenantContext.require();
    // Keycloak refuses the bare scope next to the aliased one, so the alias replaces it.
    Set<String> scopes = new LinkedHashSet<>(configuredScopes());
    scopes.remove(ORGANIZATION_SCOPE);
    scopes.add(ORGANIZATION_SCOPE + ":" + tenant.slug());
    builder.scopes(scopes);
  }

  private Set<String> configuredScopes() {
    ClientRegistration registration =
        registrations.findByRegistrationId(AuthController.REGISTRATION_ID);
    return registration == null ? Set.of() : registration.getScopes();
  }
}
