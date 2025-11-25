# Video Presentation Code Snippets

This folder contains PNG images of code for the 8-minute final assessment video presentation.
All PNGs show **full classes or substantial code sections** for proper understanding.

## Command Pattern (Section 2.1 - Oliver)

**01_posremote_press_undo.png**
- File: `PosRemote.java` (full class)
- Shows: Command pattern invoker with slots array, press() executes commands, undo() uses history stack

## Adapter Pattern (Section 2.2 - Oliver)

**02_adapter.png**
- File: `LegacyPrinterAdapter.java` (full class)
- Shows: Adapter wrapping LegacyThermalPrinter to implement modern Printer interface

## Composite & Iterator Patterns (Section 2.3 - Oliver)

**03_menu_composite.png**
- File: `Menu.java` (class with all methods)
- Shows: Composite with children list, add/remove, iterator() returning CompositeIterator, print() recursion

## State Pattern (Section 2.4 - Oliver)

**04_orderfsm_full.png**
- File: `OrderFSM.java` (full class)
- Shows: FSM with state field, all 5 methods (pay, prepare, markReady, deliver, cancel) delegating to current state

**05_newstate_full.png**
- File: `NewState.java` (full class)
- Shows: Complete state implementation with all 5 transitions (pay→PREPARING, cancel→CANCELLED, others rejected)

## MVC & EventBus (Section 2.5 - Oliver)

**06_controller_full.png**
- File: `OrderController.java` (full class)
- Shows: MVC Controller with repo/checkout dependencies, createOrder(), addItem(), checkout() methods

**07_eventbus_full.png**
- File: `EventBus.java` (full class)
- Shows: Pub-sub with handlers map, on() for registration, emit() dispatching to all handlers

## Components & Connectors (Section 3.2 - Nick)

**08_repository_interface.png**
- File: `OrderRepository.java` (full interface)
- Shows: Domain repository interface with save() and findById() - DIP in action

**09_repository_implementation.png**
- File: `InMemoryOrderRepository.java` (full class)
- Shows: Infrastructure implementation using HashMap, implements domain interface

**10_wiring_full.png**
- File: `Wiring.java` (full class)
- Shows: DI composition root, Components record, createDefault() wiring repo→pricing→checkout

## Code Quality (Section 4.1 - Oliver)

**11_money_full.png**
- File: `Money.java` (full class implementation)
- Shows: Immutable value object with validation in constructor, add(), multiply(), subtract(), compareTo()

**12_order_full.png**
- File: `Order.java` (full class body)
- Shows: Domain entity with validation (addItem), observer pattern, subtotal calculation, pay() with strategy

## Test Examples (Section 4.3 - Oliver)

**13_test_orderfsm.png**
- File: `OrderFSMTest.java`
- Shows: `ready_state_cannot_be_cancelled()` test - verifies FSM prevents invalid transitions

**14_test_layered.png**
- File: `LayeredArchitectureTest.java`
- Shows: `checkoutService_createsReceiptFromOrder()` - E2E test through all layers (repo→service→receipt)

---

## File Summary

Total: 14 PNG files (~4.1MB)
- Command: 1 file
- Adapter: 1 file
- Composite/Iterator: 1 file
- State: 2 files
- MVC/EventBus: 2 files
- Architecture: 3 files
- Code Quality: 2 files
- Tests: 2 files

All images show **complete implementations** with full context for video presentation.
