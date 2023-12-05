FROM openjdk:11-jre-slim

ARG JAR_FILE=./build/libs/*-SNAPSHOT.jar

COPY ${JAR_FILE} twithme.jar

ENTRYPOINT ["java","-jar","/twithme.jar"]
