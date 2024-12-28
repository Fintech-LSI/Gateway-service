# Use the OpenJDK 17 slim base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory
COPY target/*.jar app.jar

# Expose the default port for the gateway service
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
