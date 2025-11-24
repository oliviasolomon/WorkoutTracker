# ---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build

# working dir
WORKDIR /app

# copy pom and source
COPY pom.xml .
# Copy source tree (this assumes your project is in the usual Maven layout)
COPY src ./src

# Build and ensure Spring Boot repackages the jar as a runnable fat-jar
# spring-boot:repackage ensures the final JAR has the boot loader & Main-Class manifest
RUN mvn -B -ntp -DskipTests package spring-boot:repackage

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Expose default port
EXPOSE 8080

# Copy fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user and give ownership
RUN useradd --create-home --uid 1000 appuser \
    && chown -R appuser:appuser /app

USER appuser

# Run the app (Spring Boot fat jar)
ENTRYPOINT ["java","-jar","/app/app.jar"]
