# syntax = docker/dockerfile:1.2
FROM maven:3.9.15-eclipse-temurin-26 AS application_builder

WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package

#FROM eclipse-temurin:21-jdk AS jdk_builder
#

# TODO: Switch to distroless/nonroot
FROM eclipse-temurin:26_35-jdk AS runtime

WORKDIR /app

COPY --from=application_builder /build/target/*.jar app.jar

ARG TOMCAT_PORT="8080"
ENV TOMCAT_PORT="${TOMCAT_PORT}"
EXPOSE "${TOMCAT_PORT}"

ENTRYPOINT ["java",  "-jar", "app.jar"]
