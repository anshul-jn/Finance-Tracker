@echo off
REM =======================================================
REM Expense Tracker - Run Script
REM =======================================================
REM This script runs the Expense Tracker application

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║  Smart Expense Tracker - Startup                   ║
echo ╚════════════════════════════════════════════════════╝
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo ERROR: bin folder not found!
    echo Please compile the project first by running: compile.bat
    pause
    exit /b 1
)

REM Find the sqlite-jdbc JAR file
for /f "delims=" %%A in ('dir /b lib\*.jar 2^>nul ^| findstr /i sqlite') do (
    set SQLITE_JAR=%%A
    goto found_jar
)

echo ERROR: sqlite-jdbc JAR file not found in lib folder!
pause
exit /b 1

:found_jar
echo [+] Starting Smart Expense Tracker Application...
echo [+] Using SQLite driver: lib\%SQLITE_JAR%
echo.

REM Run the application
java -cp "bin;lib\%SQLITE_JAR%" com.expensetracker.ExpenseTrackerApp

pause
