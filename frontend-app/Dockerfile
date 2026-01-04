# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build WAR (Spring Boot)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Java
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the WAR file from build stage
COPY --from=build /app/target/frontend-app.war app.war

# Expose port (Render injecte la variable PORT)
EXPOSE 8080

# Health check - timeouts plus permissifs pour éviter les faux positifs
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the Spring Boot application
# Use PORT and BACKEND_URL environment variables from Render
# IMPORTANT: BACKEND_URL doit être en HTTPS sur Render (défini dans render.yaml)
# Augmentation de la mémoire pour éviter les OutOfMemoryError
ENTRYPOINT ["sh", "-c", "java -Xmx400m -Xms200m -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -Dbackend.url=${BACKEND_URL:-https://electricity-business-backend-jvc9.onrender.com} -jar app.war"]
