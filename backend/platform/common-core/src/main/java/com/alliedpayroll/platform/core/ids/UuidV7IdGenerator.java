package com.alliedpayroll.platform.core.ids;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/** UUID v7: millisecond timestamp prefix plus randomness, so indexes grow at the end. */
public final class UuidV7IdGenerator implements IdGenerator {

  @Override
  public UUID next() {
    return UuidCreator.getTimeOrderedEpoch();
  }
}
