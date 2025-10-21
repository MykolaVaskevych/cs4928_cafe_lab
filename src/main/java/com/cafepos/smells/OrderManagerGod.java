package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

public class OrderManagerGod {
  // SMELL: Global/Static State - mutable static fields are hard to test and not thread-safe
  public static int TAX_PERCENT = 10;
  public static String LAST_DISCOUNT_CODE = null;

  // SMELL: God Class - this class knows too much (creation, pricing, discounts, tax, payment, printing)
  // SMELL: Long Method - 60+ lines doing multiple unrelated responsibilities
  public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
    ProductFactory factory = new ProductFactory();
    Product product = factory.create(recipe);

    Money unitPrice;
    try {
      var priced = product instanceof com.cafepos.catalog.Priced p ? p.price() : product.basePrice();
      unitPrice = priced;
    } catch (Exception e) {
      unitPrice = product.basePrice();
    }

    if (qty <= 0) qty = 1;
    Money subtotal = unitPrice.multiply(qty);

    // SMELL: Primitive Obsession - using string codes instead of proper discount types/objects
    // SMELL: Feature Envy - discount logic should belong to a DiscountPolicy, not here
    // SMELL: Shotgun Surgery risk - adding a new discount requires editing this method
    Money discount = Money.zero();
    if (discountCode != null) {
      if (discountCode.equalsIgnoreCase("LOYAL5")) {
        // SMELL: Duplicated Logic - BigDecimal math scattered inline (magic numbers 5, 100)
        discount = Money.of(subtotal.asBigDecimal()
            .multiply(java.math.BigDecimal.valueOf(5))
            .divide(java.math.BigDecimal.valueOf(100)));
      } else if (discountCode.equalsIgnoreCase("COUPON1")) {
        discount = Money.of(1.00); // SMELL: Magic number
      } else if (discountCode.equalsIgnoreCase("NONE")) {
        discount = Money.zero();
      } else {
        discount = Money.zero();
      }
      LAST_DISCOUNT_CODE = discountCode; // SMELL: Global state mutation
    }

    // SMELL: Duplicated Logic - more inline BigDecimal manipulation
    Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
    if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();

    // SMELL: Feature Envy - tax calculation logic should belong to a TaxPolicy
    // SMELL: Shotgun Surgery risk - changing tax rules requires editing this method
    // SMELL: Duplicated Logic - more BigDecimal math inline (accessing global TAX_PERCENT)
    var tax = Money.of(discounted.asBigDecimal()
        .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
        .divide(java.math.BigDecimal.valueOf(100)));
    var total = discounted.add(tax);

    // SMELL: Primitive Obsession - using string for payment type instead of PaymentStrategy
    // SMELL: Shotgun Surgery risk - adding new payment methods requires editing this switch
    // SMELL: Feature Envy - payment logic should use existing PaymentStrategy from Week 3
    if (paymentType != null) {
      if (paymentType.equalsIgnoreCase("CASH")) {
        System.out.println("[Cash] Customer paid " + total + " EUR");
      } else if (paymentType.equalsIgnoreCase("CARD")) {
        System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
      } else if (paymentType.equalsIgnoreCase("WALLET")) {
        System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
      } else {
        System.out.println("[UnknownPayment] " + total);
      }
    }

    // SMELL: Feature Envy - receipt formatting should belong to a ReceiptPrinter
    // SMELL: God Class - this method shouldn't be responsible for formatting output
    StringBuilder receipt = new StringBuilder();
    receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
    receipt.append("Subtotal: ").append(subtotal).append("\n");
    if (discount.asBigDecimal().signum() > 0) {
      receipt.append("Discount: -").append(discount).append("\n");
    }
    receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
    receipt.append("Total: ").append(total);

    String out = receipt.toString();
    if (printReceipt) {
      System.out.println(out);
    }
    return out;
  }
}
