# syntax = docker/dockerfile:1.2

FROM maven:3.9.15-eclipse-temurin-21 AS application_builder

WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package

#FROM eclipse-temurin:21-jdk AS jdk_builder
#

# TODO: Switch to distroless/nonroot
FROM eclipse-temurin:21-jdk AS runtime

WORKDIR /app

COPY --from=application_builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java",  "-jar", "app.jar"]
