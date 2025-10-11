# ==========================
# Stage 1: Build
# ==========================
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /workspace

# Copy only pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies (offline) — speeds up subsequent builds
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the app
RUN mvn package -DskipTests

# ==========================
# Stage 2: Runtime
# ==========================
FROM eclipse-temurin:17-jdk-alpine

# Set working directory in runtime container
WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/target/*.jar app.jar

# Expose port (adjust if different)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
