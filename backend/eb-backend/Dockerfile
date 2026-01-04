# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
# IMPORTANT: -DskipTests est nécessaire car les tests nécessitent une base de données
# qui n'est pas accessible pendant le build Docker sur Render
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/eb-backend-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check - increased start period for database connection
# IMPORTANT: Utiliser ${PORT:-8080} pour que Render puisse injecter le port
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with increased memory
# -Xmx512m: Maximum heap size of 512MB
# -Xms256m: Initial heap size of 256MB
# IMPORTANT: Render injecte la variable PORT, Spring Boot la lit automatiquement depuis application-prod.yml
# Force prod profile (Render sets SPRING_PROFILES_ACTIVE=prod via env vars, but we keep this as fallback)
ENTRYPOINT ["sh", "-c", "java -Xmx512m -Xms256m -Dspring.profiles.active=prod -Dserver.port=${PORT:-8080} -jar app.jar"]

