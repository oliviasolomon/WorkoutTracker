# --------------------
# Build stage
# --------------------
# Use Maven JDK 17 image to compile & package
FROM maven:3.9-eclipse-temurin-17 AS build

# Enable BuildKit cache hint usage (optional but recommended when BuildKit is enabled)
# Set workdir for the build
WORKDIR /app

# Copy only pom & maven wrapper first to leverage layer caching for dependencies
# (if .mvn or mvnw don't exist the COPY will just skip them in common CI setups)
COPY pom.xml mvnw* ./ 
COPY .mvn .mvn 2>/dev/null || true

# Pre-resolve dependencies to speed subsequent builds (uses maven cache)
# Note: the --mount=type=cache line requires Docker BuildKit to be enabled.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -DskipTests dependency:go-offline

# Copy the rest of the sources
COPY src ./src
COPY pom.xml ./pom.xml

# Package the app as a runnable fat-jar (spring-boot:repackage ensures manifest)
# Use cached maven repo again for speed
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -DskipTests package spring-boot:repackage

# --------------------
# Runtime stage
# --------------------
FROM eclipse-temurin:17-jre-jammy AS runtime

# Create app dir and data dir and set as working dir
WORKDIR /app
RUN mkdir -p /app/data \
    && chmod 755 /app/data

# Expose the app port
EXPOSE 8080

# Copy the fat jar from build stage
# We use --chown to ensure the non-root user will own the file (works with modern Docker)
COPY --from=build --chown=1000:1000 /app/target/*.jar /app/app.jar

# Create a non-root user and give ownership of /app
RUN useradd --create-home --uid 1000 appuser \
    && chown -R appuser:appuser /app

USER appuser

# Optional: JVM options via environment variable
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start the app. Use exec form so signals are forwarded correctly.
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
