# Café POS System - Design Patterns & Architecture Lab

A comprehensive Point-of-Sale system demonstrating **9 design patterns** and **layered architecture** principles. Built incrementally across 10 weekly labs covering everything from basic domain modeling to event-driven architecture.

## 📋 Table of Contents

- [Quick Start](#quick-start)
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Design Patterns Implemented](#design-patterns-implemented)
- [Testing](#testing)
- [Running Demos](#running-demos)
- [Project Structure](#project-structure)
- [What to Expect](#what-to-expect)

---

## 🚀 Quick Start

### Compile the Project
```bash
mvn compile
```

### Run All Tests (151 tests)
```bash
mvn test
```
**Expected Result:** `Tests run: 151, Failures: 0, Errors: 0, Skipped: 0`

### Check Test Coverage
```bash
mvn test jacoco:report
# View report: target/site/jacoco/index.html
```
**Expected Coverage:** 85%+ on business logic (48% overall including demos)

### Run Week 10 Demo (Latest)
```bash
# MVC Demo - Shows layered architecture
java -cp target/classes com.cafepos.demo.Week10Demo_MVC

# Event Bus Demo - Shows event-driven communication
java -cp target/classes com.cafepos.ui.EventWiringDemo
```

**Expected Output (MVC Demo):**
```
Order #4101
 - Espresso + Extra Shot + Oat Milk x1 = 3.80
 - Latte (Large) x2 = 7.80
Subtotal: 11.60
Discount: -0.58
Tax (10%): 1.10
Total: 12.12
```

---

## 📖 Project Overview

This is a **Café Point-of-Sale (POS) system** that demonstrates enterprise software design principles through a coffee shop ordering system. The project implements:

- **Product Catalog**: Coffee products (Espresso, Latte, Cappuccino, etc.)
- **Order Management**: Create orders, add items, calculate totals with tax
- **Payment Processing**: Multiple payment strategies (Cash, Card, Wallet)
- **Pricing & Discounts**: Loyalty discounts, coupon codes, tax calculation
- **Menu System**: Hierarchical menu with composite pattern
- **Order Lifecycle**: State machine for order status (New → Preparing → Ready → Delivered)
- **Event Notifications**: Observer pattern for kitchen, customer, and delivery notifications

### What Makes This Project Special?

1. **Clean Architecture** - Proper layering with clear dependencies
2. **Design Patterns** - 9 patterns implemented correctly in real-world scenarios
3. **High Test Coverage** - 151 tests with 85%+ coverage on business logic
4. **Production-Ready Practices** - Immutable value objects, dependency injection, event-driven design

---

## 🏗️ Architecture

### Layered Architecture (Week 10)

The system follows a **4-layer architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│   Presentation Layer (UI)                   │
│   - OrderController                         │
│   - ConsoleView                             │
│   - Demo classes                            │
├─────────────────────────────────────────────┤
│   Application Layer (Use Cases)             │
│   - CheckoutService                         │
│   - ReceiptFormatter                        │
│   - EventBus (in-process pub/sub)           │
├─────────────────────────────────────────────┤
│   Domain Layer (Business Logic)             │
│   - Order, LineItem, Product                │
│   - PaymentStrategy, DiscountPolicy         │
│   - OrderFSM (State Machine)                │
│   - Menu (Composite), OrderRepository       │
├─────────────────────────────────────────────┤
│   Infrastructure Layer (Adapters)           │
│   - InMemoryOrderRepository                 │
│   - LegacyPrinterAdapter                    │
│   - Wiring (Dependency Injection)           │
└─────────────────────────────────────────────┘
```

**Key Principles:**
- **Dependencies point inward** - Domain has no dependencies on other layers
- **Testability** - Each layer can be tested independently
- **Replaceability** - Infrastructure can be swapped (e.g., in-memory → PostgreSQL)

### Architecture Diagram

View the complete architecture diagram:
```bash
# Generate PNG diagram
plantuml -DPLANTUML_LIMIT_SIZE=16384 diagrams/puml/week10_architecture.puml -o ../png

# View diagram
open diagrams/png/week10_architecture.png
```

### Why Layered Monolith?

We chose a **Layered Monolith** architecture because:
- **Simple deployment** - Single JAR file, no distributed systems complexity
- **Clear boundaries** - Layers enforce separation of concerns
- **Easy testing** - Mock dependencies at layer boundaries
- **Future-ready** - Clean seams for future microservices extraction

**Future Partitioning Candidates:**
- **Payments** → Separate service with PCI compliance
- **Notifications** → Async messaging service (email/SMS)
- **Inventory** → Independent scaling for stock management

---

## 🎨 Design Patterns Implemented

### 1. **Strategy Pattern** (Week 4)
**Purpose:** Encapsulate algorithms and make them interchangeable

**Implementations:**
- `PaymentStrategy` - Cash, Card, Wallet payments
- `DiscountPolicy` - NoDiscount, LoyaltyPercent, FixedCoupon
- `TaxPolicy` - FixedRateTax

**Example:**
```java
Order order = new Order(1L);
order.pay(new CardPayment("1234-5678"));  // Interchangeable strategy
```

**Location:** `src/main/java/com/cafepos/payment/`, `src/main/java/com/cafepos/pricing/`

---

### 2. **Decorator Pattern** (Week 3)
**Purpose:** Add responsibilities to objects dynamically

**Implementations:**
- `ProductDecorator` base class
- Decorators: `ExtraShot`, `OatMilk`, `Syrup`, `SizeLarge`

**Example:**
```java
Product coffee = new SimpleProduct("ESP", "Espresso", Money.of(2.50));
coffee = new ExtraShot(coffee);     // +0.50
coffee = new OatMilk(coffee);       // +0.80
// Final price: 3.80
```

**Location:** `src/main/java/com/cafepos/decorator/`

---

### 3. **Factory Pattern** (Week 3)
**Purpose:** Centralize complex object creation

**Implementation:**
- `ProductFactory.create(String recipe)` - Parses recipes like "ESP+SHOT+OAT"

**Example:**
```java
ProductFactory factory = new ProductFactory();
Product product = factory.create("ESP+SHOT+OAT+L");
// Creates: Espresso with ExtraShot, OatMilk, and SizeLarge decorators
```

**Location:** `src/main/java/com/cafepos/factory/ProductFactory.java`

---

### 4. **Observer Pattern** (Week 4)
**Purpose:** Notify multiple objects when state changes

**Implementations:**
- `OrderObserver` interface
- Observers: `KitchenDisplay`, `CustomerNotifier`, `DeliveryDesk`

**Example:**
```java
Order order = new Order(1L);
order.register(new KitchenDisplay());
order.addItem(item);  // Notifies kitchen: "Order #1: item added"
```

**Location:** `src/main/java/com/cafepos/observer/`

---

### 5. **Command Pattern** (Week 8)
**Purpose:** Encapsulate requests as objects, support undo

**Implementations:**
- `Command` interface
- Commands: `AddItemCommand`, `PayOrderCommand`, `MacroCommand`
- `PosRemote` - Command invoker with undo stack

**Example:**
```java
PosRemote remote = new PosRemote(3);
remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
remote.press(0);  // Execute
remote.undo();    // Undo last command
```

**Location:** `src/main/java/com/cafepos/command/`

---

### 6. **Adapter Pattern** (Week 8)
**Purpose:** Make incompatible interfaces work together

**Implementation:**
- `LegacyPrinterAdapter` - Adapts old thermal printer to modern `Printer` interface

**Example:**
```java
LegacyThermalPrinter oldPrinter = new LegacyThermalPrinter();
Printer modernPrinter = new LegacyPrinterAdapter(oldPrinter);
modernPrinter.print(receipt);  // Works with modern interface
```

**Location:** `src/main/java/com/cafepos/printing/`

---

### 7. **Composite Pattern** (Week 9)
**Purpose:** Treat individual objects and compositions uniformly

**Implementation:**
- `MenuComponent` abstract base
- `MenuItem` (leaf), `Menu` (composite)
- `CompositeIterator` - Depth-first traversal

**Example:**
```java
Menu mainMenu = new Menu("Main Menu");
Menu drinks = new Menu("Drinks");
drinks.add(new MenuItem("Espresso", Money.of(2.50), false));
mainMenu.add(drinks);
mainMenu.print();  // Recursive print
```

**Location:** `src/main/java/com/cafepos/menu/`

---

### 8. **Iterator Pattern** (Week 9)
**Purpose:** Access elements sequentially without exposing structure

**Implementation:**
- `CompositeIterator` - Custom iterator for nested menu structures
- Depth-first traversal using stack

**Example:**
```java
Menu menu = buildMenuHierarchy();
Iterator<MenuComponent> iter = menu.iterator();
while (iter.hasNext()) {
    MenuComponent item = iter.next();
    // Process item
}
```

**Location:** `src/main/java/com/cafepos/menu/CompositeIterator.java`

---

### 9. **State Pattern** (Week 9)
**Purpose:** Change behavior based on internal state

**Implementation:**
- `OrderFSM` (Finite State Machine context)
- States: `NewState`, `PreparingState`, `ReadyState`, `DeliveredState`, `CancelledState`

**Example:**
```java
OrderFSM order = new OrderFSM();
order.pay();       // NEW → PREPARING
order.markReady(); // PREPARING → READY
order.deliver();   // READY → DELIVERED
```

**State Transitions:**
```
NEW → [pay] → PREPARING → [markReady] → READY → [deliver] → DELIVERED
  ↓                         ↓
[cancel]                [cancel]
  ↓                         ↓
CANCELLED              CANCELLED
```

**Location:** `src/main/java/com/cafepos/state/`

---

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

**What Happens:**
- Runs 151 JUnit 5 tests
- Tests all design patterns and architecture layers
- Generates JaCoCo coverage report

**Expected Output:**
```
Tests run: 151, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Test Coverage Report
```bash
mvn test jacoco:report
# Open: target/site/jacoco/index.html
```

**Coverage Breakdown:**
| Component | Coverage | Tests |
|-----------|----------|-------|
| Payment Strategies | 100% | 8 tests |
| Pricing Service | 99% | 27 tests |
| Domain (Order, LineItem) | 91% | 11 tests |
| State Pattern | 91% | 13 tests |
| Command Pattern | 91% | 9 tests |
| Decorator/Factory | 91% | 23 tests |
| Observer Pattern | 89% | 6 tests |
| **Week 10 - Application** | 100% | 13 tests |
| **Week 10 - Infrastructure** | 95% | (part of 13) |
| **Week 10 - UI (MVC)** | 50% | 7 tests |
| **Week 10 - Event Bus** | 100% | (part of 13) |

**Overall:** 85%+ business logic coverage (48% including untested demo classes)

### Test Suites

1. **`MoneyTest`** - Value object tests
2. **`OrderTest`** - Order domain logic
3. **`PaymentStrategyTest`** - Strategy pattern
4. **`DecoratorAndFactoryTest`** - Decorator + Factory patterns
5. **`Week6RefactoredTests`** - Pricing service tests
6. **`CommandPatternTest`** - Command pattern with undo
7. **`AdapterPatternTest`** - Adapter pattern
8. **`CompositeIteratorTest`** - Composite + Iterator patterns
9. **`OrderFSMTest`** - State pattern
10. **`LayeredArchitectureTest`** - Week 10 architecture
11. **`MVCPatternTest`** - Week 10 MVC pattern
12. **`ObserverPatternTest`** - Observer pattern

---

## 🎬 Running Demos

### Week 10 - Layered Architecture

**MVC Pattern Demo:**
```bash
java -cp target/classes com.cafepos.demo.Week10Demo_MVC
```

**Output:**
```
Order #4101
 - Espresso + Extra Shot + Oat Milk x1 = 3.80
 - Latte (Large) x2 = 7.80
Subtotal: 11.60
Discount: -0.58
Tax (10%): 1.10
Total: 12.12
```

**Event Bus Demo:**
```bash
java -cp target/classes com.cafepos.ui.EventWiringDemo
```

**Output:**
```
[UI] order created: 4201
[UI] order paid: 4201
```

---

### Week 9 - Composite + Iterator + State

```bash
java -cp target/classes com.cafepos.demo.Week9Demo
```

**Output:**
```
=== Menu Hierarchy (Composite + Iterator) ===
Main Menu
  Drinks Menu
    - Espresso = 2.50
    - Latte = 3.50
  Food Menu
    - Croissant (V) = 3.00

=== Depth-first Iterator ===
[Iterating through all items...]

=== State Machine Demo ===
[State] Paid → Preparing
[State] Ready for pickup
[State] Delivered
```

---

### Week 8 - Command + Adapter

```bash
java -cp target/classes com.cafepos.demo.Week8Demo
```

**Output:**
```
=== Command Pattern ===
[Service] Added ESP+SHOT+OAT x1
[Service] Removed last item (undo)

=== Adapter Pattern ===
[Legacy] Printing receipt...
```

---

### Other Weekly Demos

```bash
# Week 2-7 Demos
java -cp target/classes com.cafepos.demo.Week2Demo
java -cp target/classes com.cafepos.demo.Week3Demo
# ... etc
```

---

## 📁 Project Structure

```
cs4928_cafe_lab/
├── src/main/java/com/cafepos/
│   ├── app/                      # Application Layer (Week 10)
│   │   ├── CheckoutService.java
│   │   ├── ReceiptFormatter.java
│   │   └── events/
│   │       ├── EventBus.java
│   │       ├── OrderEvent.java
│   │       ├── OrderCreated.java
│   │       └── OrderPaid.java
│   │
│   ├── ui/                       # Presentation Layer (Week 10)
│   │   ├── OrderController.java  # MVC Controller
│   │   ├── ConsoleView.java      # MVC View
│   │   └── EventWiringDemo.java
│   │
│   ├── domain/                   # Domain Layer
│   │   ├── Order.java            # Aggregate root
│   │   ├── LineItem.java         # Value object
│   │   ├── OrderIds.java         # ID generator
│   │   └── OrderRepository.java  # Repository interface
│   │
│   ├── infra/                    # Infrastructure Layer (Week 10)
│   │   ├── InMemoryOrderRepository.java
│   │   └── Wiring.java           # DI container
│   │
│   ├── catalog/                  # Product catalog
│   │   ├── Product.java
│   │   ├── SimpleProduct.java
│   │   └── InMemoryCatalog.java
│   │
│   ├── decorator/                # Decorator Pattern (Week 3)
│   │   ├── ProductDecorator.java
│   │   ├── ExtraShot.java
│   │   ├── OatMilk.java
│   │   ├── Syrup.java
│   │   └── SizeLarge.java
│   │
│   ├── factory/                  # Factory Pattern (Week 3)
│   │   └── ProductFactory.java
│   │
│   ├── payment/                  # Strategy Pattern (Week 4)
│   │   ├── PaymentStrategy.java
│   │   ├── CardPayment.java
│   │   ├── CashPayment.java
│   │   └── WalletPayment.java
│   │
│   ├── pricing/                  # Strategy Pattern (Week 5)
│   │   ├── PricingService.java
│   │   ├── DiscountPolicy.java
│   │   ├── TaxPolicy.java
│   │   └── PricingResult.java
│   │
│   ├── observer/                 # Observer Pattern (Week 4)
│   │   ├── OrderObserver.java
│   │   ├── KitchenDisplay.java
│   │   ├── CustomerNotifier.java
│   │   └── DeliveryDesk.java
│   │
│   ├── command/                  # Command Pattern (Week 8)
│   │   ├── Command.java
│   │   ├── AddItemCommand.java
│   │   ├── PayOrderCommand.java
│   │   └── PosRemote.java
│   │
│   ├── menu/                     # Composite + Iterator (Week 9)
│   │   ├── MenuComponent.java
│   │   ├── Menu.java
│   │   ├── MenuItem.java
│   │   └── CompositeIterator.java
│   │
│   ├── state/                    # State Pattern (Week 9)
│   │   ├── State.java
│   │   ├── OrderFSM.java
│   │   ├── NewState.java
│   │   ├── PreparingState.java
│   │   ├── ReadyState.java
│   │   ├── DeliveredState.java
│   │   └── CancelledState.java
│   │
│   ├── printing/                 # Adapter Pattern (Week 8)
│   │   ├── Printer.java
│   │   └── LegacyPrinterAdapter.java
│   │
│   └── demo/                     # Demo programs (not tested)
│       ├── Week2Demo.java
│       ├── Week3Demo.java
│       ├── ...
│       └── Week10Demo_MVC.java
│
├── src/test/java/com/cafepos/   # 151 tests
│   ├── MoneyTest.java
│   ├── OrderTest.java
│   ├── payment/PaymentStrategyTest.java
│   ├── command/CommandPatternTest.java
│   ├── menu/CompositeIteratorTest.java
│   ├── state/OrderFSMTest.java
│   ├── app/LayeredArchitectureTest.java       # Week 10
│   ├── ui/MVCPatternTest.java                 # Week 10
│   └── observer/ObserverPatternTest.java
│
├── diagrams/
│   ├── puml/
│   │   ├── week10_architecture.puml  # Layered architecture diagram
│   │   ├── week8_sequence.puml
│   │   └── week*.puml
│   └── png/
│       └── week10_architecture.png
│
├── questions/
│   └── week9.md              # State transition table + reflections
│
├── pom.xml                   # Maven config with JaCoCo coverage
└── README.md                 # This file
```

---

## 🎯 What to Expect

### When You Compile
```bash
mvn compile
```
**Result:**
- Compiles 81 Java source files
- Auto-generates PlantUML class diagram (`diagrams/puml/cafe-pos-[timestamp].puml`)
- Output: `target/classes/` directory

---

### When You Run Tests
```bash
mvn test
```
**Result:**
- Runs 151 tests (all should pass)
- Tests cover all 9 design patterns
- Tests verify architecture layers work correctly
- Generates coverage report: `target/site/jacoco/index.html`

**Coverage Metrics:**
- **Business Logic:** 85%+ (meets requirement)
- **Overall:** 48% (includes 473 lines of untested demo code)
- **Week 10 Components:** 95-100% coverage

---

### When You Run Demos

Each demo shows working patterns in action:

**Week 10 (Layered Architecture):**
- Shows MVC pattern with proper layer separation
- Demonstrates event-driven communication
- Expected: Receipt output with calculated totals

**Week 9 (Composite/Iterator/State):**
- Shows menu hierarchy traversal
- Demonstrates state machine transitions
- Expected: Menu printout + state transition messages

**Week 8 (Command/Adapter):**
- Shows command execution with undo
- Demonstrates adapter bridging legacy code
- Expected: Command logs + printer output

**Earlier Weeks:**
- Show progressive pattern implementation
- Each builds on previous weeks

---

### When You Generate Diagrams
```bash
plantuml -DPLANTUML_LIMIT_SIZE=16384 diagrams/puml/week10_architecture.puml -o ../png
```
**Result:**
- Creates PNG diagram showing 4-layer architecture
- Visual representation of all components and relationships
- Output: `diagrams/png/week10_architecture.png`

---

## 🔧 Development Commands

### Full Build & Test Cycle
```bash
mvn clean compile test jacoco:report
```

### Run Specific Test Class
```bash
mvn test -Dtest=LayeredArchitectureTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=LayeredArchitectureTest#checkoutService_createsReceiptFromOrder
```

### Skip Tests During Compile
```bash
mvn compile -DskipTests
```

### View Coverage Report
```bash
# After running tests with jacoco:report
firefox target/site/jacoco/index.html
# or
open target/site/jacoco/index.html
```

---

## 📚 Key Concepts Demonstrated

### SOLID Principles
- **Single Responsibility:** Each class has one reason to change
- **Open/Closed:** Patterns allow extension without modification
- **Liskov Substitution:** Strategies/decorators are interchangeable
- **Interface Segregation:** Small, focused interfaces
- **Dependency Inversion:** Depend on abstractions (repository interface)

### Enterprise Patterns
- **Value Objects:** `Money`, `LineItem` (immutable)
- **Entities:** `Order` (has identity)
- **Repository Pattern:** `OrderRepository` interface
- **Dependency Injection:** `Wiring` composition root
- **Event-Driven Architecture:** `EventBus` pub/sub

### Clean Code Practices
- **Immutability:** Value objects are immutable
- **Fail-fast validation:** Constructor validation
- **Encapsulation:** Private fields, public methods
- **Meaningful names:** Clear, intention-revealing names
- **Small methods:** Each method does one thing

---

## 🎓 Learning Path

**Weeks 2-3:** Basic domain modeling + Decorator + Factory
**Week 4:** Strategy + Observer patterns
**Week 5:** Pricing service with strategies
**Week 6:** Refactoring code smells
**Week 7:** Interactive CLI
**Week 8:** Command + Adapter patterns
**Week 9:** Composite + Iterator + State patterns
**Week 10:** Layered Architecture + MVC + Event Bus

---

## 📊 Project Statistics

- **Lines of Code:** ~2,500 (business logic)
- **Test Lines:** ~1,500
- **Test Classes:** 14
- **Test Methods:** 151
- **Design Patterns:** 9
- **Architecture Layers:** 4
- **Java Version:** 21
- **Build Tool:** Maven
- **Test Framework:** JUnit 5
- **Coverage Tool:** JaCoCo

---

## 🔗 Related Documentation

- **Architecture Diagram:** `diagrams/png/week10_architecture.png`
- **State Transition Table:** `questions/week9.md`
- **Pattern Reflections:** `questions/week9.md`
- **Test Coverage Report:** `target/site/jacoco/index.html` (after running tests)

---

## 🙏 Acknowledgments

Built as part of CS4928 Software Architecture course, demonstrating enterprise design patterns and clean architecture principles.

---

**Last Updated:** November 2025
**Java Version:** 21
**Build Status:** ✅ All 151 tests passing
