# syntax=docker/dockerfile:1

FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /workspace/app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

ENV JAVA_OPTS=""
ENV PORT=8080

COPY --from=builder /workspace/app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
