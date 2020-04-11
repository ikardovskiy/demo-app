FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
COPY target/app-0.0.1-SNAPSHOT.jar /opt/app
EXPOSE 8080
CMD ["java", "-jar", "/opt/app/app-0.0.1-SNAPSHOT.jar"]