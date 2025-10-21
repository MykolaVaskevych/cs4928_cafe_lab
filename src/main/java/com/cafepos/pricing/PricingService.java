package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

public final class PricingService {
  private final DiscountPolicy discountPolicy;
  private final TaxPolicy taxPolicy;

  public PricingService(DiscountPolicy discountPolicy, TaxPolicy taxPolicy) {
    if (discountPolicy == null) {
      throw new IllegalArgumentException("discountPolicy required");
    }
    if (taxPolicy == null) {
      throw new IllegalArgumentException("taxPolicy required");
    }
    this.discountPolicy = discountPolicy;
    this.taxPolicy = taxPolicy;
  }

  public PricingResult price(Money subtotal) {
    Money discount = discountPolicy.discountOf(subtotal);
    Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
    if (discounted.asBigDecimal().signum() < 0) {
      discounted = Money.zero();
    }
    Money tax = taxPolicy.taxOn(discounted);
    Money total = discounted.add(tax);
    return new PricingResult(subtotal, discount, tax, total);
  }
}
