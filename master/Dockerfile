FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the packaged jar file into the container
COPY target/master-1.0-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
