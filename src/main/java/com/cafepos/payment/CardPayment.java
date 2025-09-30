package com.cafepos.payment;

import com.cafepos.domain.Order;

public final class CardPayment implements PaymentStrategy {

  private final String cardNumber;

  public CardPayment(String cardNumber) {
    if (cardNumber == null || cardNumber.isBlank()) {
      throw new IllegalArgumentException("cardNumber required");
    }
    this.cardNumber = cardNumber;
  }

  @Override
  public void pay(Order order) {
    String maskedCard = maskCardNumber(cardNumber);
    System.out.println("[Card] Customer paid " + order.totalWithTax(10) + " EUR with card " + maskedCard);
  }

  private String maskCardNumber(String cardNumber) {
    if (cardNumber.length() <= 4) {
      return cardNumber;
    }
    String lastFour = cardNumber.substring(cardNumber.length() - 4);
    return "****" + lastFour;
  }
}
