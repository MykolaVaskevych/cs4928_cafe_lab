# Nick's Presentation Script
**Total Time:** 3 minutes (1:30 Architecture + 1:30 Trade-offs)

---

## 🎬 TRANSITION IN (at 2:30)
**Oliver says:** "Now Nick will show you the architectural structure behind these patterns."

---

## 📐 PART 1: Architectural Integrity (2:30 - 4:00)

### Section 1: Four-Layer Architecture (2:30 - 3:10) — 40 seconds

**SCREEN ACTION:**
1. Open and display: `diagrams/png/week10_architecture.png` (full screen)
2. Keep diagram visible while talking
3. At 2:50, switch to terminal showing: `tree src/main/java/com/cafepos -L 1 -d`

**EXACT WORDS TO SAY:**

> "Our system follows a clean **four-layer architecture** organized across eighteen packages. Let me show you how this works.
>
> Looking at the diagram, the **Presentation layer** contains OrderController, ConsoleView, and demo classes.
>
> The **Application layer** has CheckoutService and ReceiptFormatter—orchestrating domain operations without I/O.
>
> The **Domain layer** is the largest—we organized it by pattern: separate packages for command, state, menu, observer, payment, pricing, and factory. This makes patterns easy to find.
>
> The **Infrastructure layer** has InMemoryOrderRepository and Wiring for dependency injection.
>
> All dependencies point inward to the domain—that's the Dependency Inversion Principle in action."

**VISUAL SEQUENCE:**
- 2:30-2:45: Point cursor at diagram layers (Presentation → Application → Domain → Infrastructure)
- 2:45-2:55: Circle the domain layer showing multiple packages inside it
- 2:55-3:10: Point to dependency arrows showing they all point toward domain

---

### Section 2: Components & Connectors (3:10 - 4:00) — 50 seconds

**SCREEN ACTION:**
1. Open `src/main/java/com/cafepos/domain/OrderRepository.java` in editor
2. Then open `src/main/java/com/cafepos/infra/InMemoryOrderRepository.java` side-by-side
3. Then open `src/main/java/com/cafepos/infra/Wiring.java`
4. Finally show `src/main/java/com/cafepos/app/events/EventBus.java`

**EXACT WORDS TO SAY:**

> "Let me highlight the key **connectors** in this architecture.
>
> First, the **Repository pattern**—the domain defines the OrderRepository interface with no implementation details. The infrastructure provides InMemoryOrderRepository. This means we could swap in a PostgreSQL implementation without touching domain code.
>
> Second, the **Wiring class** acts as a composition root. It creates all components and wires dependencies in one place. This makes the system testable and maintainable.
>
> Third, the **EventBus** connects components loosely. The Presentation layer can subscribe to domain events without direct coupling to the Application layer.
>
> These connectors maintain clean boundaries—no layer leaks details to others."

**VISUAL SEQUENCE:**
- 3:10-3:25: Show OrderRepository interface (8 lines), then InMemoryOrderRepository implementation (18 lines)
- 3:25-3:40: Show Wiring.java with createDefault() method visible
- 3:40-3:55: Show EventBus.java with emit() method visible
- 3:55-4:00: Return to architecture diagram briefly

**TRANSITION OUT:**
> "Oliver will now demonstrate our testing strategy and code quality."

---

## 🎬 TRANSITION IN (at 6:00)
**Oliver says:** "Nick will finish with our architectural decision records and trade-offs."

---

## 📝 PART 2: Trade-off Documentation (6:00 - 7:30)

### Section 3: ADR Introduction (6:00 - 6:15) — 15 seconds

**SCREEN ACTION:**
1. Open file: `docs/adr/001-layered-monolith-architecture.md` in editor
2. Scroll to show the structure (Status, Context, Decision, Consequences)

**EXACT WORDS TO SAY:**

> "We documented our architectural decisions using Architecture Decision Records. Let me show you the key ADR on choosing a layered monolith over microservices. This captures the context, alternatives, and trade-offs."

**VISUAL SEQUENCE:**
- 6:00-6:15: Show ADR file open with clear headings visible

---

### Section 4: ADR Walkthrough (6:15 - 7:15) — 60 seconds

**SCREEN ACTION:**
1. Keep ADR file open
2. Scroll slowly through sections as you read them
3. Pause on "Decision" and "Consequences" sections

**EXACT WORDS TO SAY:**

> "**Context**: The team needed to decide whether to build separate microservices for Payments, Notifications, and Orders, or use a single layered monolith.
>
> **Alternatives considered**: First, a microservices architecture with separate services communicating via REST and events. Second, a modular monolith with strong package boundaries. Third, a simple layered monolith with clear layers.
>
> **Decision**: They chose the layered monolith.
>
> **Reasoning**: For a POS system of this scale, microservices would add unnecessary complexity—distributed transactions, network latency, and operational overhead. A monolith keeps deployment simple, makes testing easier, and still maintains clean boundaries through layers.
>
> **Consequences**: The positive side—simple deployment as a single JAR, easy testing with no network mocking, and fast in-process communication.
>
> The negative—we can't scale components independently, and team boundaries aren't as clear.
>
> However, the architecture was designed with clean seams for future extraction. See the EventBus—it's in-process now but could easily become a message broker. The repository pattern means we could split persistence later."

**VISUAL SEQUENCE:**
- 6:15-6:30: Scroll to "Context" section
- 6:30-6:45: Scroll to "Alternatives Considered" section
- 6:45-6:55: Scroll to "Decision" section (pause here)
- 6:55-7:10: Scroll to "Consequences" section (pause here)
- 7:10-7:15: Scroll to "Future Migration Path" section

---

### Section 5: Connect to Code (7:15 - 7:30) — 15 seconds

**SCREEN ACTION:**
1. Switch back to architecture diagram: `diagrams/png/week10_architecture.png`
2. Then quickly show EventBus.java
3. Then quickly show OrderRepository.java

**EXACT WORDS TO SAY:**

> "You can see this decision reflected throughout the code. The EventBus demonstrates the future migration path—it's in-process now but designed like a message broker. The repository pattern gives database flexibility. The four layers are strictly enforced—UI never touches infrastructure directly. These seams make future partitioning possible without rewriting."

**VISUAL SEQUENCE:**
- 7:15-7:20: Show architecture diagram with layers highlighted
- 7:20-7:25: Show EventBus.java emit() method
- 7:25-7:30: Show OrderRepository interface and implementation side-by-side

**TRANSITION OUT:**
> "Let me hand back to Oliver for our conclusion."

---

## 📋 NICK'S CHECKLIST

### Files to Have Open (in tabs, ready to switch):
- [ ] `diagrams/png/week10_architecture.png`
- [ ] `src/main/java/com/cafepos/domain/OrderRepository.java`
- [ ] `src/main/java/com/cafepos/infra/InMemoryOrderRepository.java`
- [ ] `src/main/java/com/cafepos/infra/Wiring.java`
- [ ] `src/main/java/com/cafepos/app/events/EventBus.java`
- [ ] `docs/adr/001-layered-monolith-architecture.md`

### Terminal Commands Ready:
```bash
# Show package structure (use at 2:50 if needed)
tree src/main/java/com/cafepos -L 1 -d
```

### Timing Checkpoints:
- **2:30** - Start Section 1 (Architecture Overview)
- **3:10** - Start Section 2 (Connectors)
- **4:00** - Hand off to Oliver
- **6:00** - Start Section 3 (ADR Intro)
- **6:15** - Start Section 4 (ADR Walkthrough)
- **7:15** - Start Section 5 (Connect to Code)
- **7:30** - Hand off to Oliver

### Speaking Tips:
1. **Slow down** - You have 3 minutes total, don't rush
2. **Pause after technical terms** - Give viewers time to read the screen
3. **Use cursor to point** - Circle or underline things on screen as you talk
4. **Practice transitions** - "Let me show you..." "Looking at the diagram..." "You can see here..."
5. **Don't say "I wrote" or "I implemented"** - Say "we", "the system", "this architecture"
6. **If running long** - Cut the EventBus explanation in Section 2 to save 10 seconds

### Voice Tone:
- Professional but conversational
- You're **explaining** the architecture, not defending it
- Speak like a consultant presenting a client's system
- Confidence without arrogance

---

## 🎯 QUICK REFERENCE: What to Show When

| Time | Screen | Action |
|------|--------|--------|
| 2:30 | Architecture Diagram | Point to 4 layers |
| 2:50 | Terminal (optional) | Show 18 packages |
| 3:10 | OrderRepository.java | Show interface |
| 3:15 | InMemoryOrderRepository.java | Show implementation |
| 3:25 | Wiring.java | Show createDefault() |
| 3:40 | EventBus.java | Show emit() |
| 6:00 | ADR file | Show structure |
| 6:15 | ADR file | Scroll through sections |
| 7:15 | Architecture Diagram | Show layers again |
| 7:20 | EventBus.java | Quick flash |
| 7:25 | OrderRepository | Quick flash |

---

**Total Word Count:** ~440 words
**Speaking Pace:** 150 words/minute
**Actual Time:** ~2:55 (5 seconds buffer)

Good luck! 🚀
