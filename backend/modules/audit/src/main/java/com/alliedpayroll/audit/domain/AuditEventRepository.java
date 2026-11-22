package com.alliedpayroll.audit.domain;

import java.util.List;

/** Reads of the trail in the bound tenant, newest first, by keyset. */
public interface AuditEventRepository {

  List<AuditEvent> find(AuditQuery query);
}
