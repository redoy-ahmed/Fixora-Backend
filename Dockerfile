# Stage 1: Build application using JDK 17
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy gradle files
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Copy source code
COPY src src

# Build Spring Boot executable JAR
RUN ./gradlew bootJar --no-daemon

# Stage 2: Production lightweight JRE 17 runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
