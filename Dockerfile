# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /workspace

# Copy only pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies offline
RUN mvn dependency:go-offline

# Copy the full source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine

# Set working directory in container
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /workspace/target/*.jar app.jar

# Expose port (adjust if your app uses a different port)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
