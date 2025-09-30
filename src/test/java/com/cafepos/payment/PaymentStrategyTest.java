package com.cafepos.payment;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PaymentStrategyTest {

  @Test
  void cash_payment_prints_confirmation() {
    var order = createTestOrder();
    var strategy = new CashPayment();

    String output = captureOutput(() -> strategy.pay(order));

    assertTrue(output.contains("[Cash]"));
    assertTrue(output.contains("9.35 EUR"));
  }

  @Test
  void card_payment_masks_card_number() {
    var order = createTestOrder();
    var strategy = new CardPayment("1234567812341234");

    String output = captureOutput(() -> strategy.pay(order));

    assertTrue(output.contains("[Card]"));
    assertTrue(output.contains("****1234"));
    assertFalse(output.contains("1234567812341234"));
  }

  @Test
  void card_payment_short_number_not_masked() {
    var order = createTestOrder();
    var strategy = new CardPayment("1234");

    String output = captureOutput(() -> strategy.pay(order));

    assertTrue(output.contains("1234"));
  }

  @Test
  void card_payment_null_fails() {
    assertThrows(IllegalArgumentException.class, () -> new CardPayment(null));
  }

  @Test
  void card_payment_blank_fails() {
    assertThrows(IllegalArgumentException.class, () -> new CardPayment(""));
  }

  @Test
  void wallet_payment_shows_wallet_id() {
    var order = createTestOrder();
    var strategy = new WalletPayment("alice-wallet-01");

    String output = captureOutput(() -> strategy.pay(order));

    assertTrue(output.contains("[Wallet]"));
    assertTrue(output.contains("alice-wallet-01"));
    assertTrue(output.contains("9.35 EUR"));
  }

  @Test
  void wallet_payment_null_fails() {
    assertThrows(IllegalArgumentException.class, () -> new WalletPayment(null));
  }

  @Test
  void wallet_payment_blank_fails() {
    assertThrows(IllegalArgumentException.class, () -> new WalletPayment(""));
  }

  // Helper methods
  private Order createTestOrder() {
    var p1 = new SimpleProduct("A", "Product A", Money.of(2.50));
    var p2 = new SimpleProduct("B", "Product B", Money.of(3.50));
    var order = new Order(1);
    order.addItem(new LineItem(p1, 2));
    order.addItem(new LineItem(p2, 1));
    return order;
  }

  private String captureOutput(Runnable action) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    try {
      action.run();
      return outputStream.toString();
    } finally {
      System.setOut(originalOut);
    }
  }
}
