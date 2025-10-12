# Dockerfile — multi-stage build using maven image (no .mvn required)
# Paste as ./Dockerfile

# -------------------------
# Build stage
# -------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

ARG MAVEN_OPTS="-Xmx512m"
ENV MAVEN_OPTS=${MAVEN_OPTS}

WORKDIR /app

# Copy only pom first to leverage Docker layer cache for dependencies
COPY pom.xml ./

# Try to go-offline (best-effort). If this fails it's fine — the next mvn package will download deps.
RUN mvn -B -ntp -DskipTests dependency:go-offline || true

# Copy the rest of the project
COPY src ./src
# If you have other resources (like static frontend under src/main/resources/static), they will be copied above.

# Build the jar (skip tests for quicker CI builds)
RUN mvn -B -ntp -DskipTests package

# -------------------------
# Runtime stage
# -------------------------
FROM eclipse-temurin:17-jre-jammy

# Create directory for persistent H2 DB file and make it a mount point
RUN mkdir -p /data && chmod 755 /data

WORKDIR /app

# Copy built artifact from build stage (assumes single JAR)
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render sets PORT; default 8080 for local)
EXPOSE 8080

# Make /data a volume to allow mounting persistent storage
VOLUME ["/data"]

# Default environment variables (can be overridden in Render / docker run)
ENV PORT=8080 \
    JAVA_OPTS=""

# Run as non-root user for better security (optional, standard uid/gid)
RUN useradd --create-home --uid 1000 appuser && chown -R appuser:appuser /app /data
USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT}"]

