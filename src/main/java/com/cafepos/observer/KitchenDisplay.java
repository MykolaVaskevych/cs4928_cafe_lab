package com.cafepos.observer;

import com.cafepos.domain.Order;

public final class KitchenDisplay implements OrderObserver {

  @Override
  public void updated(Order order, String eventType) {
    if (eventType.equals("itemAdded")) {
      System.out.println("[Kitchen] Order #" + order.id() + ": item added");
    } else if (eventType.equals("paid")) {
      System.out.println("[Kitchen] Order #" + order.id() + ": Payment received");
    }
  }
}
