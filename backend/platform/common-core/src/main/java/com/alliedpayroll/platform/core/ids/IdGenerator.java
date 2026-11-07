package com.alliedpayroll.platform.core.ids;

import java.util.UUID;

/**
 * Source of new primary keys. Injected so tests are deterministic and so every row created by the
 * application carries a time-ordered UUID v7.
 */
@FunctionalInterface
public interface IdGenerator {

  UUID next();
}
