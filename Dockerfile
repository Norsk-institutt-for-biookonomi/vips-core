# Multi-stage build for VIPSCore
FROM maven:3.9.12-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw mvnw.cmd ./
COPY .mvn ./.mvn
COPY pom.xml ./
COPY models_for_docker.xml ./
COPY build_with_models.py ./

# Make mvnw executable
RUN chmod +x mvnw

# Copy source code
COPY src ./src

# Install Python3 and build the application with models
RUN apt-get update && apt-get install -y python3 && \
    python3 build_with_models.py models_for_docker.xml

# Runtime stage
FROM quay.io/wildfly/wildfly:34.0.1.Final-jdk17

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.net.preferIPv4Stack=true"

# Create deployment directory
USER root
RUN mkdir -p /opt/jboss/wildfly/standalone/deployments

# Copy the built WAR file
COPY --from=builder /app/target/VIPSCore-*.war /opt/jboss/wildfly/standalone/deployments/VIPSCore.war

# Switch back to jboss user
USER jboss

# Expose port 8080
EXPOSE 8080

# Start WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
