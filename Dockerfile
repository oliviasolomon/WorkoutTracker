# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /workspace

# Copy only pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies offline to speed up build
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application, skipping tests
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /workspace/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
