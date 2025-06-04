# ================= BUILD STAGE =================
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy source code vào container
COPY pom.xml .
COPY src ./src

# (Optional) Download dependencies để tăng tốc build lần sau
RUN mvn dependency:go-offline

# Build không tạo jar (có thể dùng `mvn clean install -DskipTests` nếu muốn jar)
# Nhưng nếu chạy trực tiếp, bước này có thể bỏ qua

# Stage chạy ứng dụng trực tiếp


# ================ RUNTIME STAGE =================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy toàn bộ source code vào container
COPY --from=build /app /app

# Cài đặt Maven (cần nếu muốn chạy mvn spring-boot:run)
RUN apt-get update && apt-get install -y maven

# Mở cổng (tuỳ app của bạn)
EXPOSE 8081

# Chạy ứng dụng spring boot từ source
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-Xms2g -Xmx2g"]
