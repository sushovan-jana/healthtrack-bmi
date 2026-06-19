# ==========================================
# Build Stage
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and cache dependencies offline
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy src and compile package
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Run Stage
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user for container security hardening
# Install fontconfig and fonts required for Java AWT and JFreeChart PDF generation
RUN apk add --no-cache fontconfig ttf-dejavu
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build --chown=spring:spring /app/target/bmi-*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Djava.net.preferIPv4Stack=true", "-Duser.timezone=Asia/Kolkata", "-jar", "app.jar"]
