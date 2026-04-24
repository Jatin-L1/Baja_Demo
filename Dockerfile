# ─── Stage 1: Build the JAR ───────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first (layer caching — only re-downloads deps when pom changes)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make the wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ─── Stage 2: Minimal runtime image ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only the built JAR from stage 1
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Render sets PORT automatically via env var; default 8080
EXPOSE 8080

# JVM tuning for low-memory containers (Render free = 512MB RAM)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
