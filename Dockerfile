# syntax = docker/dockerfile:1.2
FROM maven:3.9.15-eclipse-temurin-26 AS application_builder

WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package

#FROM eclipse-temurin:21-jdk AS jdk_builder
#

FROM busybox:1.37.0-musl AS shell_builder

# TODO: Switch to distroless/nonroot
FROM eclipse-temurin:26_35-jdk AS runtime

WORKDIR /app

COPY --from=application_builder /build/target/*.jar app.jar

# Expose Tomcat port
ARG TOMCAT_PORT="8080"
ENV TOMCAT_PORT="${TOMCAT_PORT}"
EXPOSE "${TOMCAT_PORT}"

# Set up the healthcheck
COPY --from=shell_builder /bin/busybox /bin/wget
HEALTHCHECK --interval=30s \
            --timeout=5s \
            --start-period=10s \
            --retries=3 \
            CMD /bin/wget \
                --no-check-certificate \
                --quiet \
                --tries=1 \
                --spider \
                "http://127.0.0.1:${TOMCAT_PORT}/status" || exit 1

ENTRYPOINT ["java",  "-jar", "app.jar"]
