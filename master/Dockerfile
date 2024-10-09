FROM openjdk:17-jdk-slim

WORKDIR /app

# TODO: delete, just for debug
RUN apt-get update && apt-get install -y iputils-ping

# Copy the packaged jar file into the container
COPY target/master-1.0-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
