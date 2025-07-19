# ---- Stage 1: Build JAR using Maven ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# ---- Stage 2: Run Spring Boot JAR ----
FROM openjdk:17-jdk-slim
WORKDIR /app
# Create uploads directory with proper permissions
RUN mkdir -p /app/uploads && chmod 755 /app/uploads
COPY --from=builder /build/target/knowallrates-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Create volume for persistent file storage
VOLUME ["/app/uploads"]
ENV JAVA_TOOL_OPTIONS="-Djdk.internal.platform.Metrics.enabled=false"
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
