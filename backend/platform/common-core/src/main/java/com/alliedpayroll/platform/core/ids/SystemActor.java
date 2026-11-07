package com.alliedpayroll.platform.core.ids;

import java.util.UUID;

/** The fixed actor recorded on rows written by jobs and event listeners. */
public final class SystemActor {

  public static final UserId ID =
      new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

  private SystemActor() {}
}
