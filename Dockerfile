# Multi-stage Dockerfile — proven, tag-only base images
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only what we need early so Docker layer caching helps in future builds
COPY pom.xml .
COPY src ./src

# Build the application (skip tests for faster CI builds)
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the Spring Boot jar produced in the build stage
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
