FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/user-service.jar /app/user-service.jar

EXPOSE 8501

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]