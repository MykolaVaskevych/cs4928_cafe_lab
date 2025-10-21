package com.cafepos.demo;

import com.cafepos.checkout.CheckoutService;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import com.cafepos.smells.OrderManagerGod;

public final class Week6Demo {

  public static void main(String[] args) {
    System.out.println("========================================");
    System.out.println("Week 6 Refactoring Demo");
    System.out.println("Comparing Old (Smelly) vs New (Clean)");
    System.out.println("========================================\n");

    // Old behavior (smelly OrderManagerGod)
    String oldReceipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);

    // New behavior (clean CheckoutService with equivalent configuration)
    var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
    var printer = new ReceiptPrinter();
    var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);
    String newReceipt = checkout.checkout("LAT+L", 2);

    // Display results
    System.out.println("Old Receipt (OrderManagerGod):");
    System.out.println(oldReceipt);

    System.out.println("\n========================================\n");

    System.out.println("New Receipt (CheckoutService):");
    System.out.println(newReceipt);

    System.out.println("\n========================================\n");

    System.out.println("Receipts Match: " + oldReceipt.equals(newReceipt));

    if (oldReceipt.equals(newReceipt)) {
      System.out.println("\n SUCCESS: Refactoring preserved behavior!");
      System.out.println(" Removed global state (TAX_PERCENT, LAST_DISCOUNT_CODE)");
      System.out.println(" Extracted DiscountPolicy, TaxPolicy, PricingService, ReceiptPrinter");
      System.out.println(" Applied Dependency Injection (no more God Class)");
    } else {
      System.out.println("\n FAILURE: Receipts do not match!");
    }

    System.out.println("\n========================================");
  }
}
