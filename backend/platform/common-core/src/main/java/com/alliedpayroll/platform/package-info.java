/**
 * Platform libraries: cross-cutting code with no business meaning. Open so every module may use any
 * package below it; business modules expose only their {@code api} package.
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
@NullMarked
package com.alliedpayroll.platform;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
