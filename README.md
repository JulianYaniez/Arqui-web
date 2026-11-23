# TicketYa — Event Ticketing & Reservation Platform

TicketYa is a distributed microservices platform for event management, ticket reservations, and payment processing built for Web Architecture (UNICEN), featuring a decoupled React + Vite web client and a Spring Boot microservices backend.

## Authors

- [Kessy, Leonardo]
- [Yañez, Julian Fidel]
- [Tami, Marcos David]
- [Fennema, Agustina Magali]
- [Gomez, Nicolas Valentin]

## Quickstart & Execution

### Prerequisites

- Docker & Docker Compose.
- Ensure the following ports are available on your host machine:
  - `5173`: Frontend (React + Vite dev server)
  - `8080`: API Gateway[metrics](management-service/src/main/java/com/arquiweb/management/metrics)
  - `8081–8085`: Backend Microservices (`ticketing`, `management`, `identity`, `payment`, `catalog`)
  - `5432–5435`: PostgreSQL databases (`ticketing`, `management`, `identity`, `payment`)
  - `27017`: MongoDB (`catalog`)
  - `6379`: Redis (virtual waiting queue)
  - `9092`: Apache Kafka (event broker)
  - `1025` & `8025`: Mailpit (SMTP server & Web UI)
- Java 17 & Maven (optional, required only for local backend builds outside containers).
- Node.js & npm (optional, required only for local frontend builds outside containers).

### Setup & Run

1. Clone the repository: `git clone -b tpe_final_2 https://github.com/JulianYaniez/Arqui-web.git`
2. Enter project root: `cd Arqui-web`
3. Clean any residual volumes from previous deliveries (recommended to prevent database schema or data incompatibilities): `docker compose down -v`
4. Build and start all containers in attached mode (without `-d`) so you can monitor build progress and confirm when all databases, brokers, frontend, and Spring Boot services have finished starting up: `docker compose up --build`

> **Note:** Database healthchecks (`pg_isready` for PostgreSQL and `mongosh` ping for MongoDB) ensure database readiness before launching the dependent Spring Boot services. Precaution is still advised.

### Service Interfaces

Once startup logs stabilize, the main interfaces are available at:

- **Frontend Application:** http://localhost:5173
- **API Gateway & Unified Swagger UI:** http://localhost:8080/swagger-ui.html
- **Mailpit Web Inbox (Email Notifications):** http://localhost:8025

### Data Seeding

Open a new terminal and execute the following requests in order to populate initial development data:

1. Seed Identity Users: `curl -X POST http://localhost:8080/api/identity/dev/seed`
2. Seed Event Catalog: `curl -X POST http://localhost:8080/api/catalog/dev/seed`
3. Seed Ticket Inventory: `curl -X POST http://localhost:8080/api/ticketing/dev/seed`

#### Seeded Test Accounts

All seeded accounts share the password **`Pass_1234`**:

| Role | Email | Password |
| :--- | :--- | :--- |
| `ADMIN` | `admin@gmail.com` | `Pass_1234` |
| `ORGANIZER` | `organizer@gmail.com` | `Pass_1234` |
| `STAFF` | `staff@gmail.com` | `Pass_1234` |
| `CLIENT` | `user@gmail.com` | `Pass_1234` |

## Frontend Application

The web client (`./frontend`) is built with **React**, **Vite**, **JavaScript**, and **CSS**, communicating exclusively with the backend through the **API Gateway** via centralized service modules (`authService`, `eventService`, `ticketService`, `venueService`).

- **Key Views & Flows:** Event browsing and category filtering (`Home`, `Events`, `EventDetails`), user registration/login and profile management (`Login`, `Register`, `Profile`), ticket checkout and wallet (`Checkout`, `MyTickets`), and administrative event/venue management (`Managment`).
- **Module Documentation:** Detailed component breakdown, directory structure, and standalone execution instructions are available in `./frontend/README.md`.

## API Documentation & Testing

Access the unified OpenAPI documentation through the API Gateway:

- **Unified Swagger UI:** http://localhost:8080/swagger-ui.html

Available service definitions in the Gateway dropdown:

- Identity Service: `/api/identity/v3/api-docs`
- Catalog Service: `/api/catalog/v3/api-docs`
- Ticketing Service: `/api/ticketing/v3/api-docs`
- Management Service: `/api/management/v3/api-docs`
- Payment Service: `/api/payment/v3/api-docs`

### Authentication & Payment Notes

- **JWT Authentication:** Protected endpoints require a valid Bearer token issued by **Identity Service**. Authenticate via the login endpoint using any of the seeded accounts above and click **Authorize** in Swagger UI to attach the JWT. The API Gateway validates the signature and propagates `X-Client-Id` and `X-Client-Role` headers to downstream services.
- **Mocked Payment Gateway:** Payment processing is simulated. For a transaction to be approved, the credit card number must have **16 digits** and start with one of the following prefixes: `4111`, `4242`, `5555`, `3782`.

### Development Documentation & Project Management

Detailed architectural models, domain diagrams, and project management tools used during development include:

- **GitHub Wiki:** Technical documentation—including entity-relationship diagrams (ERDs), module diagrams, and sequence diagrams detailing key system interactions—is available in the project's GitHub Wiki.
- **Trello:** Used as the primary project management tool to organize team workflows, assign user stories, and track milestone execution.

## Architecture & Concurrency Highlights

- **Frontend Decoupling:** Single-Page Application (React + Vite) separating UI components (`components/`), views (`pages/`), and HTTP clients (`services/`), consuming backend APIs strictly through the API Gateway.
- **Backend Architecture:** Microservice architecture organized by Package-by-Feature adhering to Domain-Driven Design (DDD) principles.
- **API Gateway & Observability:** Non-blocking asynchronous routing using Spring Cloud Gateway (WebFlux) with centralized JWT validation, role header propagation, and reactive HTTP request tracing.
- **Hybrid Storage & Reporting:** MongoDB for read-heavy event catalogs; isolated PostgreSQL instances per transactional domain, supporting persistent administrative audit logs and TTL-cached event metrics (`JSONB`).
- **Event-Driven Messaging (Apache Kafka):** Asynchronous event streaming across `Catalog`, `Ticketing`, and `Identity` to decouple purchase confirmations, event alerts, and SMTP email delivery via Mailpit.
- **Virtual Waiting Queue (Redis):** Red[src](catalog-service/src)is-backed admission queue in `Ticketing` regulating concurrent checkout traffic via configurable batch sizes and admission TTLs.
- **Concurrency Strategies:**
  - **Optimistic Locking (`@Version`):** Prevents race conditions during high-volume sector capacity updates.
  - **Pessimistic Locking (`@Lock`):** Enforces row-level write locks (`FOR UPDATE`) on reservation refunds and seat reservations.
  - **Circuit Breaker State:** Fast `salesEnabled` domain check in `Ticketing` to reject incoming purchases immediately when an event is halted.

## Academic Integrity Statement

All business logic, domain entities, database persistence, and concurrency strategies were strictly designed and implemented directly by the development team without coding agents. AI tools were used exclusively in an advisory capacity for general research, testing boilerplate and technical documentation polishing.