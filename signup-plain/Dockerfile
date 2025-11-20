# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build WAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Tomcat
FROM tomcat:10.1-jdk17-temurin-jammy
WORKDIR /usr/local/tomcat

# Remove default Tomcat applications
RUN rm -rf webapps/*

# Copy the WAR file from build stage
COPY --from=build /app/target/signup-plain.war webapps/ROOT.war

# Expose port
EXPOSE 8080

# Configure Tomcat to use dynamic PORT (default 8080)
ENV PORT=8080
RUN echo '#!/bin/bash\nsed -i "s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/" /usr/local/tomcat/conf/server.xml\nexec catalina.sh run' > /usr/local/tomcat/bin/start.sh \
    && chmod +x /usr/local/tomcat/bin/start.sh

# Health check (using PORT env var)
HEALTHCHECK --interval=30s --timeout=3s --start-period=90s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/ || exit 1

# Run Tomcat with dynamic port
CMD ["/usr/local/tomcat/bin/start.sh"]

