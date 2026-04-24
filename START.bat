@echo off

REM Finance Tracker - Compile & Run (Master Script)


setlocal enabledelayedexpansion

echo.

echo  FINANCE TRACKER v2.0
echo  Building and Starting...

echo.

REM Check if Java is installed
where javac >nul 2>nul
if errorlevel 1 (
    echo.
    echo ERROR: Java compiler ^(javac^) not found!
    echo Please install JDK and add to PATH
    echo.
    pause
    exit /b 1
)

REM Check if lib folder exists
if not exist "lib" (
    echo.
    echo ERROR: lib folder not found!
    echo.
    pause
    exit /b 1
)

REM Check if SQLite JAR exists
if not exist "lib\sqlite-jdbc-3.51.3.0.jar" (
    echo.
    echo ERROR: sqlite-jdbc-3.51.3.0.jar not found in lib folder!
    echo.
    pause
    exit /b 1
)

REM Clean old build
echo [1/4] Cleaning old build files...
if exist "bin" (
    rmdir /s /q bin >nul 2>nul
)
if not exist "bin" mkdir bin

REM Compile Java files
echo [2/4] Compiling Java files...

setlocal
cd /d "%~dp0"
javac -d bin -cp "lib/sqlite-jdbc-3.51.3.0.jar" src/com/expensetracker/*.java src/com/expensetracker/dao/*.java src/com/expensetracker/model/*.java src/com/expensetracker/ui/*.java src/com/expensetracker/util/*.java

if errorlevel 1 (
    echo.
    echo COMPILATION FAILED!
    echo Check the errors above
    echo.
    pause
    exit /b 1
)

REM Check if compilation was successful
if not exist "bin\com\expensetracker\ExpenseTrackerApp.class" (
    echo.
    echo COMPILATION FAILED!
    echo .class files not created
    echo.
    pause
    exit /b 1
)

echo [3/4] Compilation successful!
echo.

REM Run the application
echo [4/4] Starting application...
echo.

java -cp "bin;lib/sqlite-jdbc-3.51.3.0.jar" com.expensetracker.ExpenseTrackerApp

if errorlevel 1 (
    echo.
    echo APPLICATION FAILED TO START!
    echo Check the errors above
    echo.
    pause
    exit /b 1
)

exit /b 0
