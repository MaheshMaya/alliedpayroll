package com.alliedpayroll.identity.infrastructure.keycloak;

import com.alliedpayroll.identity.application.IdentityDirectory;
import com.alliedpayroll.platform.core.error.DependencyUnavailableException;
import java.net.http.HttpClient;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(KeycloakProperties.class)
class DirectoryConfiguration {

  @Bean
  IdentityDirectory identityDirectory(KeycloakProperties properties, Clock clock) {
    if (!properties.configured()) {
      return new UnconfiguredDirectory();
    }
    HttpClient http = HttpClient.newBuilder().connectTimeout(properties.timeout()).build();
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
    factory.setReadTimeout(properties.timeout());
    return new KeycloakDirectory(properties, RestClient.builder().requestFactory(factory), clock);
  }

  /** Without credentials every write that needs Keycloak fails cleanly and changes nothing. */
  private static final class UnconfiguredDirectory implements IdentityDirectory {

    @Override
    public void invite(String organizationAlias, NewAccount account) {
      throw unavailable();
    }

    @Override
    public void disable(String username) {
      throw unavailable();
    }

    @Override
    public void enable(String username) {
      throw unavailable();
    }

    private static DependencyUnavailableException unavailable() {
      return new DependencyUnavailableException(
          KeycloakDirectory.UNAVAILABLE,
          new IllegalStateException("alliedpayroll.identity.keycloak is not configured"));
    }
  }
}
