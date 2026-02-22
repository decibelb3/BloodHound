@rem Gradle wrapper script for Windows
@if "%DEBUG%"=="" @echo off

@if not exist "%~dp0gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle wrapper JAR not found.
    echo Run "gradle wrapper" if you have Gradle installed, or open this project in Android Studio.
    exit /b 1
)

"%JAVA_HOME%\bin\java" -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %*
