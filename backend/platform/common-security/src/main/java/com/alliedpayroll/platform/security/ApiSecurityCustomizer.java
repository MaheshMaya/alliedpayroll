package com.alliedpayroll.platform.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * How a channel module adds its authentication mechanism to the shared filter chain, such as the
 * browser login flow. Customizers run before the common rules and may only shape authentication;
 * authorization stays here.
 */
@FunctionalInterface
public interface ApiSecurityCustomizer {

  void customize(HttpSecurity http) throws Exception;
}
