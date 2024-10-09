FROM openjdk:17-jdk
LABEL authors="wllama"
ARG JAR_FILE=./build/libs/intern-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker" , "-jar" ,"/app.jar" ]