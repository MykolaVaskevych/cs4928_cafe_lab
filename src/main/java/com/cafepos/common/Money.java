package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/*
 *
 *
 *
 *
 *
 *
 *
 *
 */

public final class Money implements Comparable<Money> {
  private final BigDecimal amount;

  private Money(BigDecimal a) {
    if (a == null) {
      throw new IllegalArgumentException("amount required");
    }

    if (a.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("amount cant be negative");
    }

    this.amount = a.setScale(2, RoundingMode.HALF_UP);
  }

  public static Money of(double value) {
    return new Money(BigDecimal.valueOf(value));
  }

  public static Money zero() {
    return new Money(BigDecimal.ZERO);
  }

  public Money add(Money other) {
    if (other == null) {
      throw new IllegalArgumentException("add(Money other) -> other cant be null");
    }
    return new Money(this.amount.add(other.amount));
  }

  public Money multiply(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("quantity cant be < 0");
    }
    return new Money(this.amount.multiply(new BigDecimal(quantity)));
  }

  public Money subtract(Money other) {
    if (other == null) {
      throw new IllegalArgumentException("subtract(Money other) -> other cant be null");
    }
    BigDecimal result = this.amount.subtract(other.amount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("result cant be negative");
    }
    return new Money(result);
  }

  @Override
  public int compareTo(Money other) {
    return this.amount.compareTo(other.amount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Money money = (Money) o;
    return amount.equals(money.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount);
  }

  @Override
  public String toString() {
    return amount.toString();
  }
}
