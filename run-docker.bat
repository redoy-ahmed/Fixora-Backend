@echo off
echo =======================================================================
echo         Fixora Complete Platform (PostgreSQL + API + Web Portal)
echo =======================================================================
echo.

cd /d "%~dp0"

echo Building and starting Docker services...
docker compose up --build -d

echo.
echo =======================================================================
echo  Services Running:
echo  1. Web Admin Portal:      http://localhost:3000
echo  2. Spring Boot REST API:  http://localhost:8082
echo  3. Swagger API Specs:     http://localhost:8082/swagger-ui.html
echo  4. Actuator Health:       http://localhost:8082/actuator/health
echo  5. pgAdmin Web Console:   http://localhost:8081 (admin@fixora.com / admin)
echo  6. PostgreSQL DB:         localhost:5432 (fixora_db / postgres / postgres)
echo =======================================================================
pause
