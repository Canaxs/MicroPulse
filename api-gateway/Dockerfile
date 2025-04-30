FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/api-gateway.jar /app/api-gateway.jar

EXPOSE 8502

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]