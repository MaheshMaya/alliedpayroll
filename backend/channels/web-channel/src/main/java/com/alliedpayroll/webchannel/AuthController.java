package com.alliedpayroll.webchannel;

import com.alliedpayroll.platform.security.AuthenticatedUser;
import com.alliedpayroll.platform.security.CurrentUserProvider;
import com.alliedpayroll.platform.tenancy.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** The only auth surface the SPA talks to; tokens never leave the server. */
@RestController
@Tag(name = "Web session", description = "Browser login through the backend token handler")
class AuthController {

  static final String LOGIN = "/auth/login";
  static final String SESSION = "/auth/session";
  static final String REGISTRATION_ID = "keycloak";

  private final CurrentUserProvider users;

  AuthController(CurrentUserProvider users) {
    this.users = users;
  }

  @GetMapping(LOGIN)
  @Operation(operationId = "login", summary = "Start the sign-in flow for this tenant")
  ResponseEntity<Void> login(
      @RequestParam(name = "return_to", required = false) @Nullable String returnTo,
      @RequestParam(name = "error", required = false) @Nullable String error,
      HttpServletRequest request) {
    if (error != null) {
      // Spring sends a failed callback here; answering instead of redirecting stops the loop.
      throw new SignInFailedException();
    }
    if (returnTo != null) {
      request
          .getSession(true)
          .setAttribute(
              ReturnToSuccessHandler.SESSION_KEY, ReturnToSuccessHandler.safeReturnTo(returnTo));
    }
    return ResponseEntity.status(HttpStatus.FOUND)
        .header(
            HttpHeaders.LOCATION,
            WebChannelSecurityCustomizer.AUTHORIZE_BASE + "/" + REGISTRATION_ID)
        .build();
  }

  @GetMapping(SESSION)
  @Operation(operationId = "getSession", summary = "Who is signed in on this browser session")
  SessionView session() {
    AuthenticatedUser user = users.require();
    String tenant = TenantContext.current().map(current -> current.slug()).orElse(null);
    return new SessionView(user.subject(), user.displayName(), user.email(), tenant);
  }

  record SessionView(
      String subject,
      @Nullable String displayName,
      @Nullable String email,
      @Nullable String tenantSlug) {}
}
