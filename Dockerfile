# ================= BUILD STAGE =================
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy toàn bộ project vào container
COPY . .

# Build project (skip test nếu muốn nhanh)
RUN mvn clean package -DskipTests


# ================ RUNTIME STAGE =================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy JAR app đã build từ stage builder
COPY --from=builder /app/target/*.jar app.jar

# Copy file thư viện VnCoreNLP
COPY src/main/resources/libs/VnCoreNLP-1.2.jar /app/libs/VnCoreNLP-1.2.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-cp", "app.jar:/app/libs/VnCoreNLP-1.2.jar", "org.springframework.boot.loader.JarLauncher"]
