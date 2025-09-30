package com.cafepos.domain;

import com.cafepos.common.Money;
import com.cafepos.payment.PaymentStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Order {

  private final long id;
  private final List<LineItem> items = new ArrayList<>();

  public Order(long id) {
    this.id = id;
  }

  public long id() {
    return id;
  }

  public void addItem(LineItem li) {
    if (li == null) {
      throw new IllegalArgumentException("lineItem required");
    }
    items.add(li);
  }

  public List<LineItem> items() {
    return Collections.unmodifiableList(items);
  }

  public Money subtotal() {
    return items.stream()
        .map(LineItem::lineTotal)
        .reduce(Money.zero(), Money::add);
  }

  public Money taxAtPercent(int percent) {
    if (percent < 0) {
      throw new IllegalArgumentException("percent cannot be negative");
    }
    BigDecimal subtotalAmount = new BigDecimal(subtotal().toString());
    BigDecimal taxAmount = subtotalAmount
        .multiply(new BigDecimal(percent))
        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    return Money.of(taxAmount.doubleValue());
  }

  public Money totalWithTax(int percent) {
    return subtotal().add(taxAtPercent(percent));
  }

  public void pay(PaymentStrategy strategy) {
    if (strategy == null) {
      throw new IllegalArgumentException("strategy required");
    }
    strategy.pay(this);
  }
}
