FROM maven:3.9.6-eclipse-temurin-22

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Copy Maven configuration files
COPY pom.xml .
COPY .mvn ./.mvn
COPY mvnw .
COPY mvnw.cmd .

# Download dependencies (this will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Expose the application port
EXPOSE 8080

# Start the application with hot-reloading in development mode
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true'"] 