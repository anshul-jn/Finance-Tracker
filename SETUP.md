# Smart Expense Tracker - Complete Setup Guide

This guide will walk you through setting up and running the Smart Expense Tracker application step by step.

## 📋 Prerequisites Checklist

Before starting, ensure you have:
- [ ] Java Development Kit (JDK) 11 or higher installed
- [ ] About 200 MB of disk space
- [ ] Text editor or IDE (VS Code, IntelliJ IDEA, Eclipse, or similar)
- [ ] Internet connection (to download SQLite JDBC driver)

## 🔧 Step-by-Step Setup

### Step 1: Install Java

#### Windows:
1. Go to [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptopenjdk.net/)
2. Download JDK 11 or higher (choose Windows installer)
3. Run the installer and follow the installation wizard
4. Accept the license agreement
5. Choose installation path (default is fine)
6. Click "Install"

**Verify Installation:**
```bash
java -version
javac -version
```

#### macOS:
```bash
# Using Homebrew (if installed)
brew install openjdk@11
# or
brew install openjdk

# Or download from Oracle/AdoptOpenJDK
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install openjdk-11-jdk
# or
sudo apt install default-jdk
```

### Step 2: Project Setup

1. **Create Project Folder:**
   ```bash
   mkdir SmartExpenseTracker
   cd SmartExpenseTracker
   ```

2. **Create Directory Structure:**
   ```bash
   mkdir -p src/com/expensetracker/{model,dao,ui,util}
   mkdir -p lib
   mkdir -p bin
   ```

3. **Your folder should look like:**
   ```
   SmartExpenseTracker/
   ├── src/
   │   └── com/expensetracker/
   │       ├── model/
   │       ├── dao/
   │       ├── ui/
   │       └── util/
   ├── lib/
   ├── bin/
   ├── compile.bat (Windows)
   ├── run.bat (Windows)
   ├── run.sh (Linux/macOS)
   └── README.md
   ```

### Step 3: Download SQLite JDBC Driver

1. Go to [SQLite JDBC Releases](https://github.com/xerial/sqlite-jdbc/releases)
2. Download the latest `sqlite-jdbc-x.x.x.0.jar` file (e.g., `sqlite-jdbc-3.40.0.0.jar`)
3. Place the JAR file in the `lib/` folder

**Verify:**
```bash
dir lib/          # Windows
ls lib/           # Linux/macOS
```

You should see: `sqlite-jdbc-3.40.0.0.jar` (or similar version)

### Step 4: Copy Project Files

Copy all the provided Java files into their respective directories:

- `ExpenseTrackerApp.java` → `src/com/expensetracker/`
- `Expense.java` → `src/com/expensetracker/model/`
- `ExpenseDAO.java` → `src/com/expensetracker/dao/`
- `MainDashboard.java` → `src/com/expensetracker/ui/`
- `AddEditExpenseDialog.java` → `src/com/expensetracker/ui/`
- `CSVExporter.java` → `src/com/expensetracker/ui/`
- `DatabaseConnection.java` → `src/com/expensetracker/util/`

### Step 5: Compile the Project

#### Option A: Windows (Using compile.bat)
```bash
cd SmartExpenseTracker
compile.bat
```

#### Option B: Windows (Manual)
```bash
javac -d bin -cp "lib\sqlite-jdbc-3.40.0.0.jar" ^
    src\com\expensetracker\*.java ^
    src\com\expensetracker\model\*.java ^
    src\com\expensetracker\dao\*.java ^
    src\com\expensetracker\ui\*.java ^
    src\com\expensetracker\util\*.java
```

#### Option C: Linux/macOS (Manual)
```bash
javac -d bin -cp "lib/sqlite-jdbc-3.40.0.0.jar" \
    src/com/expensetracker/*.java \
    src/com/expensetracker/model/*.java \
    src/com/expensetracker/dao/*.java \
    src/com/expensetracker/ui/*.java \
    src/com/expensetracker/util/*.java
```

**Success Message:**
```
✓ Compilation completed successfully!
```

### Step 6: Run the Application

#### Option A: Windows (Using run.bat)
```bash
run.bat
```

#### Option B: Windows (Manual)
```bash
java -cp "bin;lib\sqlite-jdbc-3.40.0.0.jar" com.expensetracker.ExpenseTrackerApp
```

#### Option C: Linux/macOS (Using run.sh)
```bash
chmod +x run.sh
./run.sh
```

#### Option D: Linux/macOS (Manual)
```bash
java -cp "bin:lib/sqlite-jdbc-3.40.0.0.jar" com.expensetracker.ExpenseTrackerApp
```

## 🎉 Success!

If everything is set up correctly, you should see:

```
╔════════════════════════════════════════════════════╗
║   Smart Expense Tracker with Analytics              ║
╚════════════════════════════════════════════════════╝

[*] Initializing application...

✓ Database initialized successfully. Table 'Expense' ready.
✓ Application started successfully!
✓ Main Dashboard is now open.
```

The GUI window will appear showing the main dashboard.

## 🆘 Troubleshooting

### Problem: "javac: command not found"

**Solution:**
1. Java is not installed or PATH is not set
2. Install JDK from Oracle or OpenJDK
3. Add Java to system PATH:

**Windows:**
1. Open System Properties → Environment Variables
2. Add JAVA_HOME variable pointing to JDK installation
3. Add `%JAVA_HOME%\bin` to PATH
4. Restart terminal

**Linux/macOS:**
```bash
export PATH="/usr/lib/jvm/java-11-openjdk/bin:$PATH"
# Add to ~/.bashrc or ~/.zshrc for permanent
```

### Problem: "sqlite-jdbc JAR not found"

**Solution:**
1. Download from https://github.com/xerial/sqlite-jdbc/releases
2. Ensure it's in the `lib/` folder
3. Check filename matches in compile/run scripts
4. Update script if version number is different

Example:
```bash
# If you got version 3.41.0.0:
javac -d bin -cp "lib\sqlite-jdbc-3.41.0.0.jar" ...
```

### Problem: "class path" or "package not found" errors

**Solution:**
1. Verify all Java files are in correct directories
2. Check that package names match (e.g., `package com.expensetracker.model;`)
3. Ensure directory structure is exact:
   ```
   src/
   └── com/expensetracker/
       ├── ExpenseTrackerApp.java
       ├── model/Expense.java
       ├── dao/ExpenseDAO.java
       ├── ui/*.java
       └── util/DatabaseConnection.java
   ```

### Problem: "Cannot find database connection to database"

**Solution:**
1. Ensure you're running from the project root directory
2. Check write permissions in the folder
3. Try deleting `expenses.db` and rerun (will be recreated)
4. Run with explicit path if using IDE

### Problem: Application starts but no window appears

**Solution:**
1. Application might be running but GUI hidden
2. Check console for errors
3. Try clicking on Java icon in taskbar (Windows)
4. Run again from terminal to see error messages

### Problem: "Main window not responding"

**Solution:**
1. Give it 5-10 seconds to fully load
2. Check console for SQL errors
3. Close and restart application
4. If persistent, check database file permissions

## 🚀 Quick Start Scripts

### Windows Users
Save as `quickstart.bat`:
```batch
@echo off
title Smart Expense Tracker
cd /d "%~dp0"
cls
java -cp "bin;lib/sqlite-jdbc-3.40.0.0.jar" com.expensetracker.ExpenseTrackerApp
```
Double-click to run!

### Linux/macOS Users
Save as `quickstart.sh`:
```bash
#!/bin/bash
cd "$(dirname "$0")"
java -cp "bin:lib/sqlite-jdbc-3.40.0.0.jar" com.expensetracker.ExpenseTrackerApp
```
Then:
```bash
chmod +x quickstart.sh
./quickstart.sh
```

## 📝 Using an IDE

### IntelliJ IDEA
1. Open → Select `SmartExpenseTracker` folder
2. Project Structure (Ctrl+Alt+Shift+S)
3. Libraries → Add (+) → JARs or Directories
4. Select `lib/sqlite-jdbc-3.40.0.0.jar`
5. Right-click `ExpenseTrackerApp.java` → Run

### Eclipse
1. File → New → Java Project
2. Project Name: SmartExpenseTracker
3. Create separate source folder: `src/`
4. Copy Java files into `src/`
5. Project → Properties → Java Build Path
6. Add External JARs → select `sqlite-jdbc-3.40.0.0.jar`
7. Run → Run As → Java Application

### VS Code
1. Install "Extension Pack for Java"
2. Open the folder in VS Code
3. It will auto-detect Java project
4. Create `.classpath` with JAR reference (if needed)
5. Click "Run" above `main()` method

## ✅ Verification Checklist

After setup, verify everything works:

- [ ] Java is installed (`java -version`)
- [ ] Project structure exists
- [ ] SQLite JAR is in `lib/` folder
- [ ] All Java files are in correct directories
- [ ] Project compiles without errors
- [ ] Application starts successfully
- [ ] Main window appears with dashboard
- [ ] Can add a test expense
- [ ] Data persists after restart

## 📞 Getting Help

If you're stuck:
1. Check this guide's Troubleshooting section
2. Verify all prerequisites are installed
3. Check console error messages
4. Ensure correct file paths and naming
5. Verify Java and database permissions

## 🎓 Next Steps

Once running:
1. Try adding expenses with different categories
2. Experiment with filters and date navigation
3. Check analytics and category breakdown
4. Export data to CSV file
5. Review the code structure and comments
6. Consider adding features (see README.md)

---

**Happy expense tracking! 💰📊**
