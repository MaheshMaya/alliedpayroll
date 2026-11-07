package com.alliedpayroll.platform.core;

import com.alliedpayroll.platform.core.ids.IdGenerator;
import com.alliedpayroll.platform.core.ids.UuidV7IdGenerator;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Time and identity are injected so payroll snapshots and tests are reproducible. */
@Configuration(proxyBeanMethods = false)
public class CoreConfiguration {

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  IdGenerator idGenerator() {
    return new UuidV7IdGenerator();
  }
}
