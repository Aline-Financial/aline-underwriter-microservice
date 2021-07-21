FROM openjdk:8-jdk-alpine
EXPOSE 8071
COPY **path/to/target/microservice.jar** app.jar
# ENTRYPOINT ["java", "-jar", "/app.jar"]
