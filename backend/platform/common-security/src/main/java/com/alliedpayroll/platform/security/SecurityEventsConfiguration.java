package com.alliedpayroll.platform.security;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

/**
 * Makes Spring Security tell the application about sign-ins, failures and denials, which the trail
 * records. Without these beans denials are silent and OAuth2 failures publish nothing.
 */
@Configuration(proxyBeanMethods = false)
class SecurityEventsConfiguration {

  @Bean
  AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher events) {
    var publisher = new DefaultAuthenticationEventPublisher(events);
    publisher.setAdditionalExceptionMappings(
        Map.of(
            OAuth2AuthenticationException.class, AuthenticationFailureBadCredentialsEvent.class));
    return publisher;
  }

  @Bean
  AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher events) {
    return new SpringAuthorizationEventPublisher(events);
  }
}
