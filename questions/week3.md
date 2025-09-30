# Week 3 Lab - Strategy Pattern Reflection

## Reflection Questions

### 1. Point to one if/else you avoided in Order by introducing PaymentStrategy?

we avoided having an if/else  in the `Order` class to handle different payment methods. Without the Strategy pattern, the `Order.pay()` method would need to check the payment type and handle each case differently.

Instead, the Strategy pattern delegates the payment behavior to the concrete strategy implementation via polymorphism in `Order.java` file.

### 2. Show (1–2 sentences) how the same Order can be paid by different strategies in the demo without changing Order code?

In `Week3Demo.java`,

we created two orders with identical items and totals. Order #1001 is paid using by `new CashPayment()` and Order #1002 is paid using `new CardPayment("1234567812341234")`.

The `Order.pay()` method is unchanged no matter what and simply delegates to whichever `PaymentStrategy` is passed to it, which shows polymorphism where different payment behaviors are swapped at runtime without modifying the `Order` class.
