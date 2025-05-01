FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/api-gateway.jar /app/api-gateway.jar

EXPOSE 8502

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]