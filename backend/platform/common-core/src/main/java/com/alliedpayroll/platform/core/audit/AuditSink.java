package com.alliedpayroll.platform.core.audit;

/**
 * Where the trail is written. Implemented by the audit module; called inside the transaction of the
 * change it describes, so a change without its audit row cannot commit.
 */
@FunctionalInterface
public interface AuditSink {

  void record(AuditEntry entry);
}
