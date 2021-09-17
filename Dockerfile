FROM openjdk:8-jdk-alpine
COPY account-microservice/target/account-microservice-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
