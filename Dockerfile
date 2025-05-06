FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
COPY libs/VnCoreNLP-1.2.jar /app/libs/VnCoreNLP-1.2.jar
COPY libs/models/ /app/libs/models/

# Debug: kiểm tra trong lúc build
RUN echo "===== /app/libs =====" && ls -al /app/libs && echo "===== /app/libs/models =====" && ls -al /app/libs/models

ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-cp", "app.jar:libs/VnCoreNLP-1.2.jar", "org.springframework.boot.loader.JarLauncher"]
