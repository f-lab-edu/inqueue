# build jar
FROM amazoncorretto:17-alpine-jdk AS builder

ARG BUILD_OPTIONS
ENV BUILD_OPTIONS ${BUILD_OPTIONS}

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src
RUN ./gradlew clean build ${BUILD_OPTIONS}

# run jar
FROM amazoncorretto:17-alpine-jdk
COPY --from=builder build/libs/*.jar inqueue.jar

ENTRYPOINT ["java", "-jar", "inqueue.jar"]