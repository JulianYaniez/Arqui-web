# Catalog:

### Epic: Venue Management

**US-CAT-01 \[Delivery 1\]:** As an Organizer, I want to register venues by defining their sectors so that I can host events in them.

- **Acceptance Criteria:**
  - Given valid venue details (name, address, coordinates), the system saves the venue.

  - The organizer can create numbered sectors (requiring row and seat identifiers).

  - The organizer can create unnumbered sectors (requiring a maximum capacity limit).

### Epic: Event Lifecycle

**US-CAT-02 \[Delivery 1\]:** As an Organizer, I want to create an event in draft status and configure sector prices, so that I can publish it when ready.

- **Acceptance Criteria:**
  - Event is created with status `DRAFT`.

  - Organizer can assign prices to previously created venue sectors.

  - Organizer can change status to `PUBLISHED` only if all mandatory fields and at least one sector price are configured.

### Epic: Discovery

**US-CAT-03 \[Delivery 1\]:** As a Client, I want to search for events filtering by multiple criteria and sort them, so that I can find events I want to attend.

- **Acceptance Criteria:**
  - System allows filtering by event type, participant (artist/team), city, and date range.

  - System allows sorting results by date, price (lowest/highest), or relevance.

  - Only `PUBLISHED` events are returned in the search results.

**US-CAT-04 \[Delivery 2\]:** As the System, I want to record client preferences and suggest events based on their purchase history, so that clients discover relevant content.

- **Acceptance Criteria:**
  - System stores user preferences (event types, favorite artists, city).

  - System provides a "Recommended for you" endpoint based on past purchases (e.g., "users who bought this also bought...").

# Ticketing:

### Epic: Purchasing Flow

**US-TCK-01 \[Delivery 1\]:** As a Client, I want to select an event, a sector, and specific seats to initiate the purchase.

- **Acceptance Criteria:**
  - Client can view available capacity for unnumbered sectors.

  - Client can view and select specific available seats for numbered sectors.

  - System enforces a maximum number of tickets per client for the event.

**US-TCK-02 \[Delivery 1\]:** As the System, I want to temporarily reserve the selected places (e.g., for 10 minutes) so that they aren't sold to another user during checkout.

- **Acceptance Criteria:**
  - Selected seats/capacity are locked immediately upon selection.

  - Other users querying availability will see these seats as "reserved/unavailable".

  - The lock has a strict Time-To-Live (TTL) of 10 minutes.

**US-TCK-03 \[Delivery 1\]:** As the System, I want to immediately release the reserved seats if the payment is rejected or the reservation timeframe expires.

- **Acceptance Criteria:**
  - If 10 minutes pass without payment confirmation, seats are freed automatically.

  - If the Payment Gateway returns a rejection, seats are freed immediately.

**US-TCK-04 \[Delivery 1\]:** As the System, I want to issue tickets with a unique QR code after a successful payment, guaranteeing no double-selling under high concurrency.

- **Acceptance Criteria:**
  - If multiple users attempt to reserve the last available seat simultaneously, only one succeeds; others receive a specific error.

  - Upon successful payment, a unique QR code is generated per ticket.

  - Purchase details and payment method are recorded.

### Epic: Crowd Control & Access

**US-TCK-05 \[Delivery 2\]:** As the System, I want to place clients in a virtual queue during the opening of high-demand events.

- **Acceptance Criteria:**
  - Events marked as "high demand" redirect users to a queue upon sales opening.

  - Users receive their current position in the queue.

  - Users are admitted to the purchasing flow in batches.

**US-TCK-06 \[Delivery 2\]:** As Access Staff, I want to scan the QR code at the entrance to validate the ticket and mark it as used.

- **Acceptance Criteria:**
  - System validates the QR code exists and corresponds to the specific event.

  - If valid and unused, system grants access and updates status to `USED`.

  - If already `USED`, system alerts staff (prevents double entry).

# Management

### Epic: Refunds & Cancellations

**US-MGT-01 \[Delivery 1\]:** As a Client, I want to request a ticket refund up to 72 hours before the event starts.

- **Acceptance Criteria:**
  - System accepts request only if current time is > 72 hours before event start.

  - Ticket status changes to `REFUND_REQUESTED`.

**US-MGT-02 \[Delivery 1\]:** As an Administrator, I want to approve/reject refund requests and suspend/cancel events.

- **Acceptance Criteria:**
  - Admin can view a list of pending refund requests.

  - Admin can change event status to `CANCELLED` or `SUSPENDED`.

  - If event is cancelled, all sold tickets automatically transition to a refund state.

**US-MGT-03 \[Delivery 1\]:** As the System, I want to process approved refunds by releasing seats and requesting money back.

- **Acceptance Criteria:**
  - When a refund is approved (or event cancelled), the associated seat/capacity is released back to the Catalog/Ticketing pool.

  - An async request is sent to the Payment Gateway to reimburse the money.

### Epic: Analytics

**US-MGT-04 \[Delivery 2\]:** As an Organizer, I want to consult reports for my events detailing sales and occupancy.

- **Acceptance Criteria:**
  - Organizer can view total tickets sold and refunded per sector.

  - Organizer can view overall occupancy percentage.

  - Organizer can view total revenue generated.

# Identity

### Epic: Security

**US-IDN-01 \[Delivery 2\]:** As the System, I want to authenticate and authorize users using JWT, enforcing role-based access control.

- **Acceptance Criteria:**
  - Users can log in and receive a valid JWT.

  - `CLIENT` role can only access search, purchase, and own tickets.

  - `ORGANIZER` role can manage their own venues/events and view reports.

  - `ADMIN` role can access backoffice endpoints (refunds, cancellations).

  - `ACCESS_STAFF` role can only access the QR scanning endpoint.

### Epic: Communications

**US-IDN-02 \[Delivery 2\]:** As the System, I want to send notifications (email or push) for key events.

- **Acceptance Criteria:**
  - System sends confirmation email with QR codes when a purchase is successful.

  - System sends alert email when an event is cancelled or modified.

  - System sends notification when a user's favorite artist publishes a new event in their city.

# Payment

### Epic: Payment Processing

**US-PAY-01 \[Delivery 1\]:** As the System, I want to process ticket payments through a payment provider so that the purchase flow can be finalized.

- **Acceptance Criteria:**
  - System receives payment details (e.g., credit card token, amount) and a reservation ID from the Ticketing microservice.

  - System communicates with the external payment gateway (mocked for Delivery 1) to authorize the transaction.

  - System returns a synchronous response (APPROVED or REJECTED) to the Ticketing microservice.

  - The transaction details and status are logged in the database.

**US-PAY-02 \[Delivery 1\]:** As the System, I want to trigger a compensation mechanism to the Ticketing microservice if a payment fails, so that reserved seats are immediately released.

- **Acceptance Criteria:**
  - If the payment gateway returns a REJECTED status, the Payment microservice notifies the Ticketing microservice to release the lock on the seats.

  - If the connection to the external gateway times out, it is treated as a failure, and the compensation is triggered.

### Epic: Refunds

**US-PAY-03 \[Delivery 1\]:** As the System, I want to process monetary refunds with the payment gateway when a cancellation or user refund is approved by Management.

- **Acceptance Criteria:**
  - System accepts a refund request containing the original transaction ID from the Management microservice.

  - System communicates with the payment gateway (mocked) to reverse the charge.

  - System updates the internal transaction status to `REFUNDED`.

### Epic: Resilience

**US-PAY-04 \[Delivery 2\]:** As the System, I want to handle external payment gateway failures gracefully using retries and circuit breakers to ensure system stability.

- **Acceptance Criteria:**
  - If the external payment gateway responds with a transient error (e.g., 503 Service Unavailable), the system automatically retries the request up to a configured maximum number of times.

  - If the external gateway fails continuously, a Circuit Breaker opens to prevent system overload, and a fallback response is returned to the user immediately.

  - When the Circuit Breaker is open, pending transactions are safely aborted and compensations are sent to Ticketing.
