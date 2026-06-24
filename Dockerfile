# ================================
# Stage 1 — BUILD
# Maven compiles the code and creates the jar
# ================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# copy pom.xml first — Docker caches dependencies
# if pom.xml hasn't changed, this layer is reused (faster builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy source code
COPY src ./src

# build the jar, skip tests (tests run in CI separately)
RUN mvn clean package -DskipTests

# ================================
# Stage 2 — RUN
# Lightweight image, just copies the jar from Stage 1
# ================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# copy ONLY the jar from the build stage
# the Maven image, source code, and dependencies are left behind
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]