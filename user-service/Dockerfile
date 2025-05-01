FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/user-service.jar /app/user-service.jar

EXPOSE 8501

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]