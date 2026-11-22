package com.alliedpayroll.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** The client address of the request being served, when there is one. */
final class RequestOrigin {

  private RequestOrigin() {}

  static @Nullable String clientIp() {
    return current().map(HttpServletRequest::getRemoteAddr).orElse(null);
  }

  static @Nullable String path() {
    return current().map(HttpServletRequest::getRequestURI).orElse(null);
  }

  private static Optional<HttpServletRequest> current() {
    return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servlet
        ? Optional.of(servlet.getRequest())
        : Optional.empty();
  }
}
