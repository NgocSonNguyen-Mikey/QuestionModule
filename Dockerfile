# ================== BUILD STAGE ==================
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy code
COPY pom.xml .
COPY src ./src

# Copy VnCoreNLP jar vào resource để đưa vào image sau
COPY libs/VnCoreNLP-1.2.jar src/main/resources/libs/VnCoreNLP-1.2.jar

# Build mà không chạy test
RUN mvn clean package -DskipTests

# ================== RUNTIME STAGE ==================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy Spring Boot jar từ builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy VnCoreNLP jar và models cùng cấp
COPY --from=builder /app/src/main/resources/libs/VnCoreNLP-1.2.jar /app/libs/VnCoreNLP-1.2.jar
COPY models/ /app/libs/models/

# Chạy Spring Boot app với classpath chứa VnCoreNLP
ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-cp", "app.jar:libs/VnCoreNLP-1.2.jar", "org.springframework.boot.loader.JarLauncher"]
