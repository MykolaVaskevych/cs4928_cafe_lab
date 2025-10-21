package com.cafepos.checkout;

import com.cafepos.catalog.Priced;
import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.PricingResult;
import com.cafepos.pricing.PricingService;
import com.cafepos.pricing.ReceiptPrinter;

public final class CheckoutService {
  private final ProductFactory factory;
  private final PricingService pricing;
  private final ReceiptPrinter printer;
  private final int taxPercent;

  public CheckoutService(ProductFactory factory, PricingService pricing, ReceiptPrinter printer, int taxPercent) {
    if (factory == null) {
      throw new IllegalArgumentException("factory required");
    }
    if (pricing == null) {
      throw new IllegalArgumentException("pricing required");
    }
    if (printer == null) {
      throw new IllegalArgumentException("printer required");
    }
    this.factory = factory;
    this.pricing = pricing;
    this.printer = printer;
    this.taxPercent = taxPercent;
  }

  public String checkout(String recipe, int qty) {
    Product product = factory.create(recipe);
    if (qty <= 0) qty = 1;

    Money unit = (product instanceof Priced p) ? p.price() : product.basePrice();
    Money subtotal = unit.multiply(qty);

    PricingResult result = pricing.price(subtotal);
    return printer.format(recipe, qty, result, taxPercent);
  }
}
