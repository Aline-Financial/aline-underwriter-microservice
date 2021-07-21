FROM openjdk:8-jdk-alpine
EXPOSE 8071
COPY underwriter-microservice/target/underwriter-micrservice-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
