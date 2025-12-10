@echo off

REM Define versions in variables
set "version_old=1.2025.10"
set "version_new=1.2025.11beta5"

REM Display versions for verification
echo Old version: %version_old%
echo New version: %version_new%

REM Change directory and run Gradle to build the JAR
cd ../plantuml
echo Running Gradle task to build the JAR...
call ./gradlew jar

REM Check if the Gradle command succeeded
IF %ERRORLEVEL% NEQ 0 (
    echo Failed to generate the JAR with Gradle. Stopping the script.
    exit /b %ERRORLEVEL%
)

REM Return to the pdiff directory
cd ../pdiff

REM Run the first command
java -cp "../plantuml/build/libs/plantuml-%version_new%.jar;build/libs/pdiff-all.jar" com.pdiff.Main run

REM Check if the first command succeeded before running the second
IF %ERRORLEVEL% EQU 0 (
    echo First command executed successfully. Launching the second command...
    java -jar build/libs/pdiff-all.jar diff %version_old% %version_new%
) ELSE (
    echo The first command failed, the second command will not be executed.
)
