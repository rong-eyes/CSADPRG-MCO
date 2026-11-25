@echo off
REM Runs all automated tests. Usage from the project folder: tests\run_tests.bat
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run_tests.ps1"
exit /b %errorlevel%
