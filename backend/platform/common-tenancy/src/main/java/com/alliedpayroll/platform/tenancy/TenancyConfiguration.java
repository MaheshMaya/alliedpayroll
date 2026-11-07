package com.alliedpayroll.platform.tenancy;

import javax.sql.DataSource;
import org.springframework.boot.transaction.autoconfigure.TransactionManagerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@Configuration(proxyBeanMethods = false)
public class TenancyConfiguration {

  /** Runs before the security filter chain so authorization can compare principal and tenant. */
  public static final int TENANT_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 100;

  @Bean
  HostTenantResolver hostTenantResolver(TenantDirectory directory) {
    return new HostTenantResolver(directory);
  }

  @Bean
  FilterRegistrationBean<TenantFilter> tenantFilter(HostTenantResolver resolver) {
    var registration = new FilterRegistrationBean<>(new TenantFilter(resolver));
    registration.setOrder(TENANT_FILTER_ORDER);
    registration.setName("tenantFilter");
    return registration;
  }

  @Bean
  TransactionManagerCustomizer<AbstractPlatformTransactionManager> tenantTransactionCustomizer(
      DataSource dataSource) {
    var listener = new TenantTransactionListener(dataSource);
    return manager -> manager.addListener(listener);
  }

  @Bean
  HibernateTenantResolver hibernateTenantResolver() {
    return new HibernateTenantResolver();
  }
}
