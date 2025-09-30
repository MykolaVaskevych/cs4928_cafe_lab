package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.payment.PaymentStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
