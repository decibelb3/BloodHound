@echo off
setlocal
cd /d "%~dp0"
if not exist out (
    echo Run compile.bat first.
    exit /b 1
)
java -cp out com.txstate.bloodhound.Main
