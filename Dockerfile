FROM amazoncorretto:17.0.7
COPY build/libs/*.jar inqueue.jar

ENTRYPOINT java -Dspring.profiles.active=deploy -jar inqueue.jar