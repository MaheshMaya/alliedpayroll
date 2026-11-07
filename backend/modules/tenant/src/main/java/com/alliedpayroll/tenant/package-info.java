/**
 * Tenant directory, settings, feature flags, terminology and the bootstrap resource every client
 * calls first. Other modules see only {@code com.alliedpayroll.tenant.api}.
 */
@ApplicationModule(displayName = "Tenant")
@NullMarked
package com.alliedpayroll.tenant;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
