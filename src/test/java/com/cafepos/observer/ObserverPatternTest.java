package com.cafepos.observer;

import com.cafepos.domain.*;
import com.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ObserverPatternTest {

  @Test
  void kitchenDisplay_receivesItemAddedNotification() {
    var order = new Order(6001L);
    var kitchen = new KitchenDisplay();
    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    order.register(kitchen);
    order.addItem(new LineItem(new ProductFactory().create("ESP"), 1));

    System.setOut(originalOut);
    assertTrue(outputStream.toString().contains("[Kitchen] Order #6001"));
    assertTrue(outputStream.toString().contains("item added"));
  }

  @Test
  void customerNotifier_receivesPaymentNotification() {
    var order = new Order(6002L);
    var notifier = new CustomerNotifier();
    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    order.register(notifier);
    order.pay(new com.cafepos.payment.CashPayment());

    System.setOut(originalOut);
    assertTrue(outputStream.toString().contains("[Customer] Dear customer, your Order #6002"));
    assertTrue(outputStream.toString().contains("paid"));
  }

  @Test
  void deliveryDesk_receivesReadyNotification() {
    var order = new Order(6003L);
    var delivery = new DeliveryDesk();
    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    order.register(delivery);
    order.markReady();

    System.setOut(originalOut);
    assertTrue(outputStream.toString().contains("[Delivery] Order #6003"));
    assertTrue(outputStream.toString().contains("ready"));
  }

  @Test
  void multipleObservers_allReceiveNotifications() {
    var order = new Order(6004L);
    var kitchen = new KitchenDisplay();
    var notifier = new CustomerNotifier();
    var delivery = new DeliveryDesk();
    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    order.register(kitchen);
    order.register(notifier);
    order.register(delivery);
    order.addItem(new LineItem(new ProductFactory().create("ESP"), 1));

    System.setOut(originalOut);
    String output = outputStream.toString();
    // KitchenDisplay and CustomerNotifier respond to "itemAdded"
    assertTrue(output.contains("[Kitchen]"));
    assertTrue(output.contains("[Customer]"));
    // DeliveryDesk only responds to "ready", so it won't print for "itemAdded"
    // Let's test with markReady() event instead

    outputStream.reset();
    System.setOut(new PrintStream(outputStream));
    order.markReady();
    System.setOut(originalOut);

    String readyOutput = outputStream.toString();
    assertTrue(readyOutput.contains("[Delivery]"));
    assertTrue(readyOutput.contains("[Customer]")); // CustomerNotifier responds to all events
  }

  @Test
  void unregister_stopsReceivingNotifications() {
    var order = new Order(6005L);
    var kitchen = new KitchenDisplay();
    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();

    order.register(kitchen);
    order.unregister(kitchen);

    System.setOut(new PrintStream(outputStream));
    order.addItem(new LineItem(new ProductFactory().create("ESP"), 1));
    System.setOut(originalOut);

    assertEquals("", outputStream.toString().trim());
  }

  @Test
  void observer_cannotRegisterTwice() {
    var order = new Order(6006L);
    var kitchen = new KitchenDisplay();

    order.register(kitchen);
    order.register(kitchen);

    var originalOut = System.out;
    var outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    order.addItem(new LineItem(new ProductFactory().create("ESP"), 1));

    System.setOut(originalOut);
    String output = outputStream.toString();
    // Should only receive one notification, not two
    assertEquals(1, output.split("\\[Kitchen\\]").length - 1);
  }
}
