package com.alliedpayroll.identity.api;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/** Display names for user ids, so other modules can show who did something without a join. */
public interface UserDirectory {

  Map<UUID, String> displayNames(Collection<UUID> userIds);
}
