FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/CENG453_20242_GROUP15_backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]