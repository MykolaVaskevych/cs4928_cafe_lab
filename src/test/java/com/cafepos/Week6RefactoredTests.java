package com.cafepos;

import com.cafepos.checkout.CheckoutService;
import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Week6RefactoredTests {

  // ========== DiscountPolicy Tests ==========

  @Test
  void no_discount_returns_zero() {
    DiscountPolicy d = new NoDiscount();
    assertEquals(Money.zero(), d.discountOf(Money.of(10.00)));
  }

  @Test
  void loyalty_discount_5_percent() {
    DiscountPolicy d = new LoyaltyPercentDiscount(5);
    assertEquals(Money.of(0.39), d.discountOf(Money.of(7.80)));
  }

  @Test
  void loyalty_discount_10_percent() {
    DiscountPolicy d = new LoyaltyPercentDiscount(10);
    assertEquals(Money.of(1.00), d.discountOf(Money.of(10.00)));
  }

  @Test
  void loyalty_discount_negative_percent_throws() {
    assertThrows(IllegalArgumentException.class, () -> new LoyaltyPercentDiscount(-5));
  }

  @Test
  void fixed_coupon_discount_applies_amount() {
    DiscountPolicy d = new FixedCouponDiscount(Money.of(1.00));
    assertEquals(Money.of(1.00), d.discountOf(Money.of(10.00)));
  }

  @Test
  void fixed_coupon_discount_capped_at_subtotal() {
    DiscountPolicy d = new FixedCouponDiscount(Money.of(15.00));
    assertEquals(Money.of(10.00), d.discountOf(Money.of(10.00)));
  }

  @Test
  void fixed_coupon_null_amount_throws() {
    assertThrows(IllegalArgumentException.class, () -> new FixedCouponDiscount(null));
  }

  // ========== TaxPolicy Tests ==========

  @Test
  void fixed_rate_tax_10_percent() {
    TaxPolicy t = new FixedRateTaxPolicy(10);
    assertEquals(Money.of(0.74), t.taxOn(Money.of(7.41)));
  }

  @Test
  void fixed_rate_tax_20_percent() {
    TaxPolicy t = new FixedRateTaxPolicy(20);
    assertEquals(Money.of(2.00), t.taxOn(Money.of(10.00)));
  }

  @Test
  void fixed_rate_tax_zero_percent() {
    TaxPolicy t = new FixedRateTaxPolicy(0);
    assertEquals(Money.of(0.00), t.taxOn(Money.of(10.00)));
  }

  @Test
  void fixed_rate_tax_negative_percent_throws() {
    assertThrows(IllegalArgumentException.class, () -> new FixedRateTaxPolicy(-10));
  }

  @Test
  void fixed_rate_tax_get_percent() {
    TaxPolicy t = new FixedRateTaxPolicy(10);
    assertEquals(10, t.getPercent());
  }

  // ========== PricingService Tests ==========

  @Test
  void pricing_pipeline_no_discount() {
    var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
    var pr = pricing.price(Money.of(10.00));
    assertEquals(Money.of(10.00), pr.subtotal());
    assertEquals(Money.of(0.00), pr.discount());
    assertEquals(Money.of(1.00), pr.tax());
    assertEquals(Money.of(11.00), pr.total());
  }

  @Test
  void pricing_pipeline_with_loyalty_discount() {
    var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
    var pr = pricing.price(Money.of(7.80));
    assertEquals(Money.of(7.80), pr.subtotal());
    assertEquals(Money.of(0.39), pr.discount());
    assertEquals(Money.of(0.74), pr.tax());
    assertEquals(Money.of(8.15), pr.total());
  }

  @Test
  void pricing_pipeline_with_fixed_coupon() {
    var pricing = new PricingService(new FixedCouponDiscount(Money.of(1.00)), new FixedRateTaxPolicy(10));
    var pr = pricing.price(Money.of(3.30));
    assertEquals(Money.of(3.30), pr.subtotal());
    assertEquals(Money.of(1.00), pr.discount());
    assertEquals(Money.of(0.23), pr.tax());
    assertEquals(Money.of(2.53), pr.total());
  }

  @Test
  void pricing_service_null_discount_policy_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new PricingService(null, new FixedRateTaxPolicy(10)));
  }

  @Test
  void pricing_service_null_tax_policy_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new PricingService(new NoDiscount(), null));
  }

  // ========== ReceiptPrinter Tests ==========

  @Test
  void receipt_printer_formats_correctly() {
    ReceiptPrinter printer = new ReceiptPrinter();
    PricingResult pr = new PricingResult(
        Money.of(7.80),
        Money.of(0.39),
        Money.of(0.74),
        Money.of(8.15)
    );
    String receipt = printer.format("LAT+L", 2, pr, 10);
    assertTrue(receipt.contains("Order (LAT+L) x2"));
    assertTrue(receipt.contains("Subtotal: 7.80"));
    assertTrue(receipt.contains("Discount: -0.39"));
    assertTrue(receipt.contains("Tax (10%): 0.74"));
    assertTrue(receipt.contains("Total: 8.15"));
  }

  @Test
  void receipt_printer_omits_zero_discount() {
    ReceiptPrinter printer = new ReceiptPrinter();
    PricingResult pr = new PricingResult(
        Money.of(10.00),
        Money.of(0.00),
        Money.of(1.00),
        Money.of(11.00)
    );
    String receipt = printer.format("ESP", 1, pr, 10);
    assertFalse(receipt.contains("Discount:"));
    assertTrue(receipt.contains("Subtotal: 10.00"));
    assertTrue(receipt.contains("Tax (10%): 1.00"));
    assertTrue(receipt.contains("Total: 11.00"));
  }

  @Test
  void receipt_printer_format_pricing_with_discount() {
    ReceiptPrinter printer = new ReceiptPrinter();
    PricingResult pr = new PricingResult(
        Money.of(7.80),
        Money.of(0.39),
        Money.of(0.74),
        Money.of(8.15)
    );
    String pricing = printer.formatPricing(pr, 10);
    assertTrue(pricing.contains("Subtotal: 7.80"));
    assertTrue(pricing.contains("Discount: -0.39"));
    assertTrue(pricing.contains("Tax (10%): 0.74"));
    assertTrue(pricing.contains("Total: 8.15"));
  }

  @Test
  void receipt_printer_format_pricing_without_discount() {
    ReceiptPrinter printer = new ReceiptPrinter();
    PricingResult pr = new PricingResult(
        Money.of(10.00),
        Money.of(0.00),
        Money.of(1.00),
        Money.of(11.00)
    );
    String pricing = printer.formatPricing(pr, 10);
    assertFalse(pricing.contains("Discount:"));
    assertTrue(pricing.contains("Subtotal: 10.00"));
    assertTrue(pricing.contains("Tax (10%): 1.00"));
    assertTrue(pricing.contains("Total: 11.00"));
  }

  // ========== CheckoutService Integration Tests ==========

  @Test
  void checkout_service_no_discount() {
    var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
    var printer = new ReceiptPrinter();
    var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);

    String receipt = checkout.checkout("ESP+SHOT+OAT", 1);
    assertTrue(receipt.contains("Order (ESP+SHOT+OAT) x1"));
    assertTrue(receipt.contains("Subtotal: 3.80"));
    assertTrue(receipt.contains("Tax (10%): 0.38"));
    assertTrue(receipt.contains("Total: 4.18"));
  }

  @Test
  void checkout_service_with_loyalty_discount() {
    var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
    var printer = new ReceiptPrinter();
    var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);

    String receipt = checkout.checkout("LAT+L", 2);
    assertTrue(receipt.contains("Subtotal: 7.80"));
    assertTrue(receipt.contains("Discount: -0.39"));
    assertTrue(receipt.contains("Tax (10%): 0.74"));
    assertTrue(receipt.contains("Total: 8.15"));
  }

  @Test
  void checkout_service_with_fixed_coupon() {
    var pricing = new PricingService(new FixedCouponDiscount(Money.of(1.00)), new FixedRateTaxPolicy(10));
    var printer = new ReceiptPrinter();
    var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);

    String receipt = checkout.checkout("ESP+SHOT", 0); // qty 0 clamped to 1
    assertTrue(receipt.contains("Order (ESP+SHOT) x1"));
    assertTrue(receipt.contains("Subtotal: 3.30"));
    assertTrue(receipt.contains("Discount: -1.00"));
    assertTrue(receipt.contains("Tax (10%): 0.23"));
    assertTrue(receipt.contains("Total: 2.53"));
  }

  @Test
  void checkout_service_null_factory_throws() {
    var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
    var printer = new ReceiptPrinter();
    assertThrows(IllegalArgumentException.class,
        () -> new CheckoutService(null, pricing, printer, 10));
  }

  @Test
  void checkout_service_null_pricing_throws() {
    var printer = new ReceiptPrinter();
    assertThrows(IllegalArgumentException.class,
        () -> new CheckoutService(new ProductFactory(), null, printer, 10));
  }

  @Test
  void checkout_service_null_printer_throws() {
    var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
    assertThrows(IllegalArgumentException.class,
        () -> new CheckoutService(new ProductFactory(), pricing, null, 10));
  }
}
