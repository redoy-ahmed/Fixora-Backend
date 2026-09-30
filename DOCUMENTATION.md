# Fixora Backend — Complete Developer & Architecture Documentation

## Table of Contents
1. [Introduction](#1-introduction)
2. [Security & Authentication Model](#2-security--authentication-model)
3. [Database Schema (12 Tables)](#3-database-schema-12-tables)
4. [API Endpoints Reference](#4-api-endpoints-reference)
   - [Staff Operations (`/api/v1/staff/*`)](#staff-operations-apiv1staff)
   - [Customer Operations (`/api/v1/customer/*`)](#customer-operations-apiv1customer)
   - [Public Verification (`/api/v1/public/*`)](#public-verification-apiv1public)
5. [SHA-256 Canonical Hashing Engine](#5-sha-256-canonical-hashing-engine)
6. [Docker & Environment Configuration](#6-docker--environment-configuration)

---

## 1. Introduction

**Fixora Backend** provides the backend REST API infrastructure for both the **Fixora Shop** staff app and **Fixora Customer** self-service app. It handles device intake, diagnostic estimates, repair state machine transitions, payments, itemized invoices, warranties, and cryptographic repair record verification.

---

## 2. Security & Authentication Model

Fixora Backend enforces **Stateless JWT Authentication** with strict dual-portal request matcher rules in `SecurityConfig.kt`:

```text
Incoming Request -> JwtAuthenticationFilter -> JwtTokenProvider -> SecurityContextHolder
```

### Security Matchers Rules:
- `/api/v1/staff/auth/**`, `/api/v1/customer/auth/**`, `/api/v1/public/**`, `/swagger-ui.html` $\rightarrow$ **Public Access**
- `/api/v1/staff/**` $\rightarrow$ Requires **Staff Roles** (`ROLE_OWNER`, `ROLE_MANAGER`, `ROLE_RECEPTIONIST`, `ROLE_TECHNICIAN`, `ROLE_ACCOUNTANT`)
- `/api/v1/customer/**` $\rightarrow$ Requires **`ROLE_CUSTOMER`**

### Customer Identity Contract:
All customer REST endpoints extract the customer's UUID directly from `SecurityContextHolder.getContext().authentication.principal`. This guarantees that customers can only view and manage their own devices, repair jobs, invoices, and warranties.

---

## 3. Database Schema (12 Tables)

The PostgreSQL schema is managed via Flyway migration script `V1__init_schema.sql`.

```text
                             +-------------------+
                             |    staff_users    |
                             +-------------------+
                                       ^
                                       |
 +-------------------+       +-------------------+       +-------------------+
 |     customers     | <---- |    repair_jobs    | ----> |      devices      |
 +-------------------+       +-------------------+       +-------------------+
           ^                           |                           ^
           |                           v                           |
 +-------------------+       +-------------------+                 |
 |customer_credentials|      |     estimates     |                 |
 +-------------------+       +-------------------+                 |
                                       |                           |
                             +-------------------+                 |
                             |     invoices      |                 |
                             +-------------------+                 |
                                       |                           |
                             +-------------------+                 |
                             |     payments      |                 |
                             +-------------------+                 |
                                       |                           |
                             +-------------------+                 |
                             |    warranties     | ────────────────┘
                             +-------------------+
```

### Monetary Fields:
All currency values (e.g. `estimated_cost_cents`, `parts_cost_cents`, `grand_total_cents`) are stored in **`BIGINT` minor units (cents/paisa)**.

---

## 4. API Endpoints Reference

### Staff Operations (`/api/v1/staff/*`)
- `POST /api/v1/staff/auth/login` — Staff User Login
- `GET /api/v1/staff/dashboard/kpis` — Shop KPI overview (Today's jobs, In Repair, Revenue, Low Stock)
- `GET /api/v1/staff/customers` | `POST /api/v1/staff/customers` — Customer directory management
- `GET /api/v1/staff/devices` | `POST /api/v1/staff/devices` — Device registration & Device Passport generation
- `GET /api/v1/staff/repairs` | `POST /api/v1/staff/repairs` — Create & list repair jobs
- `PATCH /api/v1/staff/repairs/{id}/status` — Update repair state (`RECEIVED` $\rightarrow$ `DELIVERED`)
- `GET /api/v1/staff/inventory` | `POST /api/v1/staff/inventory` — Spare parts inventory & SKU management

### Customer Operations (`/api/v1/customer/*`)
- `POST /api/v1/customer/auth/login` — Customer Login
- `POST /api/v1/customer/auth/register` — Self-Service Customer Registration
- `GET /api/v1/customer/devices` — Signed-in customer's devices & Device Passport records
- `GET /api/v1/customer/repairs` — Customer-safe repair job progress tracking & estimate approval

### Public Verification (`/api/v1/public/*`)
- `GET /api/v1/public/verify/{recordHash}` — Public REST verification endpoint for QR code scans
- `GET /api/v1/public/device/{publicDeviceId}` — Public device summary for QR scans

---

## 5. SHA-256 Canonical Hashing Engine

When a repair job reaches `DELIVERED` status, `HashVerificationService` computes a deterministic SHA-256 hash of the canonical repair event payload:

```kotlin
val canonicalJson = "{\"deviceId\":\"${job.device.publicDeviceId}\",\"job\":\"${job.jobNumber}\",\"status\":\"${job.status}\"}"
val recordHash = sha256(canonicalJson)
```

The computed hash is stored in `repair_jobs.record_hash` and verified on public QR code scans.

---

## 6. Docker & Environment Configuration

### Management URLs (Port 8082):
- **Spring Boot REST API**: `http://localhost:8082`
- **Swagger Interactive API Docs**: `http://localhost:8082/swagger-ui.html`
- **pgAdmin DB UI**: `http://localhost:8081` (`admin@fixora.com` / `admin`)
- **PostgreSQL Database**: `localhost:5432` (`fixora_db` / `postgres` / `postgres`)

### `application.yml` Properties:
```yaml
spring:
  datasource:
    url: ${JDBC_DATABASE_URL:jdbc:postgresql://localhost:5432/fixora_db}
    username: ${JDBC_DATABASE_USERNAME:postgres}
    password: ${JDBC_DATABASE_PASSWORD:postgres}
jwt:
  secret: ${JWT_SECRET:...}
  expiration-ms: 86400000 # 24 Hours
```

### Connecting pgAdmin to PostgreSQL:
When pgAdmin launches at `http://localhost:8081`:
1. Login with `admin@fixora.com` / `admin`.
2. Add New Server:
   - **Host name**: `postgres` (or `fixora-postgres`)
   - **Port**: `5432`
   - **Maintenance database**: `fixora_db`
   - **Username**: `postgres`
   - **Password**: `postgres`
