FROM maven:3.9.5-eclipse-temurin-19 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:19-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-jar", "app.jar"]