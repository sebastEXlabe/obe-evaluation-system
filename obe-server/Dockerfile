# ============ Stage 1: Maven Build ============
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom.xml first, download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Copy source code
COPY src ./src

# Build the application (skip tests - they need a running database)
RUN mvn package -DskipTests -B -q

# ============ Stage 2: Runtime ============
FROM eclipse-temurin:21-jre-alpine

# Install healthcheck tool
RUN apk add --no-cache wget

WORKDIR /app

# Copy the built JAR from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8989

ENTRYPOINT ["java", "-jar", "app.jar"]
