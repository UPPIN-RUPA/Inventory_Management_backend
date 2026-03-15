# Inventory Management Backend

Inventory Management Backend is a Spring Boot REST API for managing products, stock levels, sales, and stock movement history.

It is designed to support inventory workflows that need more than simple CRUD. The API handles product records, live inventory state, sales deductions, restocks, and audit-friendly stock movement tracking in one backend service.

## What This Backend Does

The backend provides the core server-side logic for:

- product management
- inventory tracking
- sale recording
- stock deduction
- restock operations
- low-stock and out-of-stock visibility
- movement history for inventory auditing
- local development support through H2 and MySQL

It is suitable as both a practical backend project and a full-stack foundation for an inventory dashboard.

## How The Backend Helps

This backend helps by making inventory behavior consistent and enforceable.

It:

- prevents overselling
- centralizes stock rules in one place
- keeps product and inventory data linked cleanly
- exposes filtered and paginated endpoints for frontend consumption
- records stock movements so inventory changes can be explained later
- supports both lightweight local development and MySQL-backed parity

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- H2
- Maven
- Swagger / OpenAPI

## Core Features

- product CRUD APIs
- inventory CRUD APIs
- sales APIs
- stock movement history APIs
- health endpoint
- pagination and filtering on list endpoints
- HTTP Basic authentication for write operations
- JSON error handling
- bean validation for incoming write requests
- transactional stock deduction during sales
- restock workflow with movement logging
- dev profile with seeded demo data
- smoke-test script for local verification

## Functional Areas

### Products

The products domain manages core catalog data such as:

- name
- category
- price

### Inventory

The inventory domain tracks the stock state attached to products.

It supports:

- current stock visibility
- low-stock filtering
- out-of-stock filtering
- inventory creation
- deletion of inventory records

### Sales

The sales domain records outgoing stock and ensures inventory levels update correctly.

It also protects the system from invalid sales such as trying to sell more units than are available.

### Stock Movements

Stock movement history acts as an audit layer.

It explains why stock changed by recording movement types such as:

- sale
- restock

This makes the backend more useful than a simple inventory table because it preserves context around inventory changes.

## API Overview

### Health

- `GET /api/health`

### Products

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Inventories

- `GET /api/inventories`
- `GET /api/inventories/low-stock`
- `GET /api/inventories/out-of-stock`
- `GET /api/inventories/{id}`
- `POST /api/inventories`
- `POST /api/inventories/{productId}/restock`
- `DELETE /api/inventories/{id}`

### Sales

- `GET /api/sales`
- `GET /api/sales/{id}`
- `POST /api/sales`

### Stock Movements

- `GET /api/stock-movements`

## Response Shape

List endpoints return a stable paginated structure:

```json
{
  "items": [],
  "page": 0,
  "pageSize": 20,
  "totalItems": 0,
  "totalPages": 0
}
```

This keeps frontend integrations predictable instead of depending on raw Spring page serialization.

## Authentication

- `GET` endpoints are public
- `POST`, `PUT`, and `DELETE` endpoints require HTTP Basic authentication

Example:

```bash
curl -u admin:admin123 http://localhost:8080/api/products
```

## CORS

Local browser access is enabled by default for common frontend origins:

- `http://localhost:3000`
- `http://127.0.0.1:3000`
- `http://localhost:5173`
- `http://127.0.0.1:5173`

This can be overridden with `APP_CORS_ALLOWED_ORIGIN_PATTERNS`.

## Environment Variables

Use these values for the default MySQL-backed profile:

```bash
export DB_URL=jdbc:mysql://localhost:3306/inventory_management
export DB_USERNAME=root
export DB_PASSWORD=your_password
export APP_SECURITY_USERNAME=admin
export APP_SECURITY_PASSWORD=admin123
export APP_CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:3000,http://127.0.0.1:3000,http://localhost:5173,http://127.0.0.1:5173
```

If a `.env` file exists in the project root, Spring Boot loads it automatically.

## Local Run Options

### Option 1: Run With H2 Dev Profile

This is the easiest local setup and does not require MySQL.

```bash
sh ./run-dev.sh
```

What you get:

- in-memory H2 database
- sample seeded products, inventory, sales, and stock movement history
- default local credentials `admin` / `admin123`
- H2 console at `http://localhost:8080/h2-console`

### Option 2: Run With MySQL

If you want parity with the default profile:

```bash
cp .env.example .env
sh ./run-mysql.sh
```

This uses Docker-backed MySQL if configured with the included compose setup.

### Option 3: Run Directly With Maven

```bash
sh ./mvnw spring-boot:run
```

Or for the H2 dev profile:

```bash
sh ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Docker MySQL Setup

To run the MySQL container manually:

```bash
cp .env.example .env
docker compose up -d
```

Stop it with:

```bash
docker compose down
```

## Documentation and Health

After startup:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Swagger UI alt path: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/api/health`

## Testing

Run the automated test suite:

```bash
sh ./mvnw test
```

Run the local smoke check against a running backend:

```bash
sh ./smoke-test.sh
```

Optional overrides:

```bash
BASE_URL=http://127.0.0.1:8080 \
APP_SECURITY_USERNAME=admin \
APP_SECURITY_PASSWORD=admin123 \
sh ./smoke-test.sh
```

## Why This Backend Is Useful

This backend is useful as:

- a backend engineering project with real business logic
- a full-stack API foundation for inventory dashboards
- a clean example of transactional stock handling
- a local-development-friendly Spring Boot service
- a portfolio project that shows validation, authentication, pagination, and audit tracking

## Summary

Inventory Management Backend is more than a CRUD API. It provides the operational rules behind inventory workflows, including stock protection, movement history, health checks, frontend-ready pagination, local development profiles, and API documentation.
