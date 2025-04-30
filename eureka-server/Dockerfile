FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/eureka-server.jar /app/eureka-server.jar

EXPOSE 8500

ENTRYPOINT ["java", "-jar", "/app/eureka-server.jar"]