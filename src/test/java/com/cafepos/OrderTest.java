package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class OrderTest {

  @Test
  void order_totals() {
    var p1 = new SimpleProduct("A", "Product A", Money.of(2.50));
    var p2 = new SimpleProduct("B", "Product B", Money.of(3.50));
    var o = new Order(1);
    o.addItem(new LineItem(p1, 2));
    o.addItem(new LineItem(p2, 1));

    assertEquals(Money.of(8.50), o.subtotal());
    assertEquals(Money.of(0.85), o.taxAtPercent(10));
    assertEquals(Money.of(9.35), o.totalWithTax(10));
  }

  @Test
  void order_empty_subtotal() {
    var o = new Order(1);
    assertEquals(Money.zero(), o.subtotal());
  }

  @Test
  void order_add_null_item_fails() {
    var o = new Order(1);
    assertThrows(IllegalArgumentException.class, () -> o.addItem(null));
  }

  @Test
  void payment_strategy_called() {
    var p = new SimpleProduct("A", "A", Money.of(5));
    var order = new Order(42);
    order.addItem(new LineItem(p, 1));
    final boolean[] called = {false};
    PaymentStrategy fake = o -> called[0] = true;
    order.pay(fake);
    assertTrue(called[0], "Payment strategy should be called");
  }

  @Test
  void payment_strategy_null_fails() {
    var order = new Order(1);
    assertThrows(IllegalArgumentException.class, () -> order.pay(null));
  }

  @Test
  void observers_notified_on_item_add() {
    var p = new SimpleProduct("A", "A", Money.of(2));
    var o = new Order(1);
    o.addItem(new LineItem(p, 1)); // baseline
    List<String> events = new ArrayList<>();
    o.register((order, evt) -> events.add(evt));
    o.addItem(new LineItem(p, 1));
    assertTrue(events.contains("itemAdded"));
  }

  @Test
  void observers_notified_on_pay() {
    var p = new SimpleProduct("A", "A", Money.of(2));
    var o = new Order(1);
    o.addItem(new LineItem(p, 1));
    List<String> events = new ArrayList<>();
    o.register((order, evt) -> events.add(evt));
    o.pay(order1 -> {});
    assertTrue(events.contains("paid"));
  }

  @Test
  void observers_notified_on_ready() {
    var o = new Order(1);
    List<String> events = new ArrayList<>();
    o.register((order, evt) -> events.add(evt));
    o.markReady();
    assertTrue(events.contains("ready"));
  }

  @Test
  void multiple_observers_receive_events() {
    var p = new SimpleProduct("A", "A", Money.of(2));
    var o = new Order(1);
    List<String> events1 = new ArrayList<>();
    List<String> events2 = new ArrayList<>();
    o.register((order, evt) -> events1.add(evt));
    o.register((order, evt) -> events2.add(evt));
    o.markReady();
    assertTrue(events1.contains("ready"));
    assertTrue(events2.contains("ready"));
  }

  @Test
  void observer_null_registration_fails() {
    var o = new Order(1);
    assertThrows(IllegalArgumentException.class, () -> o.register(null));
  }

  @Test
  void observer_unregister_removes_observer() {
    var o = new Order(1);
    List<String> events = new ArrayList<>();
    var observer = (com.cafepos.observer.OrderObserver) (order, evt) -> events.add(evt);
    o.register(observer);
    o.unregister(observer);
    o.markReady();
    assertTrue(events.isEmpty(), "Observer should not receive events after unregister");
  }
}
