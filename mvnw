#!/bin/sh
# Maven Wrapper script
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.

set -e

MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

# Determine the Java command to use
if [ -n "$JAVA_HOME" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="java"
fi

# Read the distribution URL from wrapper properties
DISTRIBUTION_URL=$(grep distributionUrl "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2)

# Download Maven if not already cached
MAVEN_HOME="$HOME/.m2/wrapper/dists/$(basename "$DISTRIBUTION_URL" .zip)"

if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven from $DISTRIBUTION_URL"
    mkdir -p "$MAVEN_HOME"
    TMP_ZIP="/tmp/maven-dist.zip"
    if command -v curl > /dev/null; then
        curl -o "$TMP_ZIP" "$DISTRIBUTION_URL"
    elif command -v wget > /dev/null; then
        wget -O "$TMP_ZIP" "$DISTRIBUTION_URL"
    else
        echo "Neither curl nor wget found. Please install Maven manually."
        exit 1
    fi
    unzip -q "$TMP_ZIP" -d "$MAVEN_HOME"
    rm "$TMP_ZIP"
fi

MAVEN_BIN=$(find "$MAVEN_HOME" -name "mvn" -type f | head -1)
exec "$MAVEN_BIN" "$@"
