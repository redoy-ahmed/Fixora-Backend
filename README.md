# Fixora REST API Backend

> **Production REST API Backend for Fixora Shop and Fixora Customer Android Applications**

Fixora Backend is a high-performance, production-grade REST API service built with **Kotlin 2.0**, **Spring Boot 3.3.4**, **Spring Security JWT**, **Spring Data JPA**, **PostgreSQL 16**, **Flyway Database Migrations**, **Docker Compose**, and **Springdoc OpenAPI (Swagger UI)**.

---

## 🏗️ Architecture & Features

- **Dual-App Role Isolation**:
  - **Staff Portal (`/api/v1/staff/*`)**: Role-based access control (`ROLE_OWNER`, `ROLE_MANAGER`, `ROLE_RECEPTIONIST`, `ROLE_TECHNICIAN`, `ROLE_ACCOUNTANT`).
  - **Customer Portal (`/api/v1/customer/*`)**: Secure customer endpoints deriving identity directly from the authenticated JWT token.
  - **Public Verification (`/api/v1/public/*`)**: Public QR code scan lookup and SHA-256 canonical hash verification.
- **Privacy & Security Contract**: Customer endpoints strictly exclude internal technician notes, supplier cost prices, shop margins, or raw unmasked serial/IMEI numbers.
- **Financial Precision**: Monetary values are computed and stored in integer minor units (cents/paisa) in `BIGINT` columns.
- **Docker Ready**: Multi-stage `Dockerfile` and `docker-compose.yml` packaging PostgreSQL 16, Spring Boot API, and pgAdmin 4.

---

## 🌐 Endpoints & Service URLs

### 1. Docker Compose Run (`run-docker.bat` / `docker compose up -d`)
- **Interactive Swagger API UI**: 👉 **`http://localhost:8082/swagger-ui.html`**
- **OpenAPI 3.0 JSON Spec**: 👉 **`http://localhost:8082/api-docs`**
- **Actuator System Health Check**: 👉 **`http://localhost:8082/actuator/health`**
- **Actuator System Metrics**: 👉 **`http://localhost:8082/actuator/metrics`**
- **pgAdmin 4 Web Console**: 👉 **`http://localhost:8081`** (*Email: `admin@fixora.com` | Password: `admin`*)
- **PostgreSQL Database**: `localhost:5432` (*Database: `fixora_db` | User: `postgres` | Password: `postgres`*)

### 2. Local Gradle Run (`.\gradlew.bat bootRun`)
- **Interactive Swagger API UI**: 👉 **`http://localhost:8080/swagger-ui.html`**
- **OpenAPI 3.0 JSON Spec**: 👉 **`http://localhost:8080/api-docs`**
- **Actuator System Health Check**: 👉 **`http://localhost:8080/actuator/health`**

---

## 🛠️ Technology Stack

- **Language**: Kotlin 2.0.20
- **Framework**: Spring Boot 3.3.4 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-actuator`)
- **Database**: PostgreSQL 16
- **Migrations**: Flyway (`src/main/resources/db/migration/V1__init_schema.sql`)
- **Authentication**: Stateless JWT (`io.jsonwebtoken:jjwt-api:0.12.6`)
- **API Docs**: Springdoc OpenAPI (`/swagger-ui.html`)
- **Build Tool**: Gradle Kotlin DSL (`build.gradle.kts`)

---

## 🔑 Pre-seeded Seed Data
- **Shop Staff / Owner**: `karim@techcare.com` / `password123` (`ROLE_OWNER`)
- **Customer**: `rahim@example.com` / `password123` (`ROLE_CUSTOMER`)

---

## 🚀 Quick Start (Docker)

```bash
docker compose up --build -d
```
*(Or double-click `run-docker.bat` on Windows)*

---

## 💻 Local Development (Without Docker Container)

1. Start a local PostgreSQL instance on port `5432` with a database named `fixora_db`.
2. Run Spring Boot application:
   ```bash
   .\gradlew.bat bootRun
   ```

---

## 📄 License
Copyright © 2026 Fixora Platform. All rights reserved.
