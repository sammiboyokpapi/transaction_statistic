# Use a base image with Java
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/statistic.app-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set the entrypoint command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
