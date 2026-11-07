package com.alliedpayroll.platform.tenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/** Binds the tenant resolved from the host for the rest of the request, before security runs. */
public final class TenantFilter extends OncePerRequestFilter {

  static final String MDC_KEY = "tenant";

  private final HostTenantResolver resolver;

  public TenantFilter(HostTenantResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Optional<CurrentTenant> tenant = resolver.resolve(request);
    if (tenant.isEmpty()) {
      chain.doFilter(request, response);
      return;
    }
    CurrentTenant current = tenant.get();
    MDC.put(MDC_KEY, current.slug());
    try {
      TenantContext.callAs(
          current,
          () -> {
            chain.doFilter(request, response);
            return Boolean.TRUE;
          });
    } catch (ServletException | IOException | RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
