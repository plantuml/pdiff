#!/usr/bin/env bash

set -e

# Define versions in variables
version_old="1.2025.10"
version_new="1.2025.11beta5"

# Display versions for verification
echo "Old version: $version_old"
echo "New version: $version_new"

# Change directory and run Gradle to build the JAR
cd ../plantuml || { echo "Cannot cd to ../plantuml"; exit 1; }
echo "Running Gradle task to build the JAR..."
./gradlew jar

# Check if Gradle succeeded
if [ $? -ne 0 ]; then
    echo "Failed to generate the JAR with Gradle. Stopping the script."
    exit 1
fi

# Return to the pdiff directory
cd ../pdiff || { echo "Cannot cd to ../pdiff"; exit 1; }

# Run the first command
java -cp "../plantuml/build/libs/plantuml-${version_new}.jar:build/libs/pdiff-all.jar" com.pdiff.Main run

# Check first command result and run second if successful
if [ $? -eq 0 ]; then
    echo "First command executed successfully. Launching the second command..."
    java -jar build/libs/pdiff-all.jar diff "$version_old" "$version_new"
else
    echo "The first command failed, the second command will not be executed."
fi
