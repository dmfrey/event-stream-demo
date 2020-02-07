FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine
RUN mkdir /opt/app
EXPOSE 8080
COPY target/event-stream-demo-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/event-stream-demo-0.0.1-SNAPSHOT.jar"]
