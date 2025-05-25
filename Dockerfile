FROM gradle:8.8-jdk22-alpine AS build

ENV JAVA_TOOL_OPTIONS="--enable-preview"

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:22-jdk AS runtime

WORKDIR /app

VOLUME /app/config

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "--enable-preview", "-jar", "/app/app.jar", "--spring.config.location=file:/app/config/application.properties"]
