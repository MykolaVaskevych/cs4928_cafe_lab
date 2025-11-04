# Week 8 Reflection: Command and Adapter Patterns

## Part A: Command Pattern

### Where does Command decouple UI from business logic in your codebase?

The Command pattern creates a clean separation between the invoker (UI/buttons) and the receiver (business logic) through three key layers:

#### 1. The Invoker Layer (PosRemote)

`PosRemote` represents the UI layer - it's just a collection of programmable button slots. It has zero knowledge about what those buttons actually do. When you press slot 0, the remote doesn't know whether it's adding an item, processing payment, or marking an order ready. It just calls `execute()` on whatever command is bound to that slot.

This means I can:
- Rebind buttons at runtime without touching any domain code
- Add new buttons without modifying existing functionality
- Swap out entire button configurations for different user roles

#### 2. The Command Layer (AddItemCommand, PayOrderCommand, etc.)

Commands are thin wrappers that translate a button press into a receiver call. They store the parameters needed for the operation but contain zero business logic themselves. `AddItemCommand` doesn't know how to create products or add them to orders - it just knows to call `service.addItem(recipe, qty)`.

This decoupling means:
- UI actions are serializable objects (can be queued, logged, transmitted)
- Commands can be composed into macros without the invoker knowing
- Undo becomes trivial - just ask the command to reverse itself

#### 3. The Receiver Layer (OrderService)

`OrderService` is where actual domain work happens. It knows about `ProductFactory`, `Order`, `LineItem`, and `PaymentStrategy`. But it has no idea it's being called by commands - it's just a clean façade over the domain.

This isolation means:
- Business logic stays testable in isolation
- Domain model never depends on UI concepts
- Commands and receivers evolve independently

### The Decoupling in Action

Before Command pattern, a hypothetical button press might look like:
```java
// UI code directly manipulating domain - TIGHT COUPLING
button.onClick(() -> {
  Product p = new ProductFactory().create("ESP");
  order.addItem(new LineItem(p, 1));
  // now the UI knows about ProductFactory, Order, LineItem, Product...
});
```

After Command pattern:
```java
// UI just knows about commands - DECOUPLED
remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
remote.press(0); // that's it
```

The UI (PosRemote) only depends on the Command interface. The business logic (OrderService, Order, etc.) only depends on domain types. The command sits in the middle as a translator, but neither side depends on it - it depends on them.

### Real Benefits in Our Codebase

1. **Undo Stack**: `PosRemote` maintains a history without knowing what's undoable. Each command implements its own undo logic.

2. **Macro Commands**: `MacroCommand` bundles multiple operations (e.g., "Lunch Combo" = add sandwich + add drink + apply discount) without the invoker or receivers knowing about the composition.

3. **Testing**: I can test `OrderService` without any UI. I can test `PosRemote` with mock commands. I can test commands individually with fake services.

4. **Flexibility**: Want to bind a different recipe to button 0? Just `setSlot(0, new AddItemCommand(service, "LAT", 1))`. No domain code changes.

## Part B: Adapter Pattern

### Why is adapting the legacy printer better than changing your domain or vendor class?

Modifying either the vendor class or our domain code would be a violation of the Open/Closed Principle and would introduce coupling, maintenance burden, and risk. Here's why adapting is superior:

#### 1. We Don't Control the Vendor Code

`LegacyThermalPrinter` is a third-party dependency. It lives in the `vendor.legacy` package to signal "this is not ours." If we modify it:

- **Updates break our changes**: Next vendor release overwrites our modifications
- **Warranty/support issues**: Vendor won't support modified code
- **Legal/licensing problems**: We might violate the library's license by modifying it
- **No access to source**: In real scenarios, this might be a compiled .jar we can't even modify

The adapter wraps the vendor code without touching it. When the vendor updates their printer library, our adapter might need adjustments, but the vendor class itself remains pristine.

#### 2. Our Domain Should Not Know About Printers

If we changed our domain code (Order, ReceiptPrinter from Week 6, etc.) to directly accommodate the `byte[]` protocol:

```java
// BAD: Domain polluted with printing concerns
public class ReceiptPrinter {
  public byte[] format(...) { // returns bytes now?
    // our clean formatting logic now tied to ESC/POS protocol
  }
}
```

This would:
- **Violate SRP**: `ReceiptPrinter` should format text, not worry about byte encodings
- **Leak infrastructure details**: The domain shouldn't know about thermal printers, serial ports, or ESC/POS
- **Break existing code**: Everything that uses `ReceiptPrinter` expects a String, not byte[]
- **Kill flexibility**: What if we want to print to a web page next? Or a PDF? Now domain code is locked to byte arrays

Our domain should express **what** needs to be printed (receipt text) in its natural form (String). The adapter translates to **how** a specific device wants it (byte[]).

#### 3. The Adapter Isolates the Impedance Mismatch

The adapter exists precisely to bridge incompatible interfaces without polluting either side:

```java
// Target interface (what our app expects)
interface Printer {
  void print(String receiptText);
}

// Adaptee (what the vendor provides)
class LegacyThermalPrinter {
  void legacyPrint(byte[] payload);
}

// Adapter (the bridge)
class LegacyPrinterAdapter implements Printer {
  public void print(String receiptText) {
    byte[] escpos = receiptText.getBytes(UTF_8);
    adaptee.legacyPrint(escpos);
  }
}
```

The conversion logic (String → byte[]) lives in **exactly one place**. If we need to change encoding or add ESC/POS control codes, we modify the adapter only.

#### 4. Adapter Preserves Replaceability

With the adapter:

```java
// Today: legacy thermal printer
Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());

// Tomorrow: modern cloud printer
Printer printer = new CloudPrinterAdapter(new CloudPrintService());

// Day after: console for testing
Printer printer = text -> System.out.println(text);
```

Our checkout code just calls `printer.print(receiptText)`. The concrete printer is swappable at runtime. If we had modified the domain to use byte[], we'd be locked in.

#### 5. Real Scenario Benefits

In our codebase:

- **Week 6's `ReceiptPrinter`** continues to return clean formatted Strings
- **Week 8's adapter** translates those Strings to byte[] for the legacy device
- **Future Week 9** might add a PDF printer adapter or an email printer adapter
- **None of this changes Order, PricingService, or any domain logic**

If we had modified the domain or vendor code, every new printer type would require revisiting core business logic. With adapters, we just add a new adapter implementation.

### The Adapter Pattern in Our Tests

Our test `AdapterPatternTest` uses a `FakeLegacyPrinter` that captures the byte payload. This proves:

1. **Conversion happens**: `print("ABC")` yields 3 bytes
2. **Content preserved**: Decoding the bytes gives back the original String
3. **Domain unaffected**: We test the adapter in isolation without touching Order or ReceiptPrinter

## Summary: Clean Orchestration and Safe Integration

### Command Pattern Delivers Clean Orchestration

- **Invoker** (PosRemote): knows about slots and history, nothing about domain
- **Commands**: thin translators, no logic
- **Receiver** (OrderService): pure domain operations, no UI knowledge

Press button → execute command → call receiver → domain work happens. Each layer depends only on abstractions below it. UI and domain never directly couple.

### Adapter Pattern Delivers Safe Integration

- **Target** (Printer interface): what our app wants
- **Adaptee** (LegacyThermalPrinter): what the vendor provides
- **Adapter** (LegacyPrinterAdapter): the translator

We integrate the legacy device without modifying vendor code or polluting our domain. When requirements change (new printer, new encoding), we adapt the adapter, not the core system.

### The Flow in Week 8

```
Cashier
  ↓ press button
PosRemote (invoker)
  ↓ execute()
AddItemCommand
  ↓ addItem()
OrderService (receiver)
  ↓ domain operations
Order, ProductFactory, PaymentStrategy
```

After payment:
```
ReceiptPrinter (returns String)
  ↓ format()
LegacyPrinterAdapter
  ↓ print(String)
LegacyThermalPrinter
  ↓ legacyPrint(byte[])
Serial Port / ESC-POS Device
```

Both patterns achieved their goals: Command decoupled UI from business logic, Adapter integrated a legacy component without contaminating the domain. The system remains open for extension (new commands, new printers) and closed for modification (core domain stays stable).
