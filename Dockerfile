FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY src/main/resources/libs/VnCoreNLP-1.2.jar src/main/resources/libs/VnCoreNLP-1.2.jar

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
COPY --from=build /build/src/main/resources/libs/VnCoreNLP-1.2.jar /app/libs/VnCoreNLP-1.2.jar
COPY models/ /app/libs/models/
ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-jar", "app.jar"]