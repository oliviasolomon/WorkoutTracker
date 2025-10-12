# Build stage
FROM maven:3.8.8-openjdk-17 AS build

WORKDIR /app

# Copy only what we need to leverage layer caching
COPY pom.xml .
COPY src ./src

# Build the app (skip tests for faster builds)
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the fat/boot jar produced by Spring Boot
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 5000

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
