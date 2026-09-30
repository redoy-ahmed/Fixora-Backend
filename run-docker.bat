@echo off
echo Building and Starting Fixora Backend Stack in Docker...
docker compose up --build -d
echo.
echo ============================================================
echo  Fixora Backend Stack is running in Docker!
echo ============================================================
echo  1. Spring Boot REST API:  http://localhost:8082
echo  2. Swagger API Docs:      http://localhost:8082/swagger-ui.html
echo  3. pgAdmin DB UI:         http://localhost:8081 (admin@fixora.com / admin)
echo  4. PostgreSQL DB:        localhost:5432 (fixora_db / postgres / postgres)
echo ============================================================
echo.
pause
