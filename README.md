# Fixora REST API Backend & Unified Docker Stack

> **Production REST API Backend & Unified Stack for Fixora Shop and Fixora Customer Platforms**

Fixora Backend is a high-performance, production-grade REST API service built with **Kotlin 2.0**, **Spring Boot 3.3.4**, **Spring Security JWT**, **Spring Data JPA**, **PostgreSQL 16**, **Flyway Database Migrations**, **Docker Compose**, and **Springdoc OpenAPI (Swagger UI)**.

---

## 🏗️ Architecture & Unified Docker Stack

The Docker Compose file (`docker-compose.yml`) orchestrates the complete unified stack:

1. **`postgres`**: PostgreSQL 16 database with health check.
2. **`backend`**: Spring Boot REST API (`port 8082`).
3. **`web`**: Fixora Admin Web Portal (`port 3000`, React + TypeScript + Nginx).
4. **`pgadmin`**: pgAdmin 4 Database Web Console (`port 8081`).

---

## 🌐 Endpoints & Service URLs

### 1. Docker Compose Unified Stack (`run-docker.bat` / `docker compose up -d`)
- **Fixora Web Admin Portal**: 👉 **`http://localhost:3000`**
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

## 🔑 Pre-seeded Admin Account
- **Shop Staff / Owner**: `karim@techcare.com` / `password123` (`ROLE_OWNER`)

---

## 🚀 Quick Start (Unified Docker)

```bash
docker compose up --build -d
```
*(Or double-click `run-docker.bat` on Windows)*

---

## 📄 License
Copyright © 2026 Fixora Platform. All rights reserved.
