# cs4928_cafe_lab

## Compile

```bash
mvn compile
```

## Run Week Demo

```bash
mvn exec:java -Dexec.mainClass="com.cafepos.demo.Week2Demo"
# Replace Week2Demo with Week3Demo, Week4Demo, etc.
```

## Generate UML Diagram (PNG)

```bash
mvn compile
plantuml -DPLANTUML_LIMIT_SIZE=16384 diagrams/puml/*.puml -o ../png
# Output: diagrams/png/*.png
```

**Note:** Each compile generates a new timestamped diagram file (not overwritten).

**Install Dependencies** (if needed):

```bash
paru -S plantuml graphviz
```

## Architecture: Layering vs Partitioning

We chose a **Layered Monolith** architecture for now because it provides clear separation of concerns (Presentation, Application, Domain, Infrastructure) without the operational complexity of distributed services. This approach keeps deployment simple while maintaining clean boundaries between layers. The domain layer remains independent of UI and persistence, making it testable and portable.

Natural candidates for future partitioning include:
- **Payments**: External payment processing could become a separate service with its own database and transaction management
- **Notifications**: Order updates, SMS/email notifications could be decoupled into an async messaging service
- **Inventory Management**: Stock tracking and supplier integration might scale independently from order processing

If we partition in the future, we would define clear connectors:
- **Event-driven communication**: Domain events published to a message broker (Kafka, RabbitMQ) for async integration
- **REST APIs**: Synchronous HTTP endpoints for direct service-to-service calls where immediate response is needed
- **API Gateway**: Single entry point for external clients, routing to appropriate services

The current EventBus implementation demonstrates the event-driven pattern we would use at scale, but for now it runs in-process rather than across network boundaries. This lets us validate the architecture without premature distribution.

