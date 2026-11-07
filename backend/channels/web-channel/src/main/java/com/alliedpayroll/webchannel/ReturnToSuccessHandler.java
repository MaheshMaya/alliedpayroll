package com.alliedpayroll.webchannel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/** After login, sends the browser back to the in-app path it asked for, never off-site. */
@Component
class ReturnToSuccessHandler implements AuthenticationSuccessHandler {

  static final String SESSION_KEY = "webchannel.returnTo";

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    HttpSession session = request.getSession(false);
    String target = "/";
    if (session != null && session.getAttribute(SESSION_KEY) instanceof String saved) {
      session.removeAttribute(SESSION_KEY);
      target = saved;
    }
    response.sendRedirect(target);
  }

  /** Only a path inside this app is accepted; anything else collapses to the root. */
  static String safeReturnTo(String candidate) {
    boolean inApp =
        candidate.startsWith("/") && !candidate.startsWith("//") && !candidate.contains("\\");
    return inApp ? candidate : "/";
  }
}
