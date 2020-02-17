FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine
VOLUME /tmp
COPY target/event-stream-demo-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "/app.jar"]
