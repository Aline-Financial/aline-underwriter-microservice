FROM openjdk:8-jdk-alpine
ENV SERVER_PORT=8071
EXPOSE $SERVER_PORT
COPY underwriter-microservice/target/underwriter-micrservice-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
