package com.cafepos.domain;

import java.util.concurrent.atomic.AtomicLong;

public final class OrderIds {

  private static final AtomicLong counter = new AtomicLong(1000);

  private OrderIds() {
  }

  public static long next() {
    return counter.incrementAndGet();
  }
}
