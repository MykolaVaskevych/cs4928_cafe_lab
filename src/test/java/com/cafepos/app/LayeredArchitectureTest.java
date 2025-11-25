package com.cafepos.app;

import com.cafepos.domain.*;
import com.cafepos.infra.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import com.cafepos.app.events.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LayeredArchitectureTest {

  // Application Layer Tests
  @Test
  void checkoutService_createsReceiptFromOrder() {
    var repo = new InMemoryOrderRepository();
    var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
    var service = new CheckoutService(repo, pricing);

    var factory = new ProductFactory();
    var order = new Order(1001L);
    order.addItem(new LineItem(factory.create("ESP"), 1));
    order.addItem(new LineItem(factory.create("LAT"), 2));
    repo.save(order);

    String receipt = service.checkout(1001L, 10);

    assertTrue(receipt.contains("Order #1001"));
    assertTrue(receipt.contains("Espresso"));
    assertTrue(receipt.contains("Latte"));
    assertTrue(receipt.contains("Total:"));
  }

  @Test
  void receiptFormatter_formatsLineItems() {
    var formatter = new ReceiptFormatter();
    var factory = new ProductFactory();
    var items = java.util.List.of(
      new LineItem(factory.create("ESP"), 2),
      new LineItem(factory.create("LAT"), 1)
    );
    var pricing = new PricingResult(
      com.cafepos.common.Money.of(10.0),
      com.cafepos.common.Money.of(0.5),
      com.cafepos.common.Money.of(0.95),
      com.cafepos.common.Money.of(10.45)
    );

    String receipt = formatter.format(1002L, items, pricing, 10);

    assertTrue(receipt.contains("Order #1002"));
    assertTrue(receipt.contains("Espresso x2"));
    assertTrue(receipt.contains("Latte x1"));
    assertTrue(receipt.contains("Subtotal: 10.00"));
    assertTrue(receipt.contains("Discount: -0.50"));
    assertTrue(receipt.contains("Tax (10%): 0.95"));
    assertTrue(receipt.contains("Total: 10.45"));
  }

  @Test
  void receiptFormatter_omitsDiscountWhenZero() {
    var formatter = new ReceiptFormatter();
    var factory = new ProductFactory();
    var items = java.util.List.of(new LineItem(factory.create("ESP"), 1));
    var pricing = new PricingResult(
      com.cafepos.common.Money.of(2.50),
      com.cafepos.common.Money.zero(),
      com.cafepos.common.Money.of(0.25),
      com.cafepos.common.Money.of(2.75)
    );

    String receipt = formatter.format(1003L, items, pricing, 10);

    assertFalse(receipt.contains("Discount"));
  }

  // Infrastructure Layer Tests
  @Test
  void inMemoryRepository_savesAndFindsOrder() {
    var repo = new InMemoryOrderRepository();
    var order = new Order(2001L);

    repo.save(order);
    var found = repo.findById(2001L);

    assertTrue(found.isPresent());
    assertEquals(2001L, found.get().id());
  }

  @Test
  void inMemoryRepository_returnsEmptyForNonExistentOrder() {
    var repo = new InMemoryOrderRepository();

    var found = repo.findById(9999L);

    assertTrue(found.isEmpty());
  }

  @Test
  void inMemoryRepository_updatesExistingOrder() {
    var repo = new InMemoryOrderRepository();
    var factory = new ProductFactory();
    var order = new Order(2002L);
    repo.save(order);

    order.addItem(new LineItem(factory.create("ESP"), 1));
    repo.save(order);

    var found = repo.findById(2002L);
    assertEquals(1, found.get().items().size());
  }

  @Test
  void wiring_createsComponentsWithCorrectDependencies() {
    var components = Wiring.createDefault();

    assertNotNull(components.repo());
    assertNotNull(components.pricing());
    assertNotNull(components.checkout());
  }

  @Test
  void wiring_checkoutServiceUsesRepository() {
    var components = Wiring.createDefault();
    var factory = new ProductFactory();
    var order = new Order(3001L);
    order.addItem(new LineItem(factory.create("ESP"), 1));
    components.repo().save(order);

    String receipt = components.checkout().checkout(3001L, 10);

    assertTrue(receipt.contains("Order #3001"));
  }

  // Event Bus Tests
  @Test
  void eventBus_emitsAndReceivesEvents() {
    var bus = new EventBus();
    var receivedEvents = new java.util.ArrayList<OrderCreated>();

    bus.on(OrderCreated.class, receivedEvents::add);
    bus.emit(new OrderCreated(4001L));

    assertEquals(1, receivedEvents.size());
    assertEquals(4001L, receivedEvents.get(0).orderId());
  }

  @Test
  void eventBus_supportsMultipleHandlersForSameEvent() {
    var bus = new EventBus();
    var counter = new java.util.concurrent.atomic.AtomicInteger(0);

    bus.on(OrderPaid.class, e -> counter.incrementAndGet());
    bus.on(OrderPaid.class, e -> counter.incrementAndGet());
    bus.emit(new OrderPaid(4002L));

    assertEquals(2, counter.get());
  }

  @Test
  void eventBus_handlesMultipleEventTypes() {
    var bus = new EventBus();
    var createdEvents = new java.util.ArrayList<OrderCreated>();
    var paidEvents = new java.util.ArrayList<OrderPaid>();

    bus.on(OrderCreated.class, createdEvents::add);
    bus.on(OrderPaid.class, paidEvents::add);

    bus.emit(new OrderCreated(4003L));
    bus.emit(new OrderPaid(4004L));
    bus.emit(new OrderCreated(4005L));

    assertEquals(2, createdEvents.size());
    assertEquals(1, paidEvents.size());
  }

  @Test
  void eventBus_noErrorWhenNoHandlers() {
    var bus = new EventBus();

    assertDoesNotThrow(() -> bus.emit(new OrderCreated(4006L)));
  }
}
