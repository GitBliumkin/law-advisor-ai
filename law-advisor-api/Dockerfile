# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy your Java API JAR file into the container
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set environment variables for PostgreSQL
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/law_advisor_db
ENV SPRING_DATASOURCE_USERNAME=user
ENV SPRING_DATASOURCE_PASSWORD=password

# Command to run the Java application
CMD ["java", "-jar", "app.jar"]
