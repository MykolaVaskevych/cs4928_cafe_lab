package com.cafepos.pricing;

import com.cafepos.common.Money;

public record PricingResult(Money subtotal, Money discount, Money tax, Money total) {
}
