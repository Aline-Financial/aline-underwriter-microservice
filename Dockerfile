FROM amazoncorretto:11-alpine-jdk
COPY underwriter-microservice/target/underwriter-microservice-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
