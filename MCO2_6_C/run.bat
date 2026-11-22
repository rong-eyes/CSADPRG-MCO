@echo off
REM Builds and runs the banking application with one command.
REM Run it from the folder that contains the source files and bank_transactions.csv.

cd /d "%~dp0"

where gcc >nul 2>&1
if errorlevel 1 (
    echo gcc was not found. Install MinGW-w64 and add its bin folder to your PATH.
    pause
    exit /b 1
)

gcc -Wall -std=c99 main.c -o run.exe -lm
if errorlevel 1 (
    echo.
    echo Build failed. See the messages above.
    pause
    exit /b 1
)

run.exe
exit /b %errorlevel%
