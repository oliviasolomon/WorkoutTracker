# multi-stage build: build with Maven, run with a slim JRE
# Stage 1: build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
# copy sources (improves caching)
COPY src ./src
RUN mvn -B -DskipTests package

# Stage 2: run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# copy the fat jar (assumes spring-boot-maven-plugin created one under target/)
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 5000
ENV PORT=5000
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]
