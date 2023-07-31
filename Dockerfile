FROM openjdk:17-slim
ARG JAR_FILE=build/libs/coclimb-0.0.1-SNAPSHOT.jar
WORKDIR /app
COPY $JAR_FILE /app/coclimb-api.jar
CMD [ "java", "-jar","-Dspring.profiles.active=dev", "coclimb-api.jar" ]