#!/bin/bash

# =======================================================
# Expense Tracker - Compile and Run Script
# =======================================================
# This script compiles and runs the Expense Tracker

echo ""
echo "╔════════════════════════════════════════════════════╗"
echo "║  Smart Expense Tracker - Build & Run Script        ║"
echo "╚════════════════════════════════════════════════════╝"
echo ""

# Check Java installation
if ! command -v java &> /dev/null; then
    echo "✗ ERROR: Java is not installed or not in PATH"
    echo "Please install JDK 11 or higher"
    exit 1
fi

echo "[+] Java version:"
java -version
echo ""

# Check if required directories exist
if [ ! -d "src" ]; then
    echo "✗ ERROR: src folder not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

if [ ! -d "lib" ]; then
    echo "✗ ERROR: lib folder not found!"
    mkdir lib
    echo "[*] Created lib folder"
    echo "Please download sqlite-jdbc JAR from:"
    echo "https://github.com/xerial/sqlite-jdbc/releases"
    echo "And place it in the lib folder"
    exit 1
fi

# Create bin directory if it doesn't exist
if [ ! -d "bin" ]; then
    mkdir bin
    echo "[*] Created bin directory"
fi

# Find the sqlite-jdbc JAR file
SQLITE_JAR=$(ls lib/sqlite-jdbc*.jar 2>/dev/null | head -1)

if [ -z "$SQLITE_JAR" ]; then
    echo "✗ ERROR: sqlite-jdbc JAR file not found in lib folder!"
    echo "Please download it from: https://github.com/xerial/sqlite-jdbc/releases"
    exit 1
fi

echo "[+] Found SQLite JAR: $SQLITE_JAR"
echo ""
echo "[*] Compiling Java files..."
echo ""

# Compile all Java files
javac -d bin -cp "$SQLITE_JAR" -encoding UTF-8 \
    src/com/expensetracker/*.java \
    src/com/expensetracker/model/*.java \
    src/com/expensetracker/dao/*.java \
    src/com/expensetracker/ui/*.java \
    src/com/expensetracker/util/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Compilation completed successfully!"
    echo ""
    echo "[*] Starting application..."
    echo ""
    
    # Run the application
    java -cp "bin:$SQLITE_JAR" com.expensetracker.ExpenseTrackerApp
else
    echo ""
    echo "✗ Compilation failed!"
    echo "Please check the errors above."
    exit 1
fi
