# Week 9 Reflection: Iterator + Composite + State Patterns

## State Transition Table

| State      | pay                  | prepare                          | markReady               | deliver                          | cancel                  |
|------------|----------------------|----------------------------------|-------------------------|----------------------------------|-------------------------|
| NEW        | ✓ → PREPARING        | ✗ Cannot prepare before pay      | ✗ Not ready yet         | ✗ Cannot deliver yet             | ✓ → CANCELLED           |
| PREPARING  | ✗ Already paid       | ✗ Still preparing...             | ✓ → READY               | ✗ Deliver not allowed before ready | ✓ → CANCELLED         |
| READY      | ✗ Already paid       | ✗ Already prepared               | ✗ Already ready         | ✓ → DELIVERED                    | ✗ Cannot cancel after ready |
| DELIVERED  | ✗ Completed          | ✗ Completed                      | ✗ Completed             | ✗ Already delivered              | ✗ Completed             |
| CANCELLED  | ✗ Cancelled          | ✗ Cancelled                      | ✗ Cancelled             | ✗ Cancelled                      | ✗ Already cancelled     |

**Legend:**
- ✓ = Transition allowed
- ✗ = Transition not allowed (state unchanged)
- → STATE = Target state after successful transition

## Part A: Composite Pattern - Safety vs. Transparency

### Where did you choose safety over transparency in your Composite API and why?

The Composite pattern involves a fundamental trade-off between **transparency** (treating leaves and composites uniformly) and **safety** (preventing invalid operations at compile time). In our implementation, I chose **safety over transparency** in the base `MenuComponent` class by making all operations throw `UnsupportedOperationException` by default.

#### The Safety Choice

```java
public abstract class MenuComponent {
  // Composite ops (unsupported by default → safe)
  public void add(MenuComponent c) {
    throw new UnsupportedOperationException();
  }

  public void remove(MenuComponent c) {
    throw new UnsupportedOperationException();
  }

  public MenuComponent getChild(int i) {
    throw new UnsupportedOperationException();
  }

  // Leaf data (unsupported by default → safe)
  public String name() {
    throw new UnsupportedOperationException();
  }

  public Money price() {
    throw new UnsupportedOperationException();
  }
}
```

#### Why Safety Over Transparency?

**1. Prevents Meaningless Operations**

Without safety guards, a client could attempt `menuItem.add(otherItem)`, which makes no sense - a leaf cannot have children. With our design:

```java
MenuItem item = new MenuItem("Espresso", Money.of(2.50), true);
item.add(new MenuItem("Invalid", Money.of(1.0), false));
// throws UnsupportedOperationException at runtime
```

This fails fast with a clear error rather than silently doing nothing or corrupting state.

**2. Type Safety Where It Matters**

The alternative (full transparency) would require every operation to work on both leaves and composites:

```java
// Transparent but unsafe approach
public void add(MenuComponent c) {
  // MenuItem would just ignore this?
  // Or maintain an empty list?
}
```

This creates ambiguity. Does `add()` on a leaf silently fail? Does it throw? Does it transform the leaf into a composite? All bad options.

**3. Clear Intent at Each Level**

Our design makes the hierarchy explicit:
- `Menu` overrides `add()`, `remove()`, `getChild()` → "I am a composite"
- `MenuItem` overrides `name()`, `price()`, `vegetarian()` → "I am a leaf"
- Neither pretends to be both

When you read the code, the intent is crystal clear. A `MenuItem` is a terminal node that cannot have children.

**4. Runtime Safety Is Better Than Silent Corruption**

The transparency approach would allow:

```java
MenuComponent mystery = getComponent(); // could be Menu or MenuItem
mystery.add(newItem); // compiles fine, but what happens?
```

With safety, this throws immediately if `mystery` is a `MenuItem`. The error is loud and localized. With transparency, the bug might be silent and spread.

#### Where Transparency Still Exists

We **did** maintain transparency in the iteration and printing APIs:

```java
// Both Menu and MenuItem support these uniformly
public abstract Iterator<MenuComponent> iterator();
public abstract void print();
```

This is safe transparency because:
- `MenuItem.iterator()` returns an empty iterator (semantically correct)
- `MenuItem.print()` prints itself (semantically correct)
- No operation is meaningless or dangerous

#### The Balanced Approach

Our design achieves a **safety-first balance**:

1. **Operations that make sense on both types** (iteration, printing) → supported uniformly
2. **Operations that only make sense on composites** (`add`, `remove`, `getChild`) → throw on leaves
3. **Operations that only make sense on leaves** (`price`, `vegetarian`) → throw on composites

This prevents misuse while still allowing clients to traverse and print the tree without knowing whether they're dealing with a `Menu` or `MenuItem`.

#### Real-World Impact

In our demo, we can write:

```java
Menu root = buildMenu();
root.print(); // works uniformly on entire tree

for (MenuItem item : root.vegetarianItems()) {
  // safe: we know these are leaves
  System.out.println(item.price());
}
```

But we **cannot** write:

```java
MenuComponent c = root.getChild(0);
c.add(newItem); // might throw if c is a MenuItem
```

And that's **good**. The compiler can't prevent this (both types extend `MenuComponent`), but the runtime safety net catches it immediately.

### Alternative: Separate Interfaces (Even Safer)

The **safest** approach would be separate interfaces:

```java
interface MenuLeaf {
  String name();
  Money price();
  boolean vegetarian();
}

interface MenuComposite {
  void add(MenuComponent c);
  void remove(MenuComponent c);
  MenuComponent getChild(int i);
}
```

This would make invalid operations impossible at compile time. However, it **kills** transparency - you can no longer treat leaves and composites uniformly, which defeats the point of Composite.

### Summary: Why Our Safety Choice Is Right

Our design:
- ✓ Prevents meaningless operations (can't add children to leaves)
- ✓ Maintains transparency where it makes sense (iteration, printing)
- ✓ Fails fast with clear errors (UnsupportedOperationException)
- ✓ Makes the hierarchy explicit (overrides show intent)
- ✓ Allows uniform traversal (CompositeIterator works on entire tree)

The trade-off is worth it: we lose some compile-time safety (can't prevent `menuItem.add()` at compile time), but we gain practical usability (can traverse trees without type-checking every node).

## Part B: State Pattern - What Becomes Easy

### What new behavior becomes easy with State that was awkward with conditionals?

Before the State pattern, order lifecycle management typically looks like this:

```java
// The old way: enum + conditional chains
class Order {
  private OrderStatus status = OrderStatus.NEW;

  public void pay() {
    if (status == OrderStatus.NEW) {
      status = OrderStatus.PREPARING;
      System.out.println("Paid → Preparing");
    } else if (status == OrderStatus.PREPARING) {
      System.out.println("Already paid");
    } else if (status == OrderStatus.READY) {
      System.out.println("Already paid");
    } else if (status == OrderStatus.DELIVERED) {
      System.out.println("Completed");
    } else if (status == OrderStatus.CANCELLED) {
      System.out.println("Cancelled");
    }
  }

  public void prepare() {
    if (status == OrderStatus.NEW) {
      System.out.println("Cannot prepare before pay");
    } else if (status == OrderStatus.PREPARING) {
      System.out.println("Still preparing...");
    } else if (status == OrderStatus.READY) {
      System.out.println("Already prepared");
    } // ... repeat for all states
  }

  // ... 3 more methods with similar conditional chains
}
```

This approach has several problems that the State pattern solves elegantly:

### 1. Adding New States Requires Shotgun Surgery

**Before (Conditional Approach):**

Want to add a "REFUNDED" state? You must:
- Add `REFUNDED` to the enum
- Find **every** method with status conditionals
- Add a `else if (status == REFUNDED)` branch to **each one**
- Hope you didn't miss any
- Hope each branch does the right thing

That's **5 methods × 6 states = 30 if-else branches** to maintain. Miss one, and you have a bug.

**After (State Pattern):**

```java
class RefundedState implements State {
  @Override public void pay(OrderFSM ctx) {
    System.out.println("[State] Refunded");
  }
  @Override public void prepare(OrderFSM ctx) {
    System.out.println("[State] Refunded");
  }
  @Override public void markReady(OrderFSM ctx) {
    System.out.println("[State] Refunded");
  }
  @Override public void deliver(OrderFSM ctx) {
    System.out.println("[State] Refunded");
  }
  @Override public void cancel(OrderFSM ctx) {
    System.out.println("[State] Refunded");
  }
  @Override public String name() { return "REFUNDED"; }
}
```

That's it. Create one class, implement 6 methods, done. No existing code changes. The Open/Closed Principle in action.

### 2. State-Specific Behavior Is Scattered

**Before (Conditional Approach):**

All behavior for "NEW" state is scattered across 5 methods:

```java
// pay() has: if (status == NEW) { ... }
// prepare() has: if (status == NEW) { ... }
// markReady() has: if (status == NEW) { ... }
// deliver() has: if (status == NEW) { ... }
// cancel() has: if (status == NEW) { ... }
```

To understand what NEW state can do, you must read **all 5 methods** and mentally assemble the picture.

**After (State Pattern):**

```java
final class NewState implements State {
  @Override public void pay(OrderFSM ctx) {
    System.out.println("[State] Paid → Preparing");
    ctx.set(new PreparingState());
  }
  @Override public void prepare(OrderFSM ctx) {
    System.out.println("[State] Cannot prepare before pay");
  }
  @Override public void markReady(OrderFSM ctx) {
    System.out.println("[State] Not ready yet");
  }
  @Override public void deliver(OrderFSM ctx) {
    System.out.println("[State] Cannot deliver yet");
  }
  @Override public void cancel(OrderFSM ctx) {
    System.out.println("[State] Cancelled");
    ctx.set(new CancelledState());
  }
  @Override public String name() { return "NEW"; }
}
```

**Everything** about NEW state is in one place. You can read this class and immediately understand:
- What transitions are allowed (pay → PREPARING, cancel → CANCELLED)
- What transitions are forbidden (prepare, markReady, deliver)
- What messages are printed for each action

This is **locality of behavior** - a powerful property for maintainability.

### 3. Transition Logic Is Implicit and Error-Prone

**Before (Conditional Approach):**

```java
public void pay() {
  if (status == NEW) {
    status = PREPARING;
    System.out.println("Paid → Preparing");
  } else if (status == PREPARING) {
    System.out.println("Already paid");
    // forgot to keep status = PREPARING? Bug!
  } else if (status == READY) {
    System.out.println("Already paid");
    status = PREPARING; // copy-paste error? Bug!
  }
}
```

Each branch must:
1. Check the current status
2. Decide if transition is valid
3. Set the new status (or keep the old one)
4. Print the right message

That's 4 decisions per branch × 5 methods × 5 states = **100 decision points**. Each one is a potential bug.

**After (State Pattern):**

```java
final class PreparingState implements State {
  @Override public void markReady(OrderFSM ctx) {
    System.out.println("[State] Ready for pickup");
    ctx.set(new ReadyState()); // transition happens here
  }
}
```

Each state method either:
- Calls `ctx.set(newState)` → valid transition
- Does nothing → stays in current state

The state machine **cannot** get into an invalid state because only valid transitions change the state. Invalid actions simply don't call `ctx.set()`.

### 4. Testing Becomes a Nightmare

**Before (Conditional Approach):**

To test all transitions, you need:

```java
@Test void test_every_combination() {
  Order order = new Order();
  // Test NEW state
  order.pay(); // should → PREPARING
  order = new Order(); // reset
  order.prepare(); // should reject
  order = new Order(); // reset
  order.markReady(); // should reject
  // ... 25 more test cases
}
```

You must test **every method from every state** = 5 states × 5 methods = 25 test cases, all tangled together.

**After (State Pattern):**

```java
@Test void new_state_pay_transitions_to_preparing() {
  OrderFSM fsm = new OrderFSM();
  assertEquals("NEW", fsm.status());
  fsm.pay();
  assertEquals("PREPARING", fsm.status());
}

@Test void new_state_prepare_is_rejected() {
  OrderFSM fsm = new OrderFSM();
  fsm.prepare();
  assertEquals("NEW", fsm.status()); // still NEW
}
```

Each transition is **independently testable**. If a test fails, you know **exactly** which state and which action are broken. No need to wade through a 50-line conditional method.

### 5. State-Specific Data Becomes Natural

Imagine we want to add a "timeout" feature: orders in PREPARING state should auto-cancel after 30 minutes.

**Before (Conditional Approach):**

```java
class Order {
  private OrderStatus status;
  private LocalDateTime prepStartTime; // used only in PREPARING state

  public void pay() {
    if (status == NEW) {
      status = PREPARING;
      prepStartTime = LocalDateTime.now(); // set here
    }
  }

  public void checkTimeout() {
    if (status == PREPARING && prepStartTime != null) {
      if (Duration.between(prepStartTime, LocalDateTime.now()).toMinutes() > 30) {
        status = CANCELLED;
      }
    }
  }
}
```

State-specific data (`prepStartTime`) pollutes the Order class even though it's only relevant in one state. What if we add timeouts for other states? More fields, more conditionals.

**After (State Pattern):**

```java
final class PreparingState implements State {
  private final LocalDateTime startTime = LocalDateTime.now();

  private boolean isTimedOut() {
    return Duration.between(startTime, LocalDateTime.now()).toMinutes() > 30;
  }

  @Override public void prepare(OrderFSM ctx) {
    if (isTimedOut()) {
      System.out.println("[State] Timed out → Cancelled");
      ctx.set(new CancelledState());
    } else {
      System.out.println("[State] Still preparing...");
    }
  }
}
```

The `startTime` field lives **only in PreparingState**. When the order transitions to READY, the PreparingState instance is discarded, and so is its timeout data. No pollution of the context class.

### 6. Visualizing the State Machine

**Before (Conditional Approach):**

To understand the state machine, you must read 5 methods × 5-way if-else chains and mentally construct the graph. Good luck.

**After (State Pattern):**

The state transition table I created above was **trivial** to produce. Each state class is a row in the table. Each method is a column. Read the method, fill the cell. Done.

The code structure **is** the state machine. No mental gymnastics required.

### 7. Debugging Is Clearer

**Before (Conditional Approach):**

```
Bug: "Order went from READY to NEW somehow"
Debugging: Set breakpoints in all 5 methods, watch status changes, trace through nested conditionals
```

**After (State Pattern):**

```
Bug: "Order went from READY to NEW somehow"
Debugging: Set breakpoint in ReadyState methods. If ReadyState.pay() sets NewState, found it.
```

The bug is **localized** to one state class. You don't have to understand the entire state machine to fix it.

### 8. Extending Behavior Without Modifying States

Want to add logging to every state transition?

**Before (Conditional Approach):**

Add a log statement to **every** `status = ...` assignment. Hope you don't miss any.

**After (State Pattern):**

```java
public final class OrderFSM {
  void set(State s) {
    System.out.println("[FSM] " + state.name() + " → " + s.name());
    this.state = s;
  }
}
```

One line. Logs every transition automatically. No state classes change.

## Summary: State Pattern's Superpowers

| Challenge | Conditional Approach | State Pattern |
|-----------|---------------------|---------------|
| Add new state | Edit 5+ methods, 25+ branches | Add 1 class, 6 methods |
| Understand state behavior | Read scattered conditionals | Read 1 cohesive class |
| Test transitions | 25+ intertwined test cases | 1 independent test per transition |
| State-specific data | Pollutes context class | Encapsulated in state class |
| Visualize state machine | Mental gymnastics | Code structure = diagram |
| Debug invalid transition | Trace through all methods | Isolated to one state class |
| Extend (logging, metrics) | Edit every status assignment | One hook in context |

The State pattern turns a **tangled conditional mess** into a **clean, extensible, testable** state machine. Each state is a first-class object that knows how to behave and what transitions are valid. Adding new states, modifying existing ones, and understanding the lifecycle all become **trivial** operations.

In our order FSM, we can now add states like "ON_HOLD", "PREPARING_DELAYED", "OUT_FOR_DELIVERY", or "REFUNDED" by simply creating new state classes. The existing states don't change. The context doesn't bloat. The tests remain independent.

That's the power of the State pattern: **behavior becomes data, and data is easy to extend**.
