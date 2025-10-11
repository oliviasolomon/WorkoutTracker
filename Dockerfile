# Use Maven + JDK in a single stage to build and run
FROM maven:3.9-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Copy pom.xml and source
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn -B -DskipTests package

# Set environment variable for port
ENV PORT=5000
EXPOSE 5000

# Run the JAR
CMD ["sh", "-c", "java -jar target/*.jar"]
