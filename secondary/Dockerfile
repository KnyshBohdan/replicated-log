# secondary/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the packaged JAR file into the container
COPY target/secondary-1.0-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 50051

ENTRYPOINT ["java", "-jar", "app.jar"]