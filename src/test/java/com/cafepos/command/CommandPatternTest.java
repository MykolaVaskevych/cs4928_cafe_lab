package com.cafepos.command;

import static org.junit.jupiter.api.Assertions.*;

import com.cafepos.common.Money;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CardPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandPatternTest {

  private Order order;
  private OrderService service;

  @BeforeEach
  void setUp() {
    order = new Order(OrderIds.next());
    service = new OrderService(order);
  }

  @Test
  void addItemCommand_execute_addsItemToOrder() {
    AddItemCommand cmd = new AddItemCommand(service, "ESP", 1);

    cmd.execute();

    assertEquals(1, order.items().size());
  }

  @Test
  void addItemCommand_undo_removesLastItem() {
    AddItemCommand cmd = new AddItemCommand(service, "ESP", 1);
    cmd.execute();
    assertEquals(1, order.items().size());

    cmd.undo();

    assertEquals(0, order.items().size());
  }

  @Test
  void posRemote_pressSlot_executesCommand() {
    PosRemote remote = new PosRemote(2);
    remote.setSlot(0, new AddItemCommand(service, "LAT", 1));

    remote.press(0);

    assertEquals(1, order.items().size());
  }

  @Test
  void posRemote_undo_reversesLastCommand() {
    PosRemote remote = new PosRemote(2);
    remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
    remote.setSlot(1, new AddItemCommand(service, "LAT", 1));

    remote.press(0);
    remote.press(1);
    assertEquals(2, order.items().size());

    remote.undo();
    assertEquals(1, order.items().size());
  }

  @Test
  void macroCommand_execute_runsAllStepsInOrder() {
    Command step1 = new AddItemCommand(service, "ESP", 1);
    Command step2 = new AddItemCommand(service, "LAT", 1);
    MacroCommand macro = new MacroCommand(step1, step2);

    macro.execute();

    assertEquals(2, order.items().size());
  }

  @Test
  void macroCommand_undo_reversesStepsInReverseOrder() {
    Command step1 = new AddItemCommand(service, "ESP", 1);
    Command step2 = new AddItemCommand(service, "LAT", 1);
    MacroCommand macro = new MacroCommand(step1, step2);

    macro.execute();
    assertEquals(2, order.items().size());

    macro.undo();
    assertEquals(0, order.items().size());
  }

  @Test
  void integration_multipleCommandsWithPayment() {
    PosRemote remote = new PosRemote(3);
    remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
    remote.setSlot(1, new AddItemCommand(service, "LAT", 2));
    remote.setSlot(2, new PayOrderCommand(service, new CardPayment("1234"), 10));

    remote.press(0);
    remote.press(1);

    // Verify subtotal before payment
    Money subtotal = order.subtotal();
    assertTrue(subtotal.asBigDecimal().doubleValue() > 0);

    remote.press(2);

    // Verify order still has items after payment
    assertEquals(2, order.items().size());
  }

  @Test
  void orderService_removeLastItem_whenEmpty_doesNotThrow() {
    assertDoesNotThrow(() -> service.removeLastItem());
  }

  @Test
  void orderService_addItem_withQuantity_createsCorrectLineItem() {
    service.addItem("ESP", 2);

    assertEquals(1, order.items().size());
    assertEquals(2, order.items().get(0).quantity());
  }
}
