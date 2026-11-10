# TicketYa — Event Ticketing & Reservation Platform

TicketYa is a distributed microservices platform for event management, ticket reservations, and payment processing built for Web Architecture (UNICEN).

## Authors

- [Kessy, Leonardo]
- [Yañez, Julian Fidel]
- [Tami, Marcos David]
- [Fennema, Agustina Magali]
- [Gomez, Nicolaz Valentin]

## Quickstart & Execution

### Prerequisites

- Docker & Docker Compose.
- Ports 8080 through 8085, 5432 through 5435 and 27017 available.
- Java 17 & Maven (optional, required only for local builds outside containers)

### Setup & Run

1. Clone the repository: git clone -b tpe_final https://github.com/JulianYaniez/Arqui-web.git
2. Enter project root: cd ticketya
3. Launch services and databases: docker compose up --build -d

Note: Database healthchecks (pg_isready for PostgreSQL and mongosh ping for MongoDB) ensure database readiness before launching Spring Boot containers.

### Data Seeding

These must be done in order to work.

1. Seed Event Catalog: POST http://localhost:8080/api/catalog/dev/seed
2. Seed Ticket Inventory: POST http://localhost:8080/api/ticketing/dev/seed

## API Documentation

Access the unified OpenAPI documentation through the API Gateway:

- Unified Swagger UI: http://localhost:8080/swagger-ui.html

Available service definitions in the Gateway dropdown:

- Catalog Service: /api/catalog/v3/api-docs
- Ticketing Service: /api/ticketing/v3/api-docs
- Management Service: /api/management/v3/api-docs
- Payment Service: /api/payment/v3/api-docs

### Development Documentation & Project Management

Detailed architectural models, domain diagrams, and project management tools used during development include:

- GitHub Wiki: Further technical documentation—including entity-relationship diagrams (ERDs), module diagrams, and sequence diagrams detailing key system interactions—is available in the project's GitHub Wiki.
- Task Organization: Trello was used as the primary project management tool to organize team workflows, assign user stories, and track milestone execution.

## Architecture & Concurrency Highlights

- Architecture: Microservice architecture organized by Package-by-Feature adhering to Domain-Driven Design (DDD) principles.
- API Gateway: Non-blocking asynchronous routing using Spring Cloud Gateway (WebFlux).
- Hybrid Storage: MongoDB for read-heavy event catalogs; PostgreSQL for transactional domains.
- Concurrency Strategies:
  - Optimistic Locking (@Version): Prevents race conditions during high-volume sector capacity updates.
  - Pessimistic Locking (@Lock): Enforces row-level write locks (FOR UPDATE) on reservation refunds and seat reservations.
  - Circuit Breaker State: Fast salesEnabled domain check in Ticketing to reject incoming purchases immediately when an event is halted.

## Academic Integrity Statement

All business logic, domain entities, database persistence, and concurrency strategies were strictly designed and implemented directly by the development team without coding agents. AI tools were used exclusively in an advisory capacity for general research, testing boilerplate and technical documentation polishing.
