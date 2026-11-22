package com.alliedpayroll.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * A refusal at the filter chain, before any controller runs, still leaves as problem+json with a
 * code: {@code identity.user_disabled} for a person the tenant switched off, so their open tab can
 * say why, and {@code authz.denied} for everyone else.
 */
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

  static final String DISABLED = "identity.user_disabled";
  static final String DENIED = "authz.denied";

  private final CurrentUserProvider users;
  private final MessageSource messages;
  private final ObjectMapper json;

  public ApiAccessDeniedHandler(
      CurrentUserProvider users, MessageSource messages, ObjectMapper json) {
    this.users = users;
    this.messages = messages;
    this.json = json;
  }

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException denied)
      throws IOException {
    boolean disabled = users.current().map(AuthenticatedUser::disabled).orElse(false);
    String code = disabled ? DISABLED : DENIED;
    Map<String, Object> problem = new LinkedHashMap<>();
    problem.put("type", "about:blank");
    problem.put("status", HttpServletResponse.SC_FORBIDDEN);
    problem.put("title", messages.getMessage(code, null, code, LocaleContextHolder.getLocale()));
    problem.put("code", code);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    json.writeValue(response.getOutputStream(), problem);
  }
}
