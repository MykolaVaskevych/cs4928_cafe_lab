package com.cafepos;

import com.cafepos.catalog.Priced;
import com.cafepos.checkout.CheckoutService;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InteractiveCLITest {

  // Test that CLI components work together correctly

  @Test
  void cli_tax_policy_integration() {
    // CLI uses FixedRateTaxPolicy(10)
    TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
    assertEquals(10, taxPolicy.getPercent());
    assertEquals(Money.of(1.00), taxPolicy.taxOn(Money.of(10.00)));
  }

  @Test
  void cli_discount_no_discount_option() {
    DiscountPolicy policy = new NoDiscount();
    assertEquals(Money.zero(), policy.discountOf(Money.of(10.00)));
  }

  @Test
  void cli_discount_loyalty_5_percent_option() {
    DiscountPolicy policy = new LoyaltyPercentDiscount(5);
    assertEquals(Money.of(0.50), policy.discountOf(Money.of(10.00)));
  }

  @Test
  void cli_discount_loyalty_10_percent_option() {
    DiscountPolicy policy = new LoyaltyPercentDiscount(10);
    assertEquals(Money.of(1.00), policy.discountOf(Money.of(10.00)));
  }

  @Test
  void cli_discount_fixed_coupon_1_dollar_option() {
    DiscountPolicy policy = new FixedCouponDiscount(Money.of(1.00));
    assertEquals(Money.of(1.00), policy.discountOf(Money.of(10.00)));
  }

  @Test
  void cli_discount_fixed_coupon_2_dollar_option() {
    DiscountPolicy policy = new FixedCouponDiscount(Money.of(2.00));
    assertEquals(Money.of(2.00), policy.discountOf(Money.of(10.00)));
  }

  @Test
  void cli_pricing_service_integration_no_discount() {
    TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
    DiscountPolicy discountPolicy = new NoDiscount();
    PricingService pricingService = new PricingService(discountPolicy, taxPolicy);

    Money subtotal = Money.of(10.00);
    PricingResult result = pricingService.price(subtotal);

    assertEquals(Money.of(10.00), result.subtotal());
    assertEquals(Money.of(0.00), result.discount());
    assertEquals(Money.of(1.00), result.tax());
    assertEquals(Money.of(11.00), result.total());
  }

  @Test
  void cli_pricing_service_integration_with_discount() {
    TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
    DiscountPolicy discountPolicy = new LoyaltyPercentDiscount(5);
    PricingService pricingService = new PricingService(discountPolicy, taxPolicy);

    Money subtotal = Money.of(7.80); // Latte Large x2
    PricingResult result = pricingService.price(subtotal);

    assertEquals(Money.of(7.80), result.subtotal());
    assertEquals(Money.of(0.39), result.discount());
    assertEquals(Money.of(0.74), result.tax());
    assertEquals(Money.of(8.15), result.total());
  }

  @Test
  void cli_receipt_printer_formats_with_discount() {
    ReceiptPrinter printer = new ReceiptPrinter();
    PricingResult result = new PricingResult(
        Money.of(7.80),
        Money.of(0.39),
        Money.of(0.74),
        Money.of(8.15)
    );

    String receipt = printer.format("LAT+L", 2, result, 10);

    assertTrue(receipt.contains("Order (LAT+L) x2"));
    assertTrue(receipt.contains("Subtotal: 7.80"));
    assertTrue(receipt.contains("Discount: -0.39"));
    assertTrue(receipt.contains("Tax (10%): 0.74"));
    assertTrue(receipt.contains("Total: 8.15"));
  }

  @Test
  void cli_receipt_printer_formats_without_discount() {
    ReceiptPrinter printer = new ReceiptPrinter();
    PricingResult result = new PricingResult(
        Money.of(10.00),
        Money.of(0.00),
        Money.of(1.00),
        Money.of(11.00)
    );

    String receipt = printer.format("ESP", 1, result, 10);

    assertTrue(receipt.contains("Order (ESP) x1"));
    assertTrue(receipt.contains("Subtotal: 10.00"));
    assertFalse(receipt.contains("Discount:"));
    assertTrue(receipt.contains("Tax (10%): 1.00"));
    assertTrue(receipt.contains("Total: 11.00"));
  }

  @Test
  void cli_full_flow_with_factory_and_pricing() {
    // Simulate CLI flow: create product, apply discount, calculate price
    ProductFactory factory = new ProductFactory();
    var product = factory.create("LAT+L");

    // Get price and calculate for quantity 2
    Money itemPrice = (product instanceof Priced p) ? p.price() : product.basePrice();
    Money subtotal = itemPrice.multiply(2);
    assertEquals(Money.of(7.80), subtotal);

    // Apply discount via PricingService
    TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
    DiscountPolicy discountPolicy = new LoyaltyPercentDiscount(5);
    PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
    PricingResult result = pricingService.price(subtotal);

    // Verify final receipt values
    assertEquals(Money.of(8.15), result.total());

    // Format receipt
    ReceiptPrinter printer = new ReceiptPrinter();
    String receipt = printer.format("LAT+L", 2, result, taxPolicy.getPercent());

    assertTrue(receipt.contains("Total: 8.15"));
  }

  @Test
  void cli_order_with_observers_and_pricing() {
    // Test that Order can work with the pricing system
    Order order = new Order(OrderIds.next());
    ProductFactory factory = new ProductFactory();

    var product = factory.create("ESP+SHOT+OAT");
    order.addItem(new LineItem(product, 1));

    Money subtotal = order.subtotal();
    assertEquals(Money.of(3.80), subtotal);

    // Apply pricing
    TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
    DiscountPolicy discountPolicy = new NoDiscount();
    PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
    PricingResult result = pricingService.price(subtotal);

    assertEquals(Money.of(4.18), result.total());
  }
}
