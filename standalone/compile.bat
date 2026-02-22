@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
javac -d out -encoding UTF-8 com\txstate\bloodhound\*.java
if %ERRORLEVEL% equ 0 (
    echo Compile successful.
) else (
    echo Compile failed.
    exit /b 1
)
