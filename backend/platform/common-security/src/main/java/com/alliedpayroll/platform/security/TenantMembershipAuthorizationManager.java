package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.tenancy.TenantContext;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 * A tenant API call is allowed only when the request resolved to a tenant and the authenticated
 * user belongs to that tenant's organisation. A user of one tenant on another tenant's host gets
 * 403 even with a valid token, and so does a member the tenant has disabled, whose browser session
 * or token may outlive the change.
 */
@Component
public class TenantMembershipAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {

  private final CurrentUserProvider users;

  public TenantMembershipAuthorizationManager(CurrentUserProvider users) {
    this.users = users;
  }

  @Override
  public AuthorizationResult authorize(
      Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
    boolean granted =
        users
            .from(authentication.get())
            .filter(user -> !user.disabled())
            .flatMap(user -> TenantContext.current().map(tenant -> user.belongsTo(tenant.slug())))
            .orElse(false);
    return new AuthorizationDecision(granted);
  }
}
