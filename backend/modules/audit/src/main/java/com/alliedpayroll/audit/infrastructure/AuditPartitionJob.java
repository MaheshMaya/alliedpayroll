package com.alliedpayroll.audit.infrastructure;

import com.alliedpayroll.audit.application.AuditPartitionUpkeep;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Runs on the batch deployment: once at start, then nightly. */
@Component
@Profile("batch")
class AuditPartitionJob {

  private final AuditPartitionUpkeep upkeep;

  AuditPartitionJob(AuditPartitionUpkeep upkeep) {
    this.upkeep = upkeep;
  }

  @Scheduled(initialDelayString = "PT1M", fixedDelayString = "PT24H")
  void keepPartitionsAhead() {
    upkeep.run();
  }
}
