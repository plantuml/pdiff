#!/usr/bin/env bash

set -e

# Define versions in variables
version_new="1.2026.7beta8"

# Display versions for verification
echo "New version: $version_new"

./gradlew clean shadowJar

# Check if Gradle succeeded
if [ $? -ne 0 ]; then
    echo "Failed to generate the JAR with Gradle. Stopping the script."
    exit 1
fi

# Run the first command
java -cp "../plantuml/build/libs/plantuml-${version_new}.jar:build/libs/pdiff-all.jar" com.pdiff.Main vega

