# syntax = docker/dockerfile:1.2
# Stage 1: Build application JAR
FROM maven:3.9.15-eclipse-temurin-26 AS application_builder

WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package

#FROM eclipse-temurin:26_35-jdk AS jdk_builder

FROM busybox:1.37.0-musl AS shell_builder

# TODO: Switch to distroless/nonroot
FROM eclipse-temurin:26_35-jdk AS runtime

WORKDIR /app

COPY --from=application_builder /build/target/*.jar app.jar

# Expose Tomcat port
EXPOSE 8080

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
                "http://127.0.0.1:8080/status" || exit 1

ENTRYPOINT ["java",  "-jar", "app.jar"]
