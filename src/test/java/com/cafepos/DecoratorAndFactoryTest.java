package com.cafepos;

import com.cafepos.catalog.Priced;
import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.decorator.*;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DecoratorAndFactoryTest {

  // Test 1: Single decorator changes price and name as expected
  @Test
  void decorator_single_addon() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product withShot = new ExtraShot(espresso);
    assertEquals("Espresso + Extra Shot", withShot.name());
    assertEquals(Money.of(3.30), ((Priced) withShot).price());
  }

  @Test
  void decorator_single_oat_milk() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product withOat = new OatMilk(espresso);
    assertEquals("Espresso + Oat Milk", withOat.name());
    assertEquals(Money.of(3.00), ((Priced) withOat).price());
  }

  @Test
  void decorator_single_syrup() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product withSyrup = new Syrup(espresso);
    assertEquals("Espresso + Syrup", withSyrup.name());
    assertEquals(Money.of(2.90), ((Priced) withSyrup).price());
  }

  @Test
  void decorator_single_size_large() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product large = new SizeLarge(espresso);
    assertEquals("Espresso (Large)", large.name());
    assertEquals(Money.of(3.20), ((Priced) large).price());
  }

  // Test 2: Multiple decorators stack correctly
  @Test
  void decorator_stacks() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));
    assertEquals("Espresso + Extra Shot + Oat Milk (Large)", decorated.name());
    assertEquals(Money.of(4.50), ((Priced) decorated).price());
  }

  @Test
  void decorator_stacks_different_order() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product decorated1 = new SizeLarge(new OatMilk(new ExtraShot(espresso)));
    Product decorated2 = new ExtraShot(new SizeLarge(new OatMilk(espresso)));

    // Different names due to different wrapping order
    assertNotEquals(decorated1.name(), decorated2.name());
    // But same price (commutative)
    assertEquals(((Priced) decorated1).price(), ((Priced) decorated2).price());
  }

  // Test 3: Factory parses recipes correctly
  @Test
  void factory_parses_recipe() {
    ProductFactory f = new ProductFactory();
    Product p = f.create("ESP+SHOT+OAT");
    assertTrue(p.name().contains("Espresso"));
    assertTrue(p.name().contains("Extra Shot"));
    assertTrue(p.name().contains("Oat Milk"));
  }

  @Test
  void factory_creates_base_espresso() {
    ProductFactory f = new ProductFactory();
    Product p = f.create("ESP");
    assertEquals("Espresso", p.name());
    assertEquals(Money.of(2.50), ((Priced) p).price());
  }

  @Test
  void factory_creates_base_latte() {
    ProductFactory f = new ProductFactory();
    Product p = f.create("LAT");
    assertEquals("Latte", p.name());
    assertEquals(Money.of(3.20), ((Priced) p).price());
  }

  @Test
  void factory_creates_base_cappuccino() {
    ProductFactory f = new ProductFactory();
    Product p = f.create("CAP");
    assertEquals("Cappuccino", p.name());
    assertEquals(Money.of(3.00), ((Priced) p).price());
  }

  @Test
  void factory_handles_large_latte() {
    ProductFactory f = new ProductFactory();
    Product p = f.create("LAT+L");
    assertEquals("Latte (Large)", p.name());
    assertEquals(Money.of(3.90), ((Priced) p).price()); // 3.20 + 0.70
  }

  @Test
  void factory_handles_case_insensitive() {
    ProductFactory f = new ProductFactory();
    Product p1 = f.create("esp+shot+oat");
    Product p2 = f.create("ESP+SHOT+OAT");
    assertEquals(p1.name(), p2.name());
    assertEquals(((Priced) p1).price(), ((Priced) p2).price());
  }

  @Test
  void factory_handles_whitespace() {
    ProductFactory f = new ProductFactory();
    Product p1 = f.create("ESP + SHOT + OAT");
    Product p2 = f.create("ESP+SHOT+OAT");
    assertEquals(p1.name(), p2.name());
    assertEquals(((Priced) p1).price(), ((Priced) p2).price());
  }

  @Test
  void factory_throws_on_unknown_base() {
    ProductFactory f = new ProductFactory();
    assertThrows(IllegalArgumentException.class, () -> f.create("UNKNOWN"));
  }

  @Test
  void factory_throws_on_unknown_addon() {
    ProductFactory f = new ProductFactory();
    assertThrows(IllegalArgumentException.class, () -> f.create("ESP+UNKNOWN"));
  }

  @Test
  void factory_throws_on_null() {
    ProductFactory f = new ProductFactory();
    assertThrows(IllegalArgumentException.class, () -> f.create(null));
  }

  @Test
  void factory_throws_on_blank() {
    ProductFactory f = new ProductFactory();
    assertThrows(IllegalArgumentException.class, () -> f.create(""));
  }

  // Test 4: Order uses decorated price
  @Test
  void order_uses_decorated_price() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product withShot = new ExtraShot(espresso); // 3.30
    Order o = new Order(1);
    o.addItem(new LineItem(withShot, 2));
    assertEquals(Money.of(6.60), o.subtotal());
  }

  @Test
  void order_uses_multiple_decorated_items() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product withShot = new ExtraShot(espresso); // 3.30
    Product withOat = new OatMilk(espresso); // 3.00

    Order o = new Order(1);
    o.addItem(new LineItem(withShot, 1)); // 3.30
    o.addItem(new LineItem(withOat, 1));  // 3.00
    assertEquals(Money.of(6.30), o.subtotal());
  }

  // Test: Decorator null safety
  @Test
  void decorator_null_base_throws() {
    assertThrows(IllegalArgumentException.class, () -> new ExtraShot(null));
    assertThrows(IllegalArgumentException.class, () -> new OatMilk(null));
    assertThrows(IllegalArgumentException.class, () -> new Syrup(null));
    assertThrows(IllegalArgumentException.class, () -> new SizeLarge(null));
  }

  // Activity test: Factory vs Manual - prove they build the same drink
  @Test
  void factory_vs_manual_same_drink() {
    ProductFactory factory = new ProductFactory();
    Product viaFactory = factory.create("ESP+SHOT+OAT+L");

    Product viaManual = new SizeLarge(
        new OatMilk(
            new ExtraShot(
                new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
            )
        )
    );

    // Same name
    assertEquals(viaFactory.name(), viaManual.name());

    // Same price
    assertEquals(((Priced) viaFactory).price(), ((Priced) viaManual).price());

    // Same behavior in orders
    Order order1 = new Order(1);
    order1.addItem(new LineItem(viaFactory, 1));

    Order order2 = new Order(2);
    order2.addItem(new LineItem(viaManual, 1));

    assertEquals(order1.subtotal(), order2.subtotal());
    assertEquals(order1.totalWithTax(10), order2.totalWithTax(10));
  }

  @Test
  void decorator_preserves_base_id() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));
    assertEquals("P-ESP", decorated.id());
  }

  @Test
  void decorator_preserves_base_price() {
    Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
    Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));
    assertEquals(Money.of(2.50), decorated.basePrice());
  }
}
