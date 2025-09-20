package com.cafepos;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

  @Test
  void money_creation() {
    Money m = Money.of(2.50);
    assertEquals("2.50", m.toString());
  }

  @Test
  void money_zero() {
    Money m = Money.zero();
    assertEquals("0.00", m.toString());
  }

  @Test
  void money_add() {
    Money m1 = Money.of(2.00);
    Money m2 = Money.of(3.00);
    Money result = m1.add(m2);
    assertEquals(Money.of(5.00), result);
  }

  @Test
  void money_multiply() {
    Money m = Money.of(2.50);
    Money result = m.multiply(3);
    assertEquals(Money.of(7.50), result);
  }

  @Test
  void money_rounding() {
    Money m = Money.of(2.556);
    assertEquals("2.56", m.toString());
  }

  @Test
  void money_no_negative() {
    assertThrows(IllegalArgumentException.class, () -> Money.of(-5.00));
  }
}
