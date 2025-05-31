# Use an official OpenJDK 17 image as the build environment
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the Maven descriptor and download dependencies first (build cache optimization)
COPY pom.xml .
COPY src ./src

# Package the Spring Boot application
RUN mvn clean package -DskipTests

# Use a minimal JDK 17 runtime image
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the jar from the build image
COPY --from=build /app/target/my-api-*.jar app.jar

# Expose port (default Spring Boot port, adjust if needed)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]