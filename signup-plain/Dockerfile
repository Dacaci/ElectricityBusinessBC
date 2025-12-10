# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build JAR (Spring Boot)
COPY src ./src
COPY src/main/webapp ./src/main/webapp
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Java
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/signup-plain.jar app.jar

# Expose port (Render injecte la variable PORT)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=90s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the Spring Boot application
# Use PORT environment variable from Render
ENTRYPOINT ["sh", "-c", "java -Xmx512m -Xms256m -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"]
