# Design Patterns MCQ Preparation
## CS4928 Café POS Project - Comprehensive Review

**Total Questions: 100**

**Patterns Covered:**
- Decorator Pattern
- Factory Pattern
- Strategy Pattern
- Observer Pattern
- Command Pattern
- Adapter Pattern
- Composite Pattern
- Iterator Pattern
- State Pattern
- SOLID Principles
- Java Features

---

## QUESTIONS

### DECORATOR PATTERN (Questions 1-10)

**1. What is the primary purpose of the Decorator pattern?**
A) To create objects without specifying their exact class
B) To add responsibilities to objects dynamically without affecting other objects
C) To define a family of algorithms and make them interchangeable
D) To separate abstraction from implementation

**2. In the Decorator pattern, what must the decorator and the component share?**
A) The same parent class
B) The same interface or abstract class
C) The same constructor signature
D) The same package

**3. Which of the following is TRUE about the Decorator pattern?**
A) Decorators modify the original object
B) Decorators must be applied in a specific order
C) Decorators can be stacked infinitely
D) Decorators can only add one feature at a time

**4. In our café POS, `ExtraShot` is a decorator. What does it decorate?**
A) Order
B) LineItem
C) Product
D) Money

**5. What is a key difference between Decorator and Inheritance?**
A) Decorator adds behavior at compile-time, inheritance at runtime
B) Decorator adds behavior at runtime, inheritance at compile-time
C) Decorator is faster than inheritance
D) There is no difference

**6. How does the Decorator pattern relate to the Open/Closed Principle?**
A) It violates it by modifying existing classes
B) It supports it by allowing extension without modification
C) It has no relation to OCP
D) It replaces the need for OCP

**7. In our implementation, what does `ProductDecorator.basePrice()` return?**
A) The decorator's own price
B) The wrapped product's base price
C) The total price including all decorators
D) Zero

**8. Which method in our decorators calculates the total price?**
A) `basePrice()`
B) `price()`
C) `total()`
D) `calculate()`

**9. What pattern allows decorators to wrap other decorators?**
A) Chain of Responsibility
B) Composite
C) Recursive Composition
D) Nested Decoration

**10. What is a disadvantage of the Decorator pattern?**
A) Cannot add multiple decorators
B) Can result in many small classes
C) Modifies the original object
D) Violates encapsulation

---

### FACTORY PATTERN (Questions 11-20)

**11. What problem does the Factory pattern solve?**
A) How to traverse a collection
B) How to create objects without exposing creation logic
C) How to add behavior dynamically
D) How to manage object state

**12. In our `ProductFactory`, what does the `create("ESP+SHOT+OAT")` method return?**
A) A String
B) An array of Products
C) A single decorated Product
D) A ProductFactory

**13. Which pattern does Factory often work with to build complex objects?**
A) Decorator
B) Singleton
C) Observer
D) State

**14. What is the benefit of using Factory over `new` keyword directly?**
A) Faster object creation
B) Centralized creation logic
C) Uses less memory
D) Automatically garbage collects

**15. In our factory, what does the "+" symbol in recipes represent?**
A) Addition operation
B) Concatenation
C) Decorator combination
D) Factory method

**16. What type of Factory did we implement?**
A) Abstract Factory
B) Factory Method
C) Simple Factory
D) Builder

**17. How does Factory pattern support the Dependency Inversion Principle?**
A) Clients depend on concrete classes
B) Clients depend on abstractions, factory creates concrete types
C) Factory depends on clients
D) It doesn't support DIP

**18. What happens if an unknown recipe is passed to our factory?**
A) Returns null
B) Throws IllegalArgumentException
C) Creates a default product
D) Returns empty product

**19. Factory pattern is most useful when:**
A) Object creation is simple
B) Object creation logic is complex or varies
C) Only one type of object exists
D) Objects never change

**20. Which SOLID principle does Factory pattern primarily support?**
A) Single Responsibility Principle
B) Open/Closed Principle
C) Liskov Substitution Principle
D) Both A and B

---

### STRATEGY PATTERN (Questions 21-30)

**21. What is the main intent of the Strategy pattern?**
A) To create a family of algorithms and make them interchangeable
B) To add behavior dynamically to objects
C) To manage object lifecycle
D) To traverse collections

**22. In our POS, `PaymentStrategy` is implemented by which classes?**
A) Order, LineItem
B) CashPayment, CardPayment, WalletPayment
C) Product, Money
D) Factory, Decorator

**23. What method does `PaymentStrategy` interface define?**
A) `process()`
B) `pay()`
C) `execute()`
D) `charge()`

**24. Which pattern is Strategy most similar to?**
A) Decorator
B) State
C) Command
D) Factory

**25. How does Strategy differ from State pattern?**
A) Strategy changes behavior based on context, State changes context based on behavior
B) Strategy encapsulates algorithms, State encapsulates state-dependent behavior
C) There is no difference
D) Strategy uses inheritance, State uses composition

**26. What principle does Strategy pattern exemplify?**
A) Favor inheritance over composition
B) Favor composition over inheritance
C) Use global variables for flexibility
D) Hard-code algorithms for performance

**27. In `CardPayment`, what does `maskCardNumber()` demonstrate?**
A) Strategy pattern
B) Encapsulation and information hiding
C) Factory pattern
D) Observer pattern

**28. Which of these is a Strategy in our pricing system?**
A) `Order`
B) `DiscountPolicy`
C) `LineItem`
D) `Money`

**29. What is the benefit of using Strategy over conditional statements?**
A) Faster execution
B) Easier to add new strategies without modifying existing code
C) Uses less memory
D) No benefit

**30. Strategy pattern relates to which SOLID principle?**
A) Single Responsibility Principle
B) Open/Closed Principle
C) Interface Segregation Principle
D) All of the above

---

### OBSERVER PATTERN (Questions 31-40)

**31. What is the purpose of the Observer pattern?**
A) To traverse collections
B) To define one-to-many dependency between objects
C) To add behavior dynamically
D) To create objects

**32. In the Observer pattern, what is the "subject"?**
A) The object being observed
B) The object doing the observing
C) The event
D) The notification

**33. In our POS, what is the subject in the Observer pattern?**
A) KitchenDisplay
B) CustomerNotifier
C) Order
D) LineItem

**34. Which methods are typically in an Observer interface?**
A) `notify()` and `observe()`
B) `update()` or `updated()`
C) `changed()` and `onChange()`
D) `register()` and `unregister()`

**35. What event does our Order notify observers about?**
A) Only payment
B) Only item addition
C) itemAdded, paid, ready
D) Only order ready

**36. What is "push" vs "pull" in Observer pattern?**
A) Push sends data with notification, pull requires observer to request data
B) Push is faster, pull is slower
C) Push uses less memory, pull uses more
D) There is no difference

**37. Which of our classes implements `OrderObserver`?**
A) Order, LineItem
B) KitchenDisplay, CustomerNotifier, DeliveryDesk
C) Product, Money
D) PaymentStrategy

**38. What is a potential issue with Observer pattern?**
A) Cannot have multiple observers
B) Memory leaks if observers not unregistered
C) Subject and observers must be in same package
D) Observers must be created before subject

**39. How does Observer support loose coupling?**
A) Subject doesn't know concrete observer types
B) Observers don't know about subject
C) Both use global variables
D) They share the same interface

**40. Observer pattern is used in which common system?**
A) Database queries
B) Event handling systems (GUI, message brokers)
C) File I/O
D) Memory management

---

### COMMAND PATTERN (Questions 41-50)

**41. What does the Command pattern encapsulate?**
A) Objects
B) Requests as objects
C) Data structures
D) Algorithms

**42. What are the main components of Command pattern?**
A) Command, Receiver, Invoker, Client
B) Subject, Observer
C) Composite, Leaf
D) Strategy, Context

**43. In our POS, what is the Invoker?**
A) OrderService
B) AddItemCommand
C) PosRemote
D) Order

**44. What is the Receiver in our Command pattern?**
A) PosRemote
B) OrderService
C) AddItemCommand
D) PayOrderCommand

**45. What capability does Command pattern provide that simple method calls don't?**
A) Faster execution
B) Undo/redo functionality
C) Less memory usage
D) Type safety

**46. How does `AddItemCommand.undo()` work in our implementation?**
A) Reverses the item addition
B) Calls `OrderService.removeLastItem()`
C) Throws an exception
D) Does nothing

**47. What is a `MacroCommand`?**
A) A command that executes other commands
B) A command with many parameters
C) A command that cannot be undone
D) A command for large orders

**48. How does Command pattern decouple UI from business logic?**
A) UI calls business logic directly
B) UI only knows about Command interface, not business logic
C) UI and business logic share the same class
D) It doesn't decouple them

**49. What data structure does `PosRemote` use for undo?**
A) ArrayList
B) HashMap
C) Deque (stack)
D) Queue

**50. Command pattern is useful for implementing:**
A) Database transactions
B) Undo/redo, macro recording, queuing requests
C) Sorting algorithms
D) Memory allocation

---

### ADAPTER PATTERN (Questions 51-60)

**51. What is the purpose of the Adapter pattern?**
A) To add behavior dynamically
B) To convert one interface to another
C) To create objects
D) To manage state

**52. What are the two main types of Adapter?**
A) Class and Object adapter
B) Push and Pull adapter
C) Static and Dynamic adapter
D) Simple and Complex adapter

**53. In our POS, what is the "adaptee"?**
A) Printer interface
B) LegacyThermalPrinter
C) LegacyPrinterAdapter
D) ReceiptPrinter

**54. What is the "target" interface in our Adapter implementation?**
A) LegacyThermalPrinter
B) Printer
C) ReceiptPrinter
D) OrderService

**55. Why is Adapter better than modifying the legacy class?**
A) Faster execution
B) We don't own/control the legacy code
C) Uses less memory
D) Easier to test

**56. What does our `LegacyPrinterAdapter` convert?**
A) byte[] to String
B) String to byte[]
C) Order to Receipt
D) Money to String

**57. Adapter pattern follows which principle?**
A) Modify existing code for new requirements
B) Open/Closed Principle - extend without modification
C) Use inheritance always
D) Avoid interfaces

**58. What happens if we modify the vendor's LegacyThermalPrinter?**
A) Nothing, it's fine
B) Updates from vendor will overwrite our changes
C) Compiler error
D) Runtime exception

**59. Can we have multiple adapters for the same adaptee?**
A) No, only one adapter per adaptee
B) Yes, for different target interfaces
C) Only if adaptee allows it
D) Only in the same package

**60. Adapter pattern is commonly used for:**
A) Creating new objects
B) Integrating legacy/third-party code
C) Managing state
D) Traversing collections

---

### COMPOSITE PATTERN (Questions 61-70)

**61. What is the main purpose of Composite pattern?**
A) To add behavior dynamically
B) To compose objects into tree structures
C) To create objects
D) To convert interfaces

**62. What are the two types of nodes in Composite pattern?**
A) Parent and Child
B) Leaf and Composite
C) Subject and Observer
D) Component and Element

**63. In our menu system, what is the Leaf?**
A) Menu
B) MenuComponent
C) MenuItem
D) CompositeIterator

**64. What is the Composite in our implementation?**
A) MenuItem
B) Menu
C) MenuComponent
D) Product

**65. What is the trade-off between safety and transparency in Composite?**
A) Safety prevents invalid operations, transparency treats all uniformly
B) Safety is faster, transparency uses less memory
C) There is no trade-off
D) Safety and transparency are the same thing

**66. In our implementation, what happens if you call `MenuItem.add()`?**
A) Adds the item
B) Returns null
C) Throws UnsupportedOperationException
D) Does nothing silently

**67. Why did we choose safety over transparency?**
A) Performance
B) To prevent meaningless operations at runtime
C) Less code
D) Easier to test

**68. What operations should work uniformly on both Leaf and Composite?**
A) add(), remove()
B) iterator(), print()
C) getChild()
D) All operations

**69. Composite pattern is useful for representing:**
A) Flat lists
B) Hierarchical structures (trees)
C) Hash tables
D) Stacks

**70. Which SOLID principle does Composite support?**
A) Single Responsibility
B) Open/Closed (easy to add new components)
C) Dependency Inversion
D) Interface Segregation

---

### ITERATOR PATTERN (Questions 71-80)

**71. What is the purpose of Iterator pattern?**
A) To add behavior
B) To access elements of a collection sequentially without exposing representation
C) To create objects
D) To manage state

**72. What two methods must an Iterator typically implement?**
A) `next()` and `previous()`
B) `hasNext()` and `next()`
C) `first()` and `last()`
D) `get()` and `set()`

**73. Why is `CompositeIterator` different from a regular iterator?**
A) It's faster
B) It traverses nested structures (tree) using a stack
C) It uses less memory
D) It can modify elements

**74. What traversal order does our `CompositeIterator` use?**
A) Breadth-first
B) Depth-first
C) Random
D) Alphabetical

**75. What data structure does `CompositeIterator` use internally?**
A) ArrayList
B) HashMap
C) Deque<Iterator> (stack of iterators)
D) LinkedList

**76. How does Iterator support encapsulation?**
A) Exposes internal collection structure
B) Hides internal representation while allowing traversal
C) Uses public fields
D) Requires clients to know collection type

**77. What does `MenuItem.iterator()` return?**
A) null
B) An iterator over children
C) An empty iterator (Collections.emptyIterator())
D) Throws exception

**78. In our `Menu.vegetarianItems()`, what API do we use?**
A) For loop
B) While loop
C) Java Streams with filter()
D) Recursion

**79. When does `CompositeIterator.next()` push a new iterator?**
A) When it finds a MenuItem
B) When it finds a Menu
C) Always
D) Never

**80. Iterator pattern is built into which Java feature?**
A) for-each loop
B) switch statement
C) try-catch
D) static methods

---

### STATE PATTERN (Questions 81-90)

**81. What is the purpose of State pattern?**
A) To create objects
B) To allow an object to alter behavior when internal state changes
C) To add decorators
D) To traverse collections

**82. What are the main components of State pattern?**
A) State interface, Concrete states, Context
B) Subject, Observer
C) Command, Invoker
D) Composite, Leaf

**83. In our OrderFSM, what is the Context?**
A) NewState
B) OrderFSM
C) State interface
D) Order

**84. How many states did we implement for the order lifecycle?**
A) 3
B) 4
C) 5 (NEW, PREPARING, READY, DELIVERED, CANCELLED)
D) 6

**85. What happens when you call `pay()` in NEW state?**
A) Stays in NEW
B) Transitions to PREPARING
C) Transitions to READY
D) Throws exception

**86. Can you cancel an order in READY state?**
A) Yes, transitions to CANCELLED
B) No, stays in READY
C) Yes, transitions to NEW
D) Throws exception

**87. How is State pattern different from Strategy?**
A) State changes context behavior based on state, Strategy chooses algorithm
B) They are identical
C) State uses inheritance, Strategy uses composition
D) State is faster

**88. What is a benefit of State pattern over if/else chains?**
A) Faster execution
B) State-specific behavior is localized in state classes
C) Uses less memory
D) No benefit

**89. What visibility are our concrete state classes?**
A) public
B) protected
C) package-private (default)
D) private

**90. What does `OrderFSM.set(State s)` do?**
A) Creates a new state
B) Changes the current state
C) Deletes the old state
D) Throws exception

---

### SOLID PRINCIPLES (Questions 91-95)

**91. What does the 'S' in SOLID stand for?**
A) Strategy Principle
B) Single Responsibility Principle
C) State Principle
D) Simple Principle

**92. Which principle states "Classes should be open for extension but closed for modification"?**
A) Single Responsibility
B) Open/Closed Principle
C) Liskov Substitution
D) Dependency Inversion

**93. Which pattern best demonstrates Open/Closed Principle?**
A) Singleton
B) Decorator, Strategy
C) Observer
D) All patterns

**94. Dependency Inversion Principle states:**
A) High-level modules should depend on low-level modules
B) High-level modules should depend on abstractions, not concrete classes
C) Use concrete classes always
D) Avoid interfaces

**95. Which principle does "Favor composition over inheritance" relate to?**
A) Single Responsibility
B) Open/Closed
C) Liskov Substitution
D) Multiple principles (OCP, SRP)

---

### JAVA FEATURES (Questions 96-100)

**96. What is a Java record?**
A) A mutable class
B) An immutable data carrier with auto-generated methods
C) An interface
D) An annotation

**97. In our code, `Money` and `PricingResult` are:**
A) Classes
B) Records
C) Interfaces
D) Enums

**98. What does `Collections.unmodifiableList()` do?**
A) Creates a new list
B) Returns a read-only view of a list
C) Sorts the list
D) Removes duplicates

**99. Java Streams API is used for:**
A) File I/O
B) Functional-style operations on collections
C) Network programming
D) Memory management

**100. What does `final` keyword mean on a class?**
A) Class is immutable
B) Class cannot be extended
C) Class is abstract
D) Class is static

---

## ANSWERS

### DECORATOR PATTERN (1-10)
1. **B** - Add responsibilities dynamically without affecting others
2. **B** - Same interface or abstract class
3. **C** - Decorators can be stacked infinitely
4. **C** - Product
5. **B** - Decorator at runtime, inheritance at compile-time
6. **B** - Supports OCP by allowing extension without modification
7. **B** - The wrapped product's base price
8. **B** - `price()`
9. **C** - Recursive Composition
10. **B** - Can result in many small classes

### FACTORY PATTERN (11-20)
11. **B** - Create objects without exposing creation logic
12. **C** - A single decorated Product
13. **A** - Decorator
14. **B** - Centralized creation logic
15. **C** - Decorator combination
16. **C** - Simple Factory
17. **B** - Clients depend on abstractions, factory creates concrete types
18. **B** - Throws IllegalArgumentException (or returns basic product, depends on implementation)
19. **B** - Object creation logic is complex or varies
20. **D** - Both A (SRP - creation logic centralized) and B (OCP - easy to add products)

### STRATEGY PATTERN (21-30)
21. **A** - Create a family of algorithms and make them interchangeable
22. **B** - CashPayment, CardPayment, WalletPayment
23. **B** - `pay()`
24. **B** - State (but Strategy doesn't change based on context state)
25. **B** - Strategy encapsulates algorithms, State encapsulates state-dependent behavior
26. **B** - Favor composition over inheritance
27. **B** - Encapsulation and information hiding
28. **B** - `DiscountPolicy`
29. **B** - Easier to add new strategies without modifying existing code
30. **D** - All of the above (SRP: each strategy has one job, OCP: add without modify, ISP: focused interfaces)

### OBSERVER PATTERN (31-40)
31. **B** - Define one-to-many dependency between objects
32. **A** - The object being observed
33. **C** - Order
34. **B** - `update()` or `updated()`
35. **C** - itemAdded, paid, ready
36. **A** - Push sends data with notification, pull requires observer to request data
37. **B** - KitchenDisplay, CustomerNotifier, DeliveryDesk
38. **B** - Memory leaks if observers not unregistered
39. **A** - Subject doesn't know concrete observer types
40. **B** - Event handling systems (GUI, message brokers)

### COMMAND PATTERN (41-50)
41. **B** - Requests as objects
42. **A** - Command, Receiver, Invoker, Client
43. **C** - PosRemote
44. **B** - OrderService
45. **B** - Undo/redo functionality
46. **B** - Calls `OrderService.removeLastItem()`
47. **A** - A command that executes other commands
48. **B** - UI only knows about Command interface, not business logic
49. **C** - Deque (stack)
50. **B** - Undo/redo, macro recording, queuing requests

### ADAPTER PATTERN (51-60)
51. **B** - Convert one interface to another
52. **A** - Class and Object adapter
53. **B** - LegacyThermalPrinter
54. **B** - Printer
55. **B** - We don't own/control the legacy code
56. **B** - String to byte[]
57. **B** - Open/Closed Principle
58. **B** - Updates from vendor will overwrite our changes
59. **B** - Yes, for different target interfaces
60. **B** - Integrating legacy/third-party code

### COMPOSITE PATTERN (61-70)
61. **B** - Compose objects into tree structures
62. **B** - Leaf and Composite
63. **C** - MenuItem
64. **B** - Menu
65. **A** - Safety prevents invalid operations, transparency treats all uniformly
66. **C** - Throws UnsupportedOperationException
67. **B** - To prevent meaningless operations at runtime
68. **B** - iterator(), print()
69. **B** - Hierarchical structures (trees)
70. **B** - Open/Closed

### ITERATOR PATTERN (71-80)
71. **B** - Access elements sequentially without exposing representation
72. **B** - `hasNext()` and `next()`
73. **B** - Traverses nested structures using a stack
74. **B** - Depth-first
75. **C** - Deque<Iterator> (stack of iterators)
76. **B** - Hides internal representation while allowing traversal
77. **C** - Empty iterator (Collections.emptyIterator())
78. **C** - Java Streams with filter()
79. **B** - When it finds a Menu
80. **A** - for-each loop

### STATE PATTERN (81-90)
81. **B** - Allow an object to alter behavior when internal state changes
82. **A** - State interface, Concrete states, Context
83. **B** - OrderFSM
84. **C** - 5 (NEW, PREPARING, READY, DELIVERED, CANCELLED)
85. **B** - Transitions to PREPARING
86. **B** - No, stays in READY
87. **A** - State changes context behavior based on state, Strategy chooses algorithm
88. **B** - State-specific behavior is localized in state classes
89. **C** - package-private (default)
90. **B** - Changes the current state

### SOLID PRINCIPLES (91-95)
91. **B** - Single Responsibility Principle
92. **B** - Open/Closed Principle
93. **B** - Decorator, Strategy (and most patterns support it)
94. **B** - High-level modules should depend on abstractions
95. **D** - Multiple principles (OCP, SRP)

### JAVA FEATURES (96-100)
96. **B** - Immutable data carrier with auto-generated methods
97. **B** - Records
98. **B** - Returns a read-only view of a list
99. **B** - Functional-style operations on collections
100. **B** - Class cannot be extended

---

## QUICK REFERENCE GUIDE

### Pattern Summary Table

| Pattern | Purpose | Key Components | When to Use |
|---------|---------|----------------|-------------|
| **Decorator** | Add responsibilities dynamically | Component, ConcreteComponent, Decorator, ConcreteDecorator | Need to add features without subclassing |
| **Factory** | Create objects without exposing logic | Factory, Product, ConcreteProduct | Complex creation logic, hide concrete types |
| **Strategy** | Encapsulate interchangeable algorithms | Strategy, ConcreteStrategy, Context | Multiple ways to do something, avoid conditionals |
| **Observer** | One-to-many dependency | Subject, Observer, ConcreteObserver | Event handling, publish-subscribe |
| **Command** | Encapsulate requests as objects | Command, ConcreteCommand, Receiver, Invoker | Undo/redo, macro recording, request queuing |
| **Adapter** | Convert one interface to another | Target, Adaptee, Adapter | Integrate incompatible interfaces |
| **Composite** | Compose objects into trees | Component, Leaf, Composite | Part-whole hierarchies, tree structures |
| **Iterator** | Sequential access without exposure | Iterator, Aggregate, ConcreteIterator | Traverse collections uniformly |
| **State** | Alter behavior when state changes | State, ConcreteState, Context | State-dependent behavior, avoid conditionals |

### SOLID Quick Reference

- **S**ingle Responsibility: One reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subtypes must be substitutable for base types
- **I**nterface Segregation: Many specific interfaces > one general
- **D**ependency Inversion: Depend on abstractions, not concretions

---

**Study Tips:**
1. Understand the **problem each pattern solves**
2. Know the **key components** of each pattern
3. Remember **when to use** each pattern
4. Understand **SOLID principles** and how patterns support them
5. Know the **trade-offs** (e.g., safety vs transparency in Composite)
6. Practice identifying patterns in code examples
