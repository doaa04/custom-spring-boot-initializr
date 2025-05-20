# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the executable jar file from build context into /app/
COPY target/*.jar app.jar

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]