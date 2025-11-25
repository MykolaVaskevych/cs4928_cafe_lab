# 🎬 Final Assessment Video Script & Plan
**Total Duration:** 8 minutes
**Presenters:** Oliver (4 min) + Nick (3 min) + Shared (1 min)

---

## 📋 Video Structure & Timing

| Section | Presenter | Time | Cumulative |
|---------|-----------|------|------------|
| Introduction | Oliver | 0:30 | 0:30 |
| **Part 1: Patterns** | **Oliver** | **2:00** | **2:30** |
| **Part 2: Architecture** | **Nick** | **1:30** | **4:00** |
| **Part 3: Testing** | **Oliver** | **2:00** | **6:00** |
| **Part 4: Trade-offs** | **Nick** | **1:30** | **7:30** |
| Conclusion | Oliver | 0:30 | 8:00 |

---

## 🎥 SECTION 1: Introduction (0:00 - 0:30)
**Presenter:** Oliver
**Screen:** Project root showing folder structure

### What to Show:
```
cs4928_cafe_lab/
├── src/main/java/com/cafepos/
├── src/test/java/
├── diagrams/
├── README.md
└── pom.xml
```

### What to Say:
> "Hi, I'm Oliver, and this is Nick. Welcome to our Café POS & Delivery System final assessment. Over weeks 8 through 11, we've built a complete point-of-sale system demonstrating 9 design patterns, clean layered architecture, and comprehensive testing. The system manages coffee orders, payments, menus, and order lifecycle with over 2,500 lines of production code and 151 tests. Let me start by showing you the design patterns we've integrated."

**TRANSITION to Oliver Part 1**

---

## 🎨 SECTION 2: Pattern Correctness & Integration (0:30 - 2:30)
**Presenter:** Oliver
**Duration:** 2 minutes
**Marks:** 8/25

### 2.1 Command Pattern (0:30 - 0:50) — 20 seconds

**What to Show:**
1. Open `src/main/java/com/cafepos/command/PosRemote.java`
2. Open `src/main/java/com/cafepos/command/AddItemCommand.java`
3. Run demo: `java -cp target/classes com.cafepos.demo.Week8Demo`

**What to Say:**
> "First, the **Command pattern**. We use it to encapsulate order actions as objects, supporting undo functionality. Here's the PosRemote acting as an invoker—it stores commands in slots and executes them. The AddItemCommand encapsulates adding items to orders. When we run the demo, you can see commands executing and undoing. This decouples UI button presses from domain logic, making the system extensible."

**Screen Sequence:**
```java
// SHOW: PosRemote.java - press() and undo() methods
public void press(int slot) {
    commands[slot].execute();
    lastCommand = commands[slot];
}

public void undo() {
    if (lastCommand != null) lastCommand.undo();
}

// SHOW: Terminal output
[Service] Added ESP+SHOT+OAT x1
[Service] Removed last item (undo)
```

---

### 2.2 Adapter Pattern (0:50 - 1:05) — 15 seconds

**What to Show:**
1. Open `src/main/java/com/cafepos/printing/LegacyPrinterAdapter.java`
2. Show the legacy printer interface

**What to Say:**
> "The **Adapter pattern** integrates a legacy thermal printer without modifying our core code. The LegacyPrinterAdapter translates our modern Printer interface to the old legacy API. This follows the Open/Closed Principle—we extended functionality without changing existing classes."

**Screen Sequence:**
```java
// SHOW: LegacyPrinterAdapter.java
public class LegacyPrinterAdapter implements Printer {
    private final LegacyThermalPrinter legacy;

    public void print(String text) {
        legacy.printReceipt(text);  // Adapts to legacy API
    }
}
```

---

### 2.3 Composite & Iterator Patterns (1:05 - 1:30) — 25 seconds

**What to Show:**
1. Open `src/main/java/com/cafepos/menu/Menu.java` (Composite)
2. Open `src/main/java/com/cafepos/menu/CompositeIterator.java`
3. Run: `java -cp target/classes com.cafepos.demo.Week9Demo_Menu`

**What to Say:**
> "The **Composite pattern** creates hierarchical menus. A Menu can contain MenuItems or other Menus. The **Iterator pattern** provides depth-first traversal without exposing the tree structure. Here's the CompositeIterator using a stack to traverse nested menus. When we run it, you see the complete menu hierarchy printed recursively, and we can filter vegetarian items uniformly."

**Screen Sequence:**
```java
// SHOW: Menu.java
public class Menu extends MenuComponent {
    private final List<MenuComponent> children = new ArrayList<>();

    public Iterator<MenuComponent> iterator() {
        return new CompositeIterator(childrenIterator());
    }
}

// SHOW: Terminal output
Main Menu
  Drinks Menu
    - Espresso = 2.50
    - Latte = 3.50
  Food Menu
    - Croissant (V) = 3.00
```

---

### 2.4 State Pattern (1:30 - 1:50) — 20 seconds

**What to Show:**
1. Open `src/main/java/com/cafepos/state/OrderFSM.java`
2. Show state classes: `NewState.java`, `PreparingState.java`
3. Run: `java -cp target/classes com.cafepos.demo.Week9Demo_State`

**What to Say:**
> "The **State pattern** models order lifecycle as a finite state machine. Each state—New, Preparing, Ready, Delivered, Cancelled—encapsulates its own transition logic. The OrderFSM delegates all actions to the current state. This eliminates conditional chains and makes adding new states trivial. Watch the state transitions in the demo."

**Screen Sequence:**
```java
// SHOW: OrderFSM.java
public void pay() {
    state.pay(this);  // Delegate to current state
}

// SHOW: NewState.java
public void pay(OrderFSM ctx) {
    System.out.println("[State] Paid → Preparing");
    ctx.set(new PreparingState());
}

// SHOW: Terminal output
[State] Paid → Preparing
[State] Ready for pickup
[State] Delivered
```

---

### 2.5 MVC & EventBus (1:50 - 2:30) — 40 seconds

**What to Show:**
1. Open `src/main/java/com/cafepos/ui/OrderController.java` (Controller)
2. Open `src/main/java/com/cafepos/ui/ConsoleView.java` (View)
3. Open `src/main/java/com/cafepos/app/events/EventBus.java`
4. Run: `java -cp target/classes com.cafepos.demo.Week10Demo_MVC`
5. Run: `java -cp target/classes com.cafepos.ui.EventWiringDemo`

**What to Say:**
> "For Week 10, we implemented **MVC**. The OrderController handles user actions, translating them into service calls. The ConsoleView handles only display. The domain model remains unchanged—this is classic separation of concerns. We also built an **EventBus** for component communication. It's a lightweight pub-sub system that decouples UI from application events. When an order is created or paid, events flow through the bus without tight coupling. Let me run both demos quickly."

**Screen Sequence:**
```java
// SHOW: OrderController.java
public String checkout(long orderId, int taxPercent) {
    return checkout.checkout(orderId, taxPercent);  // Delegates to app layer
}

// SHOW: EventBus.java
public <T> void emit(T event) {
    var list = handlers.getOrDefault(event.getClass(), List.of());
    for (var h : list) ((Consumer<T>) h).accept(event);
}

// SHOW: MVC Demo output
Order #4101
 - Espresso + Extra Shot + Oat Milk x1 = 3.80
 - Latte (Large) x2 = 7.80
Total: 12.12

// SHOW: EventBus Demo output
[UI] order created: 4201
[UI] order paid: 4201
```

**TRANSITION:** "Now Nick will show you the architectural structure behind these patterns."

---

## 🏗️ SECTION 3: Architectural Integrity (2:30 - 4:00)
**Presenter:** Nick
**Duration:** 1 minute 30 seconds
**Marks:** 7/25

### 3.1 Four-Layer Architecture Overview (2:30 - 3:10) — 40 seconds

**What to Show:**
1. Display architecture diagram: `diagrams/png/week10_architecture.png`
2. Show project structure in IDE/terminal:

```
src/main/java/com/cafepos/
├── ui/           (Presentation Layer)
├── demo/         (Presentation Layer)
├── app/          (Application Layer)
├── domain/       (Domain Layer - core entities)
├── command/      (Domain - Command pattern)
├── state/        (Domain - State pattern)
├── menu/         (Domain - Composite/Iterator)
├── observer/     (Domain - Observer pattern)
├── payment/      (Domain - Strategy pattern)
├── pricing/      (Domain - business logic)
├── factory/      (Domain - Factory pattern)
├── catalog/      (Domain - product catalog)
├── decorator/    (Domain - Decorator pattern)
├── common/       (Shared utilities)
├── infra/        (Infrastructure Layer)
├── printing/     (Infrastructure - adapters)
├── checkout/     (Cross-cutting)
└── smells/       (Legacy code - Week 7)
```

**What to Say:**
> "Our system follows a clean **4-layer architecture** organized across 18 packages. At the top, the **Presentation layer** contains OrderController, ConsoleView, and demo classes. The **Application layer** has CheckoutService and ReceiptFormatter—orchestrating domain operations without I/O. The **Domain layer** is the largest—we organized it by pattern: separate packages for command, state, menu, observer, payment, pricing, and factory. This makes patterns easy to find. The **Infrastructure layer** has InMemoryOrderRepository and Wiring for dependency injection. All dependencies point inward to the domain—that's the Dependency Inversion Principle in action."

**Screen Sequence:**
```
1. Show diagram with 4 colored layers
2. Scroll through actual package structure showing 18 packages
3. Point to dependency arrows (all pointing to domain)
4. Highlight how domain/ + command/ + state/ + menu/ etc. are all Domain layer
```

---

### 3.2 Components & Connectors (3:10 - 4:00) — 50 seconds

**What to Show:**
1. Open `src/main/java/com/cafepos/infra/Wiring.java`
2. Open `src/main/java/com/cafepos/app/events/EventBus.java`
3. Show `OrderRepository` interface in domain, implementation in infra

**What to Say:**
> "Let me highlight our key **connectors**. First, the **Repository pattern**—the domain defines the OrderRepository interface with no implementation details. The infrastructure provides InMemoryOrderRepository. This means we could swap in a PostgreSQL implementation without touching domain code. Second, the **Wiring class** acts as our composition root. It creates all components and wires dependencies in one place. This makes the system testable and maintainable. Third, the **EventBus** we saw earlier connects components loosely. The Presentation layer can subscribe to domain events without direct coupling to the Application layer. These connectors maintain clean boundaries—no layer leaks details to others."

**Screen Sequence:**
```java
// SHOW: Domain defines interface
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(long id);
}

// SHOW: Infrastructure implements it
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> store = new HashMap<>();
    // Implementation details hidden from domain
}

// SHOW: Wiring.java
public static Components createDefault() {
    OrderRepository repo = new InMemoryOrderRepository();
    PricingService pricing = new PricingService(...);
    CheckoutService checkout = new CheckoutService(repo, pricing);
    return new Components(repo, pricing, checkout);
}
```

**TRANSITION:** "Oliver will now demonstrate our testing strategy and code quality."

---

## 🧪 SECTION 4: Code Quality & Testing (4:00 - 6:00)
**Presenter:** Oliver
**Duration:** 2 minutes
**Marks:** 6/25

### 4.1 Code Organization & Quality (4:00 - 4:30) — 30 seconds

**What to Show:**
1. Show package structure in IDE
2. Open 2-3 well-organized classes:
   - `Order.java` (clean domain entity)
   - `Money.java` (immutable value object)
   - `PricingService.java` (clean service)

**What to Say:**
> "Our code follows clean architecture principles and SOLID design. Let me show you the organization. Each package has a clear responsibility—domain entities like Order, value objects like Money which is immutable, services like PricingService with single responsibilities. We refactored heavily since mid-term. Notice how Order validates in constructors—fail-fast design. Money is immutable using Java records. All our strategies and decorators follow the Open/Closed Principle. The naming is intention-revealing, methods are small, and we use composition over inheritance throughout."

**Screen Sequence:**
```java
// SHOW: Money.java (Value Object)
public record Money(BigDecimal amount) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Invalid amount");
    }
    // Immutable, no setters
}

// SHOW: Order.java (Entity with validation)
public void addItem(LineItem li) {
    if (li == null) throw new IllegalArgumentException("lineItem required");
    items.add(li);
    notifyObservers("itemAdded");
}
```

---

### 4.2 Test Coverage Overview (4:30 - 5:00) — 30 seconds

**What to Show:**
1. Run: `mvn test` (show output scrolling)
2. Open JaCoCo report: `target/site/jacoco/index.html`

**What to Say:**
> "We have comprehensive testing. Let me run the full test suite—151 tests, all passing. Our coverage report shows 85% coverage on business logic. We excluded demo classes from coverage requirements since they're not meant to be tested. The coverage breakdown: Payment strategies—100%, Pricing—99%, Domain—91%, State pattern—91%, Command—91%. All Week 10 components we just built have 95-100% coverage."

**Screen Sequence:**
```
Terminal output:
Tests run: 151, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

JaCoCo Report showing:
- com.cafepos.payment: 100%
- com.cafepos.pricing: 99%
- com.cafepos.app: 100%
- com.cafepos.infra: 95%
- Overall business logic: 85%
```

---

### 4.3 Test Examples (5:00 - 6:00) — 60 seconds

**What to Show:**
1. Open `src/test/java/com/cafepos/state/OrderFSMTest.java`
2. Open `src/test/java/com/cafepos/app/LayeredArchitectureTest.java`
3. Open `src/test/java/com/cafepos/command/CommandPatternTest.java`
4. Run one specific test to show it working

**What to Say:**
> "Let me show you what we're testing. First, OrderFSMTest—13 tests covering the entire state machine. We test happy paths like going from New to Delivered, and we test invalid transitions like trying to deliver before marking ready. Here's one test verifying you can't cancel after the order is ready. Second, LayeredArchitectureTest—this is new for Week 10. We test the CheckoutService end-to-end, verify the EventBus pub-sub works, and test the repository pattern. Each layer can be tested independently. Third, CommandPatternTest validates command execution and undo. We test single commands, macro commands, and the undo stack. Let me run one test to show it's actually working."

**Screen Sequence:**
```java
// SHOW: OrderFSMTest.java
@Test
void cannotCancelAfterReady() {
    OrderFSM order = new OrderFSM();
    order.pay();
    order.markReady();
    order.cancel();  // Should fail
    assertEquals("READY", order.currentState());
}

// SHOW: LayeredArchitectureTest.java
@Test
void checkoutService_createsReceiptFromOrder() {
    var repo = new InMemoryOrderRepository();
    var service = new CheckoutService(repo, pricing);

    Order order = new Order(1001L);
    order.addItem(new LineItem(product, 1));
    repo.save(order);

    String receipt = service.checkout(1001L, 10);
    assertTrue(receipt.contains("Order #1001"));
}

// RUN: mvn test -Dtest=OrderFSMTest#cannotCancelAfterReady
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

**TRANSITION:** "Nick will finish with our architectural decision records and trade-offs."

---

## 📝 SECTION 5: Trade-off Documentation (6:00 - 7:30)
**Presenter:** Nick
**Duration:** 1 minute 30 seconds
**Marks:** 4/25

### 5.1 ADR Introduction (6:00 - 6:15) — 15 seconds

**What to Show:**
- Show file: `docs/adr/001-layered-monolith-architecture.md`

**What to Say:**
> "We documented our architectural decisions using Architecture Decision Records. Let me show you our key ADR on choosing a layered monolith over microservices. This captures the context, alternatives, and trade-offs."

---

### 5.2 ADR Walkthrough (6:15 - 7:15) — 60 seconds

**What to Show:**
Display the ADR content

**What to Say:**
> "**Context**: We needed to decide whether to build separate microservices for Payments, Notifications, and Orders, or use a single layered monolith. **Alternatives considered**: First, a microservices architecture with separate services communicating via REST and events. Second, a modular monolith with strong package boundaries. Third, a simple layered monolith with clear layers. **Decision**: We chose the layered monolith. **Reasoning**: For a POS system of this scale, microservices would add unnecessary complexity—distributed transactions, network latency, and operational overhead. A monolith keeps deployment simple, makes testing easier, and still maintains clean boundaries through layers. **Consequences**: The positive side—simple deployment as a single JAR, easy testing with no network mocking, and fast in-process communication. The negative—we can't scale components independently, and team boundaries aren't as clear. However, we designed clean seams for future extraction. See the EventBus—it's in-process now but could easily become a message broker. The repository pattern means we could split persistence later."

---

### 5.3 Connect to Code (7:15 - 7:30) — 15 seconds

**What to Show:**
- Point back to architecture diagram
- Show EventBus and Repository implementations

**What to Say:**
> "You can see this decision reflected throughout the code. The EventBus demonstrates the future migration path—it's in-process now but designed like a message broker. The repository pattern gives us database flexibility. The four layers are strictly enforced—UI never touches infrastructure directly. These seams make future partitioning possible without rewriting."

**TRANSITION:** "Let me hand back to Oliver for our conclusion."

---

## 🎬 SECTION 6: Conclusion (7:30 - 8:00)
**Presenter:** Oliver
**Duration:** 30 seconds

**What to Show:**
- Return to project root or README
- Show final stats: 151 tests, 9 patterns, 4 layers

**What to Say:**
> "To summarize: We've delivered a complete Café POS system with 9 design patterns integrated correctly, clean 4-layer architecture with clear connectors, 151 passing tests with 85% coverage, and documented trade-offs through ADRs. The system demonstrates Command for undo, Adapter for legacy integration, Composite and Iterator for menus, State for order lifecycle, and MVC with EventBus for presentation decoupling. Everything is testable, maintainable, and follows SOLID principles. The code is on GitHub, all tests pass, and the documentation is comprehensive. Thank you."

**Screen:**
```
Final Summary:
✅ 9 Design Patterns Implemented
✅ 4-Layer Architecture (Presentation, Application, Domain, Infrastructure)
✅ 151 Tests Passing (85% Coverage)
✅ ADR Documentation
✅ Production-Ready Code (~2,500 LOC)

Build Status: ✅ All tests passing
```

---

## 📋 Pre-Recording Checklist

### Before Recording:

**Oliver's Prep:**
- [ ] Compile project: `mvn clean compile`
- [ ] Have demos ready to run quickly
- [ ] Open all code files in IDE tabs (in order):
  - `PosRemote.java`, `AddItemCommand.java`
  - `LegacyPrinterAdapter.java`
  - `Menu.java`, `CompositeIterator.java`
  - `OrderFSM.java`, `NewState.java`
  - `OrderController.java`, `EventBus.java`
  - Test files
- [ ] Practice transitions to Nick
- [ ] Time yourself: Section 1 (2:30) + Section 3 (2:00) = 4:30

**Nick's Prep:**
- [ ] Open architecture diagram in viewer
- [ ] Have project structure visible in terminal
- [ ] Open code files in tabs:
  - `Wiring.java`
  - `OrderRepository.java` (interface)
  - `InMemoryOrderRepository.java`
  - `EventBus.java`
- [ ] Have ADR file ready: `docs/adr/001-layered-monolith-architecture.md`
- [ ] Practice transitions from/to Oliver
- [ ] Time yourself: Section 2 (1:30) + Section 4 (1:30) = 3:00

**Both:**
- [ ] Test screen recording software
- [ ] Practice full run-through (target 7:30, max 8:00)
- [ ] Have terminal commands ready to copy-paste
- [ ] Test audio levels
- [ ] Close unnecessary applications

---

## 🎯 Tips for Recording

**For Oliver:**
1. Speak clearly and pace yourself
2. Don't rush through code—let viewers see class names
3. Terminal output should be visible for 3-5 seconds
4. Practice saying "Now Nick will show..." smoothly
5. Keep energy up—smile even though it's recorded

**For Nick:**
1. Point with cursor to diagram elements as you talk
2. Zoom in on architecture diagram so layers are readable
3. Read ADR clearly but don't just recite—explain
4. Connect ADR back to code ("You can see this here...")
5. Keep transitions smooth

**General:**
- **Stick to the time!** 8 minutes hard limit
- Use a timer/stopwatch while recording
- If one section runs long, the other should cut content
- Have water nearby but mute when drinking
- Do a practice run and watch it back
- Re-record if you go over 8:10

---

## 🎥 Recording Tips

### Screen Layout Recommendations:

**Oliver's Sections:**
- IDE on left 70%, terminal on right 30%
- Font size: Minimum 16pt for code
- Use IDE's "Presentation Mode" if available

**Nick's Sections:**
- Architecture diagram full screen for first part
- IDE with project structure for second part
- ADR document in markdown viewer or IDE

### Audio:
- Use headset microphone (better quality than laptop mic)
- Record in quiet room
- Test audio levels beforehand
- Speak 6 inches from microphone

### Video:
- 1080p minimum resolution
- Screen recording software: OBS Studio (free) or QuickTime
- Record in one take if possible, or use simple cuts
- Max file size: Check submission requirements

---

## 📝 Quick Reference Commands

### Compile & Run Commands (copy-paste ready):

```bash
# Compile
mvn clean compile

# Run Week 8 Demo
java -cp target/classes com.cafepos.demo.Week8Demo

# Run Week 9 Menu Demo
java -cp target/classes com.cafepos.demo.Week9Demo_Menu

# Run Week 9 State Demo
java -cp target/classes com.cafepos.demo.Week9Demo_State

# Run Week 10 MVC Demo
java -cp target/classes com.cafepos.demo.Week10Demo_MVC

# Run Week 10 EventBus Demo
java -cp target/classes com.cafepos.ui.EventWiringDemo

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=OrderFSMTest#cannotCancelAfterReady

# Generate coverage report
mvn test jacoco:report
```

---

## 📊 Key Metrics to Highlight

- **Total Lines of Code:** ~2,500 (business logic)
- **Test Lines:** ~1,500
- **Number of Tests:** 151
- **Test Coverage:** 85% (business logic)
- **Design Patterns:** 9
- **Architecture Layers:** 4
- **Build Time:** <2 seconds
- **Test Execution Time:** <2 seconds

---

**Good luck with your presentation! 🚀**
