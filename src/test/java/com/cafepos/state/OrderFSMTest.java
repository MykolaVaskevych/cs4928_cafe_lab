package com.cafepos.state;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OrderFSMTest {

  @Test
  void order_fsm_happy_path() {
    OrderFSM fsm = new OrderFSM();

    assertEquals("NEW", fsm.status());

    fsm.pay();
    assertEquals("PREPARING", fsm.status());

    fsm.markReady();
    assertEquals("READY", fsm.status());

    fsm.deliver();
    assertEquals("DELIVERED", fsm.status());
  }

  @Test
  void new_state_cannot_prepare_before_pay() {
    OrderFSM fsm = new OrderFSM();

    fsm.prepare();

    assertEquals("NEW", fsm.status());
  }

  @Test
  void new_state_can_be_cancelled() {
    OrderFSM fsm = new OrderFSM();

    fsm.cancel();

    assertEquals("CANCELLED", fsm.status());
  }

  @Test
  void preparing_state_can_be_cancelled() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();

    fsm.cancel();

    assertEquals("CANCELLED", fsm.status());
  }

  @Test
  void ready_state_cannot_be_cancelled() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();
    fsm.markReady();

    fsm.cancel();

    assertEquals("READY", fsm.status());
  }

  @Test
  void preparing_state_cannot_deliver_directly() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();

    fsm.deliver();

    assertEquals("PREPARING", fsm.status());
  }

  @Test
  void delivered_state_is_terminal() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();
    fsm.markReady();
    fsm.deliver();

    fsm.pay();
    assertEquals("DELIVERED", fsm.status());

    fsm.prepare();
    assertEquals("DELIVERED", fsm.status());

    fsm.markReady();
    assertEquals("DELIVERED", fsm.status());

    fsm.cancel();
    assertEquals("DELIVERED", fsm.status());
  }

  @Test
  void cancelled_state_is_terminal() {
    OrderFSM fsm = new OrderFSM();
    fsm.cancel();

    fsm.pay();
    assertEquals("CANCELLED", fsm.status());

    fsm.prepare();
    assertEquals("CANCELLED", fsm.status());

    fsm.markReady();
    assertEquals("CANCELLED", fsm.status());

    fsm.deliver();
    assertEquals("CANCELLED", fsm.status());
  }

  @Test
  void preparing_state_rejects_duplicate_pay() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();

    fsm.pay();

    assertEquals("PREPARING", fsm.status());
  }

  @Test
  void ready_state_rejects_duplicate_markReady() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();
    fsm.markReady();

    fsm.markReady();

    assertEquals("READY", fsm.status());
  }

  @Test
  void delivered_state_rejects_duplicate_deliver() {
    OrderFSM fsm = new OrderFSM();
    fsm.pay();
    fsm.markReady();
    fsm.deliver();

    fsm.deliver();

    assertEquals("DELIVERED", fsm.status());
  }

  @Test
  void new_to_delivered_requires_all_steps() {
    OrderFSM fsm = new OrderFSM();

    // Try to skip steps
    fsm.deliver();
    assertEquals("NEW", fsm.status());

    fsm.markReady();
    assertEquals("NEW", fsm.status());

    // Proper flow
    fsm.pay();
    assertEquals("PREPARING", fsm.status());

    fsm.markReady();
    assertEquals("READY", fsm.status());

    fsm.deliver();
    assertEquals("DELIVERED", fsm.status());
  }

  @Test
  void multiple_fsm_instances_independent() {
    OrderFSM fsm1 = new OrderFSM();
    OrderFSM fsm2 = new OrderFSM();

    fsm1.pay();
    assertEquals("PREPARING", fsm1.status());
    assertEquals("NEW", fsm2.status());

    fsm2.cancel();
    assertEquals("PREPARING", fsm1.status());
    assertEquals("CANCELLED", fsm2.status());
  }
}
