/**
 * The append-only trail of changes and security events. Modules write through the platform's {@code
 * AuditSink}; this module stores the rows, keeps the monthly partitions and serves the API.
 */
@ApplicationModule(displayName = "Audit")
@NullMarked
package com.alliedpayroll.audit;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
