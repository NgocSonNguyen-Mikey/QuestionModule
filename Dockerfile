FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src
# Build project
RUN mvn clean package -DskipTests

# ---------------------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
COPY ./src/main/resources/libs /app/src/main/resources/libs

ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-jar", "app.jar"]
