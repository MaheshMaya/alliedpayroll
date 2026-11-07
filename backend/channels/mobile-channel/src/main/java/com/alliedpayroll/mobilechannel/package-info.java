/**
 * Mobile channel: bootstrap now, delta sync and the offline punch queue later. Shapes data for the
 * Flutter app; owns no business rules and no tables.
 */
@ApplicationModule(displayName = "Mobile channel")
@NullMarked
package com.alliedpayroll.mobilechannel;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
