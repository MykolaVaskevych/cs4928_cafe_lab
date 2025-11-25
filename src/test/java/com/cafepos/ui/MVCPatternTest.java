package com.cafepos.ui;

import com.cafepos.domain.*;
import com.cafepos.infra.*;
import com.cafepos.app.CheckoutService;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class MVCPatternTest {

  @Test
  void orderController_createsNewOrder() {
    var components = Wiring.createDefault();
    var controller = new OrderController(components.repo(), components.checkout());

    long id = controller.createOrder(5001L);

    assertEquals(5001L, id);
    assertTrue(components.repo().findById(5001L).isPresent());
  }

  @Test
  void orderController_addsItemToOrder() {
    var components = Wiring.createDefault();
    var controller = new OrderController(components.repo(), components.checkout());

    controller.createOrder(5002L);
    controller.addItem(5002L, "ESP", 2);

    var order = components.repo().findById(5002L).get();
    assertEquals(1, order.items().size());
    assertEquals(2, order.items().get(0).quantity());
  }

  @Test
  void orderController_addsMultipleItems() {
    var components = Wiring.createDefault();
    var controller = new OrderController(components.repo(), components.checkout());

    controller.createOrder(5003L);
    controller.addItem(5003L, "ESP", 1);
    controller.addItem(5003L, "LAT", 2);
    controller.addItem(5003L, "ESP+SHOT", 1);

    var order = components.repo().findById(5003L).get();
    assertEquals(3, order.items().size());
  }

  @Test
  void orderController_checkoutCreatesReceipt() {
    var components = Wiring.createDefault();
    var controller = new OrderController(components.repo(), components.checkout());

    controller.createOrder(5004L);
    controller.addItem(5004L, "ESP", 1);
    String receipt = controller.checkout(5004L, 10);

    assertTrue(receipt.contains("Order #5004"));
    assertTrue(receipt.contains("Espresso"));
    assertTrue(receipt.contains("Total:"));
  }

  @Test
  void orderController_throwsWhenOrderNotFound() {
    var components = Wiring.createDefault();
    var controller = new OrderController(components.repo(), components.checkout());

    assertThrows(Exception.class, () -> controller.addItem(9999L, "ESP", 1));
  }

  @Test
  void consoleView_printsToStdout() {
    var view = new ConsoleView();
    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    view.print("Test message");

    System.setOut(originalOut);
    assertTrue(outputStream.toString().contains("Test message"));
  }

  @Test
  void mvc_integration_fullWorkflow() {
    var components = Wiring.createDefault();
    var controller = new OrderController(components.repo(), components.checkout());

    // Create order (Controller)
    controller.createOrder(5005L);

    // Add items (Controller)
    controller.addItem(5005L, "ESP+SHOT+OAT", 1);
    controller.addItem(5005L, "LAT+L", 2);

    // Checkout (Controller generates receipt)
    String receipt = controller.checkout(5005L, 10);

    // Verify receipt content (View would display this)
    assertTrue(receipt.contains("Order #5005"));
    assertTrue(receipt.contains("Espresso + Extra Shot + Oat Milk"));
    assertTrue(receipt.contains("Latte (Large)"));
    assertTrue(receipt.contains("x1"));
    assertTrue(receipt.contains("x2"));
  }
}
