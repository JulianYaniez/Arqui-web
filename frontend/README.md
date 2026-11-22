# Arqui-web – Frontend

## 1. Overview

The **Arqui-web** frontend is a web application developed with React that allows users to interact with an event management and ticketing platform.

Through the interface, users can browse published events, view detailed event information, register, log in, manage their profiles, and purchase tickets.

Additionally, the application includes administrative functionalities for managing events and venues.

The frontend is decoupled from the backend and communicates with its services through HTTP requests using the API Gateway.

## 2. Technologies Used

The frontend is developed using the following technologies:

| Technology | Description |
|---|---|
| React | Library used to build user interfaces through reusable components. |
| Vite | Tool used for the development environment and application bundling. |
| JavaScript | Programming language used to implement application logic. |
| CSS | Technology used to define styles and visual presentation. |
| Fetch API | Interface used to perform HTTP requests to the backend. |
| Docker | Tool used to run the development environment through containers. |

## 3. Architecture and Project Structure

The frontend follows a separation of concerns approach, organizing pages, reusable components, services, and styles into independent directories.

This structure improves maintainability, code reusability, and the integration of new functionalities.

### 3.1. Directory Structure

```text
frontend/
├── public/
├── src/
│   ├── components/
│   │   ├── EventCard.jsx
│   │   ├── Footer.jsx
│   │   ├── NavBar.jsx
│   │   ├── PageTitle.jsx
│   │   ├── SearchBar.jsx
│   │   └── VenueForm.jsx
│   │
│   ├── data/
│   │
│   ├── pages/
│   │   ├── Checkout.jsx
│   │   ├── Contact.jsx
│   │   ├── EventDetails.jsx
│   │   ├── Events.jsx
│   │   ├── Home.jsx
│   │   ├── Login.jsx
│   │   ├── Managment.jsx
│   │   ├── MyTickets.jsx
│   │   ├── Profile.jsx
│   │   └── Register.jsx
│   │
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── eventService.js
│   │   ├── ticketService.js
│   │   └── venueService.js
│   │
│   ├── styles/
│   │
│   ├── App.css
│   ├── App.jsx
│   ├── index.css
│   └── main.jsx
│
└── package.json
```

### 3.2. Components

The `components/` directory contains reusable user interface elements.

- **EventCard:** component responsible for visually displaying an event.
- **Footer:** application footer.
- **Navbar:** main navigation bar.
- **PageTitle:** reusable component for page headings.
- **SearchBar:** event search bar.
- **VenueForm:** form used for venue management.

### 3.3. Pages

The `pages/` directory contains the different views of the application.

| Page | Description |
|---|---|
| Home | Main landing page of the platform. |
| Events | Event listing with search and filtering functionalities. |
| EventDetails | Displays detailed information about a selected event. |
| Login | User login form. |
| Register | User registration form. |
| Profile | User profile management. |
| MyTickets | Displays the user's purchased tickets. |
| Checkout | Ticket purchasing process. |
| Managment | Administrative panel for event management. |
| Contact | Contact form. |

### 3.4. Services

The `services/` directory centralizes communication between the frontend and backend.

Services separate API access operations from the presentation logic of pages and components.

- **api.js:** configuration and common functions for HTTP requests.
- **authService.js:** operations related to authentication and users.
- **eventService.js:** operations related to events.
- **ticketService.js:** operations related to tickets.
- **venueService.js:** operations related to venues.

### 3.5. Styles

The `styles/` directory contains CSS files responsible for defining the visual presentation of pages and components.

Stylesheets are kept separate from the interface structure to simplify maintenance and design customization.

## 4. Backend Communication

The frontend communicates with the backend through HTTP requests.

Communication is handled through the **API Gateway**, which acts as the entry point to the different microservices within the system.

Frontend services encapsulate requests and provide access to the functionalities exposed by the backend.

This architecture keeps the user interface and business logic responsibilities decoupled.

## 5. Main Features

The application includes the following functionalities:

### User Management
- User registration.
- User login.
- Profile visualization and management.

### Events
- Browse available events.
- Search and filter events by category.
- View detailed information about each event.

### Tickets
- Ticket selection and purchasing.
- Checkout process.
- View purchased tickets.

### Administration
- Event management.
- Venue management.
- Management of information associated with events.

### Contact
- Contact form for users.

## 6. Installation and Execution

### Prerequisites

To run the frontend locally, the following tools are required:

- Node.js.
- npm.
- Git.

### Installation

Clone the repository:

```bash
git clone <REPOSITORY_URL>
```

Navigate to the frontend directory:

```bash
cd arqui-web/frontend
```

Install dependencies:

```bash
npm install
```

### Development Environment

Start the development server:

```bash
npm run dev
```

Vite will start the application and display the local URL in the terminal so it can be accessed through a web browser.

### Production Build

To generate the production build:

```bash
npm run build
```

The compiled files will be generated in the `dist/` directory.

## 7. Running with Docker

The frontend is part of the Arqui-web development environment, which uses Docker Compose to manage the application's services.

From the project's root directory, start the environment using:

```bash
docker compose up --build
```

To stop the containers:

```bash
docker compose down
```

The Docker Compose configuration is located in the repository's root directory.

## 8. Development Guidelines

To maintain an organized structure and simplify project maintenance, the following practices are recommended:

- Keep page components inside `pages/`.
- Create reusable components inside `components/`.
- Centralize HTTP requests inside `services/`.
- Separate styles into CSS files.
- Avoid duplicating logic across pages and components.
- Maintain a clear separation between the user interface and backend communication.

---

**Project:** Arqui-web  
**Module:** Frontend  
**Main Technologies:** React + Vite