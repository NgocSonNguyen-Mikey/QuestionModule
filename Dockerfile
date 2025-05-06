# ================= BUILD STAGE =================
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ================ RUNTIME STAGE =================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy app jar
COPY --from=builder /app/target/*.jar app.jar

# Copy VnCoreNLP và models vào container
COPY src/main/resources/libs /app/libs

# Chạy app với classpath có cả app.jar và VnCoreNLP.jar
ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-cp", "app.jar:libs/VnCoreNLP-1.2.jar", "org.springframework.boot.loader.JarLauncher"]
