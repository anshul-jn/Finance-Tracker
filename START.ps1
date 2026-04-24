#!/usr/bin/env pwsh
# =====================================================
#     Finance Tracker - Compile & Run (PowerShell)
#     Alternative to START.bat
# =====================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  FINANCE TRACKER v2.0" -ForegroundColor Cyan
Write-Host "  Building and Starting..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Java is installed
$javaCheck = Get-Command javac -ErrorAction SilentlyContinue
if (-not $javaCheck) {
    Write-Host ""
    Write-Host "❌ ERROR: Java compiler (javac) not found!" -ForegroundColor Red
    Write-Host "   Please install JDK and add to PATH" -ForegroundColor Red
    Write-Host ""
    pause
    exit 1
}

# Check if lib folder exists
if (-not (Test-Path "lib")) {
    Write-Host ""
    Write-Host "❌ ERROR: lib folder not found!" -ForegroundColor Red
    Write-Host ""
    pause
    exit 1
}

# Check if SQLite JAR exists
if (-not (Test-Path "lib\sqlite-jdbc-3.51.3.0.jar")) {
    Write-Host ""
    Write-Host "❌ ERROR: sqlite-jdbc-3.51.3.0.jar not found in lib folder!" -ForegroundColor Red
    Write-Host ""
    pause
    exit 1
}

# Clean old build
Write-Host "[1/4] Cleaning old build files..." -ForegroundColor Yellow
if (Test-Path "bin") {
    Remove-Item -Path "bin" -Recurse -Force -ErrorAction SilentlyContinue
}
New-Item -ItemType Directory -Path "bin" -Force | Out-Null

# Compile Java files
Write-Host "[2/4] Compiling Java files..." -ForegroundColor Yellow
javac -d bin -cp "lib/sqlite-jdbc-3.51.3.0.jar" `
  src/com/expensetracker/*.java `
  src/com/expensetracker/dao/*.java `
  src/com/expensetracker/model/*.java `
  src/com/expensetracker/ui/*.java `
  src/com/expensetracker/util/*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ COMPILATION FAILED!" -ForegroundColor Red
    Write-Host "   Check the errors above" -ForegroundColor Red
    Write-Host ""
    pause
    exit 1
}

# Check if compilation was successful
if (-not (Test-Path "bin\com\expensetracker\ExpenseTrackerApp.class")) {
    Write-Host ""
    Write-Host "❌ COMPILATION FAILED!" -ForegroundColor Red
    Write-Host "   .class files not created" -ForegroundColor Red
    Write-Host ""
    pause
    exit 1
}

Write-Host "[3/4] Compilation successful! ✅" -ForegroundColor Green

# Run the application
Write-Host "[4/4] Starting application..." -ForegroundColor Yellow
Write-Host ""

java -cp "bin;lib/sqlite-jdbc-3.51.3.0.jar" com.expensetracker.ExpenseTrackerApp

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ APPLICATION FAILED TO START!" -ForegroundColor Red
    Write-Host "   Check the errors above" -ForegroundColor Red
    Write-Host ""
    pause
    exit 1
}

exit 0
