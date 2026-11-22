package com.alliedpayroll.platform.security;

import com.alliedpayroll.platform.core.audit.AuditAction;
import com.alliedpayroll.platform.core.audit.AuditActor;
import com.alliedpayroll.platform.core.audit.AuditEntry;
import com.alliedpayroll.platform.core.audit.AuditSink;
import com.alliedpayroll.platform.tenancy.TenantContext;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 * Writes the security rows of the trail: a browser sign-in and sign-out, a sign-in the identity
 * provider refused, and every denial. Bearer tokens are validated on every request, which is not a
 * sign-in, so only the session login counts as LOGIN. Nothing is recorded outside a tenant.
 */
@Component
class SecurityAuditListener {

  private final CurrentUserProvider users;
  private final ObjectProvider<AuditSink> sink;
  private final ApplicationEventPublisher events;

  SecurityAuditListener(
      CurrentUserProvider users, ObjectProvider<AuditSink> sink, ApplicationEventPublisher events) {
    this.users = users;
    this.sink = sink;
    this.events = events;
  }

  @EventListener
  void onLogin(AuthenticationSuccessEvent event) {
    Authentication authentication = event.getAuthentication();
    boolean browserLogin =
        authentication instanceof OAuth2LoginAuthenticationToken
            || authentication instanceof OAuth2AuthenticationToken;
    if (!browserLogin) {
      return;
    }
    record(AuditAction.LOGIN, authentication, null, Map.of());
    // Modules learn of the sign-in without seeing Spring Security's token types.
    if (TenantContext.current().isPresent()) {
      users.from(authentication).ifPresent(user -> events.publishEvent(new UserSignedIn(user)));
    }
  }

  @EventListener
  void onLogout(LogoutSuccessEvent event) {
    record(AuditAction.LOGOUT, event.getAuthentication(), null, Map.of());
  }

  @EventListener
  void onLoginFailed(AbstractAuthenticationFailureEvent event) {
    record(
        AuditAction.LOGIN_FAILED,
        event.getAuthentication(),
        "auth.sign_in_failed",
        Map.of("error", event.getException().getClass().getSimpleName()));
  }

  @EventListener
  void onDenied(AuthorizationDeniedEvent<?> event) {
    Authentication authentication = event.getAuthentication().get();
    // A missing or anonymous credential is a 401, not a permission decision about a person.
    if (authentication == null
        || authentication instanceof AnonymousAuthenticationToken
        || !authentication.isAuthenticated()) {
      return;
    }
    Map<String, Object> params = new HashMap<>();
    String path = RequestOrigin.path();
    if (path != null) {
      params.put("path", path);
    }
    if (!(event.getObject() instanceof RequestAuthorizationContext)) {
      params.put("target", String.valueOf(event.getObject()));
    }
    record(AuditAction.AUTHZ_DENIED, authentication, "authz.denied", params);
  }

  private void record(
      AuditAction action,
      @Nullable Authentication authentication,
      @Nullable String reasonKey,
      Map<String, Object> reasonParams) {
    AuditSink trail = sink.getIfAvailable();
    if (trail == null || TenantContext.current().isEmpty()) {
      return;
    }
    AuthenticatedUser user = users.from(authentication).orElse(null);
    AuditActor actor = AuditActor.UNKNOWN_USER;
    String channel = Channel.WEB.name();
    Map<String, Object> params = new HashMap<>(reasonParams);
    if (user != null) {
      channel = user.channel().name();
      if (user.userId() != null) {
        actor = AuditActor.user(user.userId());
      } else {
        params.put("subject", user.subject());
      }
    }
    trail.record(
        AuditEntry.security(action, actor, channel, RequestOrigin.clientIp(), reasonKey, params));
  }
}
