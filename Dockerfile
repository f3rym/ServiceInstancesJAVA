FROM eclipse-temurin:22-jdk-alpine AS build

RUN adduser -D worker
USER worker
WORKDIR /app

COPY --chown=worker:worker gradlew ./
COPY --chown=worker:worker settings.gradle ./
COPY --chown=worker:worker build.gradle ./
COPY --chown=worker:worker gradle ./gradle
RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY --chown=worker:worker src ./src/
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:22-jre-alpine

RUN adduser -D worker
USER worker
WORKDIR /app

COPY --from=build --chown=worker:worker /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
