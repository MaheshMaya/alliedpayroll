package com.alliedpayroll;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Scheduled work runs only on the batch deployment; the API never hosts a timer. */
@Configuration(proxyBeanMethods = false)
@Profile("batch")
@EnableScheduling
class BatchConfiguration {}
