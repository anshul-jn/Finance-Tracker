@echo off
REM =======================================================
REM Expense Tracker - Compile Script
REM =======================================================
REM This script compiles all Java files in the project

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║  Smart Expense Tracker - Compilation Script        ║
echo ╚════════════════════════════════════════════════════╝
echo.

REM Check if required directories exist
if not exist "src" (
    echo ERROR: src folder not found!
    echo Please run this script from the project root directory.
    pause
    exit /b 1
)

if not exist "lib" (
    echo ERROR: lib folder not found!
    echo Please create a lib folder and place sqlite-jdbc-*.jar in it.
    pause
    exit /b 1
)

REM Create bin directory if it doesn't exist
if not exist "bin" (
    mkdir bin
    echo [*] Created bin directory
)

REM Find the sqlite-jdbc JAR file
for /f "delims=" %%A in ('dir /b lib\*.jar 2^>nul ^| findstr /i sqlite') do (
    set SQLITE_JAR=%%A
    goto found_jar
)

echo ERROR: sqlite-jdbc JAR file not found in lib folder!
echo Please download sqlite-jdbc from: https://github.com/xerial/sqlite-jdbc/releases
echo And place it in the lib folder.
pause
exit /b 1

:found_jar
echo [+] Found SQLite JAR: lib\%SQLITE_JAR%
echo.
echo [*] Compiling Java files...
echo.

REM Compile all Java files
javac -d bin -cp "lib\%SQLITE_JAR%" -encoding UTF-8 ^
    src\com\expensetracker\*.java ^
    src\com\expensetracker\model\*.java ^
    src\com\expensetracker\dao\*.java ^
    src\com\expensetracker\ui\*.java ^
    src\com\expensetracker\util\*.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo ✓ Compilation completed successfully!
    echo.
    echo To run the application, execute:
    echo   run.bat
    echo.
) else (
    echo.
    echo ✗ Compilation failed!
    echo Please check the errors above.
    echo.
)

pause
