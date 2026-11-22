package com.alliedpayroll.platform.web;

import com.alliedpayroll.platform.core.error.ConflictException;
import com.alliedpayroll.platform.core.error.DependencyUnavailableException;
import com.alliedpayroll.platform.core.error.DomainException;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.error.PreconditionFailedException;
import com.alliedpayroll.platform.tenancy.MissingTenantException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Every error leaves as problem+json with a stable {@code code} and a localized title. Framework
 * exceptions are covered by Spring's own problem-details support, switched on in configuration.
 */
@RestControllerAdvice
public class ProblemHandler {

  private final MessageSource messages;

  public ProblemHandler(MessageSource messages) {
    this.messages = messages;
  }

  @ExceptionHandler(NotFoundException.class)
  ProblemDetail notFound(NotFoundException e, Locale locale) {
    return problem(HttpStatus.NOT_FOUND, e.code(), e.params(), locale);
  }

  @ExceptionHandler(ConflictException.class)
  ProblemDetail conflict(ConflictException e, Locale locale) {
    return problem(HttpStatus.CONFLICT, e.code(), e.params(), locale);
  }

  @ExceptionHandler(PreconditionFailedException.class)
  ProblemDetail stale(PreconditionFailedException e, Locale locale) {
    return problem(HttpStatus.PRECONDITION_FAILED, e.code(), e.params(), locale);
  }

  @ExceptionHandler(DependencyUnavailableException.class)
  ProblemDetail unavailable(DependencyUnavailableException e, Locale locale) {
    return problem(HttpStatus.SERVICE_UNAVAILABLE, e.code(), e.params(), locale);
  }

  @ExceptionHandler(DomainException.class)
  ProblemDetail domain(DomainException e, Locale locale) {
    return problem(HttpStatus.UNPROCESSABLE_CONTENT, e.code(), e.params(), locale);
  }

  /**
   * A signed-in member without the permission; the web hides the control, the API still says so.
   */
  @ExceptionHandler(AccessDeniedException.class)
  ProblemDetail denied(Locale locale) {
    return problem(HttpStatus.FORBIDDEN, "authz.denied", Map.of(), locale);
  }

  @ExceptionHandler(MissingTenantException.class)
  ProblemDetail missingTenant(Locale locale) {
    return problem(HttpStatus.NOT_FOUND, "tenant.not_resolved", Map.of(), locale);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail invalid(MethodArgumentNotValidException e, Locale locale) {
    ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "request.invalid", Map.of(), locale);
    List<Map<String, String>> errors =
        e.getBindingResult().getFieldErrors().stream().map(err -> fieldError(err, locale)).toList();
    problem.setProperty("errors", errors);
    return problem;
  }

  private Map<String, String> fieldError(FieldError error, Locale locale) {
    String code = "validation." + String.valueOf(error.getCode()).toLowerCase(Locale.ROOT);
    return Map.of("field", error.getField(), "code", code, "message", text(code, Map.of(), locale));
  }

  private ProblemDetail problem(
      HttpStatus status, String code, Map<String, Object> params, Locale locale) {
    ProblemDetail problem = ProblemDetail.forStatus(status);
    problem.setTitle(text(code, params, locale));
    problem.setProperty("code", code);
    if (!params.isEmpty()) {
      problem.setProperty("params", params);
    }
    return problem;
  }

  private String text(String code, Map<String, Object> params, Locale locale) {
    return Objects.requireNonNullElse(
        messages.getMessage(code, params.values().toArray(), code, locale), code);
  }
}
