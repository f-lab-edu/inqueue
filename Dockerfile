FROM amazoncorretto:17-alpine-jdk AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean build


FROM amazoncorretto:17-alpine-jdk
COPY --from=builder build/libs/*.jar inqueue.jar

ENTRYPOINT ["java", "-jar", "inqueue.jar"]
