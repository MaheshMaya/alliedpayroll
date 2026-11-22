package com.alliedpayroll.audit.domain;

import java.time.YearMonth;

/** Maintenance of the monthly partitions. */
public interface AuditPartitions {

  /** Creates the partitions from this month up to {@code monthsAhead} months on, if missing. */
  void ensureFrom(YearMonth month, int monthsAhead);

  /** Rows that landed in the default partition because their month had none. */
  long rowsInDefaultPartition();
}
