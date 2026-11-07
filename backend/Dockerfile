# One image, deployed twice: SPRING_PROFILES_ACTIVE=api (default) or batch.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY build-logic ./build-logic
COPY config ./config
COPY db ./db
COPY platform ./platform
COPY modules ./modules
COPY channels ./channels
COPY app ./app
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :app:bootJar -x test --no-daemon --console=plain \
 && java -Djarmode=tools -jar app/build/libs/backend.jar extract --layers --destination /layers

FROM eclipse-temurin:25-jre
RUN useradd --system --uid 10001 --create-home app && apt-get update \
 && apt-get install -y --no-install-recommends wget \
 && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build --chown=app:app /layers/dependencies/ ./
COPY --from=build --chown=app:app /layers/spring-boot-loader/ ./
COPY --from=build --chown=app:app /layers/snapshot-dependencies/ ./
COPY --from=build --chown=app:app /layers/application/ ./
USER app
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "backend.jar"]
