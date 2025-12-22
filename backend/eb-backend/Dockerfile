# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
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
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application with increased memory
# -Xmx512m: Maximum heap size of 512MB
# -Xms256m: Initial heap size of 256MB
# SPRING_PROFILES_ACTIVE will be set by Render env vars, but we keep prod as fallback
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]

