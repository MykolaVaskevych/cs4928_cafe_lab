package com.cafepos.demo;

import com.cafepos.command.*;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CardPayment;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import vendor.legacy.LegacyThermalPrinter;

public final class Week8Demo {

  public static void main(String[] args) {
    System.out.println("========================================");
    System.out.println("Week 8 Demo: Command + Adapter Patterns");
    System.out.println("========================================\n");

    // Part A: Command Pattern Demo
    System.out.println("--- Part A: Command Pattern ---");
    System.out.println("Demonstrating button-based order entry with undo\n");

    Order order = new Order(OrderIds.next());
    OrderService service = new OrderService(order);

    PosRemote remote = new PosRemote(3);
    remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT+OAT", 1));
    remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
    remote.setSlot(2, new PayOrderCommand(service, new CardPayment("1234567890123456"), 10));

    System.out.println("Pressing button 0 (Add Espresso+Shot+Oat):");
    remote.press(0);

    System.out.println("\nPressing button 1 (Add 2x Large Latte):");
    remote.press(1);

    System.out.println("\nOops! Undo last action:");
    remote.undo();

    System.out.println("\nPressing button 1 again (Add 2x Large Latte):");
    remote.press(1);

    System.out.println("\nPressing button 2 (Process Payment):");
    remote.press(2);

    // Part B: Adapter Pattern Demo
    System.out.println("\n\n--- Part B: Adapter Pattern ---");
    System.out.println("Printing receipt to legacy thermal printer\n");

    String receipt = String.format(
        "Order #%d\n" +
        "Espresso + Extra Shot + Oat Milk x1\n" +
        "Latte (Large) x2\n" +
        "Subtotal: %s\n" +
        "Tax (10%%): %s\n" +
        "Total: %s",
        order.id(),
        order.subtotal(),
        order.taxAtPercent(10),
        order.totalWithTax(10)
    );

    Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
    printer.print(receipt);

    System.out.println("\n========================================");
    System.out.println("Summary:");
    System.out.println(" - Command pattern decouples UI (buttons) from business logic");
    System.out.println(" - Adapter pattern integrates legacy printer without modifying domain");
    System.out.println("========================================");
  }
}
