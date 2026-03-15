# Inventory Management Backend

Inventory Management Backend is a Spring Boot 3 REST API for managing products, inventory records, and sales. It uses Java 17, Spring Data JPA, MySQL, and Spring Security.

## Tech Stack

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Maven

## Features

- Product CRUD APIs
- Inventory CRUD APIs
- Sales CRUD APIs
- Global exception handling with JSON error responses
- HTTP Basic authentication for write operations
- Environment-variable-based database configuration
- Bean validation on incoming write requests
- OpenAPI and Swagger UI documentation
- Transactional sale creation with stock deduction
- Oversell prevention with conflict responses
- Dedicated inventory restock flow
- Pagination and query-parameter filtering on list endpoints
- Stock movement history for sales and restocks
- Audit timestamps on primary entities
- Service and controller test coverage for key behaviors

## Prerequisites

- Java 17 or later
- MySQL running locally
- Port `8080` available

## Environment Variables

Set these before starting the application:

```bash
export DB_URL=jdbc:mysql://localhost:3306/inventory_management
export DB_USERNAME=root
export DB_PASSWORD=your_password
export APP_SECURITY_USERNAME=admin
export APP_SECURITY_PASSWORD=admin123
export APP_CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:3000,http://127.0.0.1:3000,http://localhost:5173,http://127.0.0.1:5173
```

Use your own local values. Do not commit real credentials.

If a local `.env` file is present in the project root, Spring Boot now loads it automatically during startup.

By default, browser requests are allowed from common local frontend origins on ports `3000` and `5173`. Override `APP_CORS_ALLOWED_ORIGIN_PATTERNS` if your frontend runs elsewhere.

## Database Setup

Create the database before starting the app:

```sql
CREATE DATABASE inventory_management;
```

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, so tables are created and updated automatically on startup.

## Run the Application

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
sh ./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

## Run MySQL With Docker

To run the default MySQL-backed profile locally without installing MySQL directly:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
cp .env.example .env
docker compose up -d
```

This starts MySQL 8 on `localhost:3306` with database `inventory_management`.

Then start the backend with the default profile:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export DB_URL=jdbc:mysql://localhost:3306/inventory_management
export DB_USERNAME=root
export DB_PASSWORD=your_password
export APP_SECURITY_USERNAME=admin
export APP_SECURITY_PASSWORD=admin123
sh ./mvnw spring-boot:run
```

If `.env` exists, you can also just run:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
sh ./mvnw spring-boot:run
```

Or use the helper script after creating `.env`:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
sh ./run-mysql.sh
```

To stop the MySQL container:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
docker compose down
```

Or use:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
sh ./stop-mysql.sh
```

## Run Without MySQL

For local development on a machine without MySQL, use the bundled `dev` profile backed by H2:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
sh ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Or use the helper script:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
sh ./run-dev.sh
```

This starts the API on `http://localhost:8080` with:

- in-memory H2 database
- default write credentials `admin` / `admin123`
- H2 console at `http://localhost:8080/h2-console`
- sample products, inventory, sales, and stock movement history loaded automatically on first start

Use JDBC URL `jdbc:h2:mem:inventory_management_dev` in the H2 console.

## Swagger / OpenAPI

Interactive API documentation is available after startup at:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui/index.html`

The OpenAPI document is available at:

- `http://localhost:8080/v3/api-docs`

Basic health check:

- `http://localhost:8080/api/health`

## Run Tests

```bash
sh ./mvnw test
```

If you see `Unable to locate a Java Runtime`, install Java 17 and make sure `java -version` works in your terminal.

## Smoke Test

With the backend running, you can verify the main local flows with:

```bash
cd /Users/rupa_uppin/Documents/New\ project/Inventory_Management_backend
sh ./smoke-test.sh
```

Optional environment overrides:

```bash
BASE_URL=http://127.0.0.1:8080 \
APP_SECURITY_USERNAME=admin \
APP_SECURITY_PASSWORD=admin123 \
sh ./smoke-test.sh
```

## Authentication

- `GET /api/**` endpoints are public
- `POST`, `PUT`, and `DELETE` endpoints require HTTP Basic authentication
- Browser clients are allowed by default from `http://localhost:3000`, `http://127.0.0.1:3000`, `http://localhost:5173`, and `http://127.0.0.1:5173`

Example authenticated request:

```bash
curl -u admin:admin123 http://localhost:8080/api/products
```

In Swagger UI, use the `Authorize` button and enter the configured HTTP Basic credentials for write endpoints.

## API Overview

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

### Health

- `GET /api/health`

## Example API Calls

Get all products:

```bash
curl http://localhost:8080/api/products
```

Get paged and filtered products:

```bash
curl "http://localhost:8080/api/products?page=0&size=10&category=Electronics&name=lap"
```

Paginated endpoints use `page=0` and `size=20` by default, and reject any `size` greater than `100`.
List responses use a stable pagination envelope:

```json
{
  "items": [],
  "page": 0,
  "pageSize": 20,
  "totalItems": 0,
  "totalPages": 0
}
```

Default sorts are:

- products: `id,asc`
- inventories: `id,asc`
- low-stock inventories: `itemsLeft,asc`
- out-of-stock inventories: `id,asc`
- sales: `saleDate,desc`
- stock movements: `occurredAt,desc`

Create a product:

```bash
curl -X POST http://localhost:8080/api/products \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "category": "Electronics",
    "price": 75000
  }'
```

Create inventory:

```bash
curl -X POST http://localhost:8080/api/inventories \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "initialStock": 20
  }'
```

Restock inventory:

```bash
curl -X POST http://localhost:8080/api/inventories/1/restock \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 10,
    "reason": "New shipment"
  }'
```

Get paged inventory for one product:

```bash
curl "http://localhost:8080/api/inventories?page=0&size=10&productId=1"
```

Get low-stock inventory:

```bash
curl "http://localhost:8080/api/inventories/low-stock?page=0&size=10&threshold=10"
```

Get out-of-stock inventory:

```bash
curl "http://localhost:8080/api/inventories/out-of-stock?page=0&size=10"
```

Create a sale:

```bash
curl -X POST http://localhost:8080/api/sales \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantitySold": 2,
    "priceAtSale": 75000,
    "saleDate": "2026-03-14"
  }'
```

Get paged sales for one product and date range:

```bash
curl "http://localhost:8080/api/sales?page=0&size=20&productId=1&saleDateFrom=2026-01-01&saleDateTo=2026-03-31"
```

Get stock movement history:

```bash
curl "http://localhost:8080/api/stock-movements?page=0&size=20&productId=1&type=RESTOCK"
```

When a sale is created, the backend validates available stock, deducts `itemsLeft`, increments `itemsSold`, and saves the sale in a single transaction. If stock is insufficient, the API returns `409 Conflict`.
Sales and restocks both write stock movement records so inventory-affecting changes are traceable over time.
`Product` and `ProductInventory` responses include `createdAt` and `updatedAt`. `ProductSales` responses include `createdAt`. `StockMovement` responses include both the business event time `occurredAt` and the row creation time `createdAt`.
Low-stock reporting is available through `GET /api/inventories/low-stock`, which returns inventories where `itemsLeft <= threshold`. The threshold defaults to `10`.
Out-of-stock reporting is available through `GET /api/inventories/out-of-stock`, which returns inventories where `itemsLeft = 0`.

Sales are immutable after creation. Generic `PUT` and `DELETE` operations are intentionally not supported.

Inventory stock counters are not generic editable fields. `itemsSold` is driven by sales, and `itemsLeft` is changed through sales and restock operations.
Generic `PUT /api/inventories/{id}` replacements are intentionally not supported.

Read endpoints return response DTOs instead of raw JPA entities. Inventory and sales responses include a lightweight nested `product` summary with `id` and `name`.
List endpoints return a stable paginated DTO with `items`, `page`, `pageSize`, `totalItems`, and `totalPages`, and accept standard pagination parameters such as `page`, `size`, and `sort`.

## Error Handling

Missing resources return structured JSON error responses through the global exception handler. Request validation failures return `400 Bad Request` with field-level validation messages. For example, requesting a missing product returns `404 Not Found` with an error message and request path.

## Future Improvements

- Replace basic auth with JWT-based authentication
- Add explicit domain actions such as sale cancellation or returns
- Enforce stock consistency between inventory and sales
- Add Docker-based local setup
