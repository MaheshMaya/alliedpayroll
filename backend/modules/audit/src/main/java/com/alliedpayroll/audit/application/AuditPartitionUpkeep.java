package com.alliedpayroll.audit.application;

import com.alliedpayroll.audit.domain.AuditPartitions;
import java.time.Clock;
import java.time.YearMonth;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Keeps three months of partitions ahead and says when the safety net caught rows. */
@Service
public class AuditPartitionUpkeep {

  static final int MONTHS_AHEAD = 3;

  private static final Logger log = LoggerFactory.getLogger(AuditPartitionUpkeep.class);

  private final AuditPartitions partitions;
  private final Clock clock;

  AuditPartitionUpkeep(AuditPartitions partitions, Clock clock) {
    this.partitions = partitions;
    this.clock = clock;
  }

  public void run() {
    partitions.ensureFrom(YearMonth.now(clock.withZone(ZoneOffset.UTC)), MONTHS_AHEAD);
    long stray = partitions.rowsInDefaultPartition();
    if (stray > 0) {
      log.warn("audit: {} rows sit in the default partition; a month had no partition", stray);
    }
  }
}
