# syntax = docker/dockerfile:1.2
# Stage 1: Build application JAR
FROM maven:3.9.15-eclipse-temurin-26 AS application_builder

WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package

# Stage 2: Analyse the fat JAR and link a minimal JRE
FROM eclipse-temurin:26_35-jdk AS jdk_builder

COPY --from=application_builder /build/target/url-shortener.jar /app/url-shortener.jar

RUN apt-get update && \
    apt-get install -yqq --no-install-recommends \
        binutils="2.42-4ubuntu2.10" \
    && \
    apt-get autoremove && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# jdeps detects required JDK modules via static analysis; java.naming (Tomcat JNDI)
# and jdk.crypto.ec (TLS EC ciphers) are added explicitly as they are loaded dynamically.
RUN DEPS=$(jdeps \
        --ignore-missing-deps \
        --print-module-deps \
        --multi-release 26 \
        /app/url-shortener.jar) && \
    jlink \
        --add-modules "${DEPS},java.naming,jdk.crypto.ec" \
        --strip-debug \
        --no-header-files \
        --no-man-pages \
        --compress=zip-6 \
        --output /opt/jdk  && \
    strip -p --strip-unneeded "/opt/jdk/lib/server/libjvm.so" && \
    { find /opt/jdk/bin -type f -exec strip -p --strip-unneeded {} \; || true; }

# Stage 3: Use BusyBox for shell binaries
FROM busybox:1.37.0-musl AS shell_builder

# Stage 4: Distroless, non-root runtime
FROM gcr.io/distroless/base-debian13:nonroot AS runtime

COPY --from=jdk_builder /opt/jdk /opt/jdk
COPY --from=application_builder /build/target/url-shortener.jar /app.jar
COPY --from=shell_builder /bin/busybox /bin/wget

# Expose Tomcat port
EXPOSE 8080

# Exec form required: distroless has no /bin/sh for shell-form commands
HEALTHCHECK --interval=30s \
            --timeout=5s \
            --start-period=10s \
            --retries=3 \
            CMD ["/bin/wget", "--no-check-certificate", "--quiet", "--tries=1", "--spider", "http://127.0.0.1:8080/status"]

ENTRYPOINT ["/opt/jdk/bin/java", "-jar", "/app.jar"]
