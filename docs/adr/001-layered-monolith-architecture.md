# ADR 001: Layered Monolith Architecture

## Status
Accepted (Week 10, November 2025)

## Context
The Café POS & Delivery system required an architectural approach that could:
- Support multiple design patterns (Command, State, Composite, etc.)
- Maintain clear separation of concerns
- Enable comprehensive testing
- Remain maintainable as complexity grows
- Provide future scalability options

We needed to choose between microservices, modular monolith, or layered monolith.

## Decision
We chose a **Layered Monolith** with strict four-layer architecture:

1. **Presentation Layer** - UI controllers, views, demo programs
2. **Application Layer** - Use cases, orchestration, event bus
3. **Domain Layer** - Business entities, value objects, interfaces
4. **Infrastructure Layer** - Repositories, adapters, external integrations

Dependencies flow inward: Presentation → Application → Domain ← Infrastructure

## Alternatives Considered

### 1. Microservices Architecture
**Pros:**
- Independent deployment and scaling
- Technology heterogeneity
- Team autonomy
- Fault isolation

**Cons:**
- Distributed transaction complexity
- Network latency overhead
- Operational complexity (monitoring, deployment, debugging)
- Over-engineering for current scale (~2,500 LOC)

### 2. Modular Monolith
**Pros:**
- Module isolation within single deployment
- Potential for future extraction
- Simpler than microservices

**Cons:**
- Requires strict module boundaries enforcement
- Less clear than layer boundaries
- Module coupling risks

### 3. Layered Monolith (Chosen)
**Pros:**
- Clear, well-understood architecture
- Simple deployment (single JAR)
- Easy testing (no network mocking)
- Fast in-process communication
- Sufficient modularity for current scale

**Cons:**
- Cannot scale components independently
- Shared database
- Requires discipline to maintain boundaries

## Rationale

For a POS system of this scope (academic project, ~2,500 LOC), **microservices would be premature optimization**. The operational overhead (service discovery, distributed tracing, eventual consistency) outweighs benefits.

The layered monolith provides:
1. **Testability** - Each layer tested independently (151 tests, 85% coverage)
2. **Maintainability** - Clear responsibilities per layer
3. **Performance** - No network hops for internal calls
4. **Simplicity** - Single build, single deployment
5. **Future-ready** - Clean seams for service extraction if needed

## Implementation Details

### Layer Separation
- **Presentation** (`com.cafepos.ui`) - MVC controllers, views
- **Application** (`com.cafepos.app`) - CheckoutService, ReceiptFormatter, EventBus
- **Domain** (`com.cafepos.domain`) - Order, LineItem, OrderRepository interface
- **Infrastructure** (`com.cafepos.infra`) - InMemoryOrderRepository, Wiring (DI)

### Key Patterns Supporting Architecture
- **Repository Pattern** - Domain defines interface, Infrastructure implements
- **Dependency Injection** - Wiring class as composition root
- **Event-Driven** - EventBus for decoupled communication

## Consequences

### Positive
✅ **Simple Operations** - Single JAR deployment, one database
✅ **Fast Testing** - 151 tests run in <2 seconds (no network I/O)
✅ **Clear Boundaries** - Package structure enforces layer separation
✅ **Low Latency** - In-process method calls (~1ns vs ~1ms network)
✅ **Easy Debugging** - Single process, unified logs

### Negative
❌ **Scaling Limits** - Cannot scale Payments independently from Orders
❌ **Database Coupling** - Shared schema limits polyglot persistence
❌ **Deployment Coupling** - Changes to any layer require full redeployment
❌ **Team Boundaries** - Harder to assign teams to specific components

### Mitigations
We designed **extraction seams** for future microservices:

1. **EventBus** - Currently in-process, designed like message broker
   → Future: Replace with Kafka/RabbitMQ for cross-service events

2. **Repository Pattern** - Domain doesn't know about persistence
   → Future: Each service can own its database

3. **Identified Bounded Contexts**:
   - **Payments** - PCI compliance may require isolation
   - **Notifications** - Async nature suits separate service
   - **Inventory** - Different scaling characteristics

## Future Migration Path

If we outgrow the monolith:

### Phase 1: Extract Notifications Service
- Notifications already event-driven (Observer pattern)
- Replace EventBus with RabbitMQ
- Pull out CustomerNotifier, DeliveryDesk
- Minimal domain changes

### Phase 2: Extract Payments Service
- Payment strategies already isolated
- Create payments database
- Expose REST API for payment processing
- Maintain transactional integrity via Saga pattern

### Phase 3: Extract Inventory Service
- Catalog already separate from Orders
- Independent scaling for read-heavy operations
- Event sourcing for inventory changes

Each extraction leverages existing clean boundaries.

## Validation

After implementing (Weeks 8-11):

✅ **Architecture Integrity** - No layer violations detected
✅ **Performance** - All demos run in <100ms
✅ **Testing** - 85% coverage, all layers independently testable
✅ **Maintainability** - Successfully added 3 new patterns without refactoring layers
✅ **Code Quality** - Clean code principles followed throughout

## Trade-off Analysis

### Why Not Microservices?

**Context of Decision:**
- Project size: ~2,500 LOC business logic
- Team size: 2 developers
- Deployment environment: Academic/development
- Performance requirements: Sub-second response times
- Scaling requirements: Single-instance sufficient

**Microservices Overhead:**
- Service discovery and registration
- API gateway configuration
- Distributed tracing setup
- Multiple deployment pipelines
- Network serialization/deserialization
- Distributed transaction management
- Cross-service testing complexity

**Cost-Benefit:**
For our scale, microservices add 5-10x complexity for marginal benefits. The layered monolith achieves our goals (testability, maintainability, pattern demonstration) with minimal overhead.

### Key Design Decisions

**1. Repository Pattern Over Active Record**
- Decision: Domain interfaces, Infrastructure implements
- Trade-off: More classes, but better testability
- Benefit: Can mock repositories in tests, swap implementations

**2. EventBus Over Direct Calls**
- Decision: Pub-sub event bus for component communication
- Trade-off: Indirection vs. coupling
- Benefit: Decoupled components, easy to add observers

**3. In-Memory Storage Over Database**
- Decision: InMemoryOrderRepository for persistence
- Trade-off: No durability vs. simplicity
- Benefit: Fast tests, no database setup, easy to swap later

**4. MVC Over MVVM**
- Decision: Classic MVC pattern for UI
- Trade-off: Less reactive than MVVM
- Benefit: Simpler for console UI, well-understood pattern

## References
- Clean Architecture (Robert C. Martin)
- Building Microservices (Sam Newman) - "Start with a monolith"
- Domain-Driven Design (Eric Evans) - Layered Architecture pattern
- Patterns of Enterprise Application Architecture (Martin Fowler)
- Course material: Week 10 Layered Architecture lab

## Revision History
- 2025-11-25: Initial decision (Week 10)
- Author: Nick & Oliver
- Reviewers: Course coordinator

## Related ADRs
- (Future) ADR 002: Event-Driven Communication Strategy
- (Future) ADR 003: Testing Strategy and Coverage Targets
- (Future) ADR 004: Migration to Microservices (if needed)
