# Week 4 Lab - Observer Pattern Reflection

## Reflection Questions

### 1. How does the Observer pattern improve decoupling in the Café POS system?

The Observer pattern introdices abstraction through the OrderObserver interface. it simplifies announces events like "itemAdded", "paid", or "ready".

This means that order has no knowldge about who is listening or what they do with the info provided, which results in loose coupling where core domain logic remains isolated from presentation or notification concerns.

### 2. Why is it beneficial that new observers can be added without modifying the Order class?

it follows open/closed principle whre the sytem is open for extension but closed for modification. if later we will want to  add something else, we can simply create a new class which would implement OrderObserver, and no need  to modify existing classes.

it  reduces risk of bugs. it makes system more maintainable and allows another teams to extend functionality without touching already tested code.

### 3. Can you think of a real-world system (outside cafés) where Observer is used (e.g., push notifications, GUIs)?

The observer pattern is very widely used in gui frameworks. also its common example in event event-driven systems like stock market tickers,where a lot of things react differently like price display, notification sending or auto trades triggers. Push notificatons also use observers, when user gets new message or status of order changes.
