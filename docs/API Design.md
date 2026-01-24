# REST API Design (MVP)

## Overview
This document outlines the REST API design for the **Budget vs Reality Tracker** MVP.  
The API follows RESTful principles with JSON payloads and JWT-based authentication.

---

## Base Configuration

- **Base URL:** `/v1`
- **Auth:** `Authorization: Bearer <JWT>`
- **Content-Type:** `application/json; charset=utf-8`
- **Dates:** ISO-8601 UTC (e.g., `2025-10-31T00:00:00Z`)
- **Money:** Use strings for decimals (e.g., `"12.34"`) to avoid float drift
- **Idempotency:** Use `Idempotency-Key` for safe retries on `POST`

---

## Response Structure

**Success:**
```json
{ "data": <payload>, "meta": { ...optional } }
```

**Error:**
```json
{ "error": { "code": "VALIDATION_ERROR", "message": "…", "details": { … } } }
```

**Pagination:** `?page=1&size=50` → `meta.page`, `meta.size`, `meta.totalPages`, `meta.totalItems`

---

## Endpoints

### Auth

#### POST `/auth/register`
Create a new user.
```json
{ "email": "user@example.com", "first_name": "John", "last_name": "Doe", "password": "S3cret!", "baseCurrency": "USD" }
```

#### POST `/auth/login`
```json
{ "email": "user@example.com", "password": "S3cret!" }
```

#### POST `/auth/refresh`
```json
{ "refreshToken": "<jwt>" }
```

#### POST `/auth/logout`
Invalidate the refresh token.
```json
{ "refreshToken": "<jwt>" }
```

---

### Me

#### GET `/me`
Retrieve user profile and preferences.

#### PATCH `/me`
Update preferences.
```json
{ "baseCurrency": "USD" }
```

---

### Categories

#### GET `/categories`
Optional query: `?includeIncome=true|false`

#### POST `/categories`
```json
{ "name": "Food", "emoji": "🍔" }
```

#### PATCH `/categories/{id}`
```json
{ "name": "Dining Out", "emoji": "🍣", "sortOrder": 3 }
```

#### DELETE `/categories/{id}`

#### PATCH `/categories:reorder`
Bulk reorder.
```json
{ "orders": [ { "id": "uuidA", "sortOrder": 1 }, { "id": "uuidB", "sortOrder": 2 } ] }
```

---

### Budget Periods

#### GET `/budget-periods`
Optional query: `?year=2025&month=10`

#### POST `/budget-periods`
```json
{ "year": 2025, "month": 10 }
```

#### GET `/budget-periods/{id}`  
#### DELETE `/budget-periods/{id}`

##### Allocations (nested)

#### GET `/budget-periods/{id}/allocations`
Retrieve allocations for the budget period.

#### PUT `/budget-periods/{id}/allocations`
Upsert allocations.
```json
{ "allocations": [
  { "categoryId": "uuid-food", "plannedAmount": "350.00" },
  { "categoryId": "uuid-rent", "plannedAmount": "1200.00" }
]}
```

#### PATCH `/budget-allocations/{allocationId}`
```json
{ "plannedAmount": "375.00" }
```

#### DELETE `/budget-allocations/{allocationId}`

---

### Transactions

#### GET `/transactions`
Optional filters: `?from=&to=&categoryId=&min=&max=&q=&page=&size=`

#### POST `/transactions`
```json
{ "categoryId": "uuid-food", "amount": "-23.10", "occurredAt": "2025-10-31T02:15:00Z", "description": "Late sushi" }
```

#### PATCH `/transactions/{id}`
```json
{ "amount": "-22.90", "description": "Corrected tip" }
```

#### DELETE `/transactions/{id}`

#### POST `/transactions:bulk`
```json
{
  "create": [
    { "clientId": "c1", "categoryId": "uuid-food", "amount": "-7.00", "occurredAt": "2025-10-30T17:00:00Z" }
  ],
  "update": [
    { "id": "uuidX", "amount": "-19.50" }
  ],
  "delete": [ "uuidY", "uuidZ" ]
}
```

---

### Budget vs Actual (Summary)

#### GET `/summary/monthly`
Query: `?year=2025&month=10`  
Returns totals and per-category breakdowns.

#### GET `/summary/rollup`
Query: `?from=&to=&granularity=month`  
Returns time-series summary for charts.

#### GET `/summary/categories`
Query: `?from=&to=`  
Returns spending by category for visualization.

---

### Metadata & Health

#### GET `/meta`
Static configuration data such as currencies, locales, and options.

#### GET `/health`
Simple health check (no auth required).

---

## Common Status Codes

| Code | Description |
|------|--------------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Validation error |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 422 | Semantic error |
| 429 | Rate limit |
| 500 | Server error |

---

## Validation Rules

- **BudgetPeriod**: unique `(user, year, month)`; month 1–12.
- **Category**: unique `(user, name)`; sortOrder ≥ 0.
- **Allocation**: plannedAmount ≥ 0.
- **Transaction**: non-zero amount; occurredAt ≤ current time.
- **Ownership**: entities must belong to the authenticated user.
