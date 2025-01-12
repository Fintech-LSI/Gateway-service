# Build stage
FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /app

# Copy and prepare dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8222

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "app.jar"]
