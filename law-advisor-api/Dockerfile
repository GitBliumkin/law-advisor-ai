# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy your Java API JAR file into the container
COPY target/law-advisor-api.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the Java application
CMD ["java", "-jar", "app.jar"]
