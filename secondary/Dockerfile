FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/secondary-1.0-SNAPSHOT.jar app.jar

EXPOSE 50051-50055

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["50051"]
