FROM openjdk:17-jre-slim

VOLUME /tmp

COPY target/demo-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
