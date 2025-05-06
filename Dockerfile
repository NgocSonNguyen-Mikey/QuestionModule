FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Copy VnCoreNLP jar vào đúng vị trí
COPY src/main/resources/libs/VnCoreNLP-1.2.jar /app/src/main/resources/libs/VnCoreNLP-1.2.jar

# Cài đặt VnCoreNLP vào local repo
RUN mvn install:install-file \
    -Dfile=src/main/resources/libs/VnCoreNLP-1.2.jar \
    -DgroupId=vncorenlp \
    -DartifactId=vncorenlp \
    -Dversion=1.2 \
    -Dpackaging=jar

# Build project
RUN mvn clean package -DskipTests

# ---------------------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-jar", "app.jar"]
