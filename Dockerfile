# Multi-stage Dockerfile for WorkoutTracker (Maven build + lightweight runtime)
# -------------------------
# Build stage
# -------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

# Build context
WORKDIR /app

# Copy maven files first to leverage cache for dependencies
COPY pom.xml mvnw* ./
COPY .mvn .mvn
RUN mvn -B -ntp -DskipTests dependency:go-offline

# Copy source and package
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# -------------------------
# Runtime stage
# -------------------------
FROM eclipse-temurin:17-jre-jammy

# Create a directory for persistent DB file and make it a mount point
RUN mkdir -p /data \
    && chmod 755 /data

# App working dir
WORKDIR /app

# Copy jar from build stage (assumes single artifact in target)
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render sets PORT env; default 8080 locally)
EXPOSE 8080

# Make /data a volume so Render or Docker can mount persistent storage
VOLUME ["/data"]

# Recommended environment variables (can be overridden)
ENV PORT=8080 \
    JAVA_OPTS=""

# Entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT}"]

# End of Dockerfile
