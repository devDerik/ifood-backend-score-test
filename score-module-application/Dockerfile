FROM openjdk:8-jdk-alpine

RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/score-module-application-0.0.1-SNAPSHOT.jar $PROJECT_HOME/score-app.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-Dspring.data.mongodb.uri=mongodb://mongodb:27017/score-db","-Djava.security.egd=file:/dev/./urandom","-jar","./score-app.jar"]