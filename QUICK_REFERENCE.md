# Smart Expense Tracker - Quick Reference Card

## 🚀 5-MINUTE QUICK START

### Step 1: Download Java (if not installed)
- Go to: https://adoptopenjdk.net/
- Download JDK 11 or higher for your OS
- Install (accept defaults)
- Verify: Open terminal, type `java -version`

### Step 2: Download SQLite JDBC
- Go to: https://github.com/xerial/sqlite-jdbc/releases
- Download: sqlite-jdbc-3.40.0.0.jar
- Place in: `SmartExpenseTracker/lib/` folder

### Step 3: Compile
**Windows:**
```bash
cd SmartExpenseTracker
compile.bat
```

**Linux/macOS:**
```bash
cd SmartExpenseTracker
./run.sh
```

### Step 4: Run
**Windows:**
```bash
run.bat
```

**That's it! Application will open.**

---

## 📖 KEY COMMANDS

### Windows
```bash
compile.bat              # Compile project
run.bat                  # Run application
javac -version          # Check Java version
```

### Linux/macOS
```bash
chmod +x run.sh         # Make executable
./run.sh                # Compile & run
java -version          # Check Java version
```

---

## 🎯 APP NAVIGATION

| Button | Function |
|--------|----------|
| ➕ Add Expense | Create new expense |
| ✏️ Edit Expense | Modify selected expense |
| 🗑️ Delete Expense | Remove selected expense |
| 📊 Export to CSV | Save data to file |
| ◀ Previous Month | View previous month |
| Next Month ▶ | View next month |

---

## 🗂️ PROJECT STRUCTURE

```
SmartExpenseTracker/
├── src/com/expensetracker/      ← Java source code
├── lib/                         ← SQLite JAR file
├── bin/                         ← Compiled classes
├── README.md                    ← Full documentation
├── SETUP.md                     ← Detailed setup
└── compile.bat / run.sh         ← Scripts
```

---

## 💾 DATABASE SCHEMA

| Column | Type | Purpose |
|--------|------|---------|
| id | Integer | Auto-incremented ID |
| amount | Real | Expense amount |
| category | Text | Category (Food, Travel, etc.) |
| date | Date | Expense date |
| description | Text | Details |

---

## 📊 FEATURES AT A GLANCE

✅ Add/Edit/Delete Expenses  
✅ Filter by Category  
✅ Monthly Summary  
✅ Category Breakdown  
✅ Total Spending  
✅ Export to CSV  
✅ Real-time Updates  
✅ Persistent Storage  

---

## 🐛 QUICK TROUBLESHOOTING

| Problem | Solution |
|---------|----------|
| Java not found | Install JDK 11+ |
| JAR file missing | Download from GitHub, place in lib/ |
| Compilation fails | Check file paths |
| App won't start | Check console errors |
| Database issue | Delete expenses.db, restart |

**Full help**: See SETUP.md

---

## 📝 PREDEFINED CATEGORIES

- Food & Dining
- Travel
- Bills & Utilities
- Shopping
- Entertainment
- Healthcare
- Education
- Personal Care
- Fitness
- Other

---

## 🔗 IMPORTANT LINKS

| Resource | URL |
|----------|-----|
| Java Distribution | https://adoptopenjdk.net |
| SQLite JDBC | https://github.com/xerial/sqlite-jdbc |
| Java Swing | https://docs.oracle.com/javase/tutorial/uiswing/ |
| SQLite Docs | https://www.sqlite.org/docs.html |

---

## 📂 FILE LOCATIONS

```
Project Root:
f:\projects\Expense tracker\SmartExpenseTracker\

Source Code:
src/com/expensetracker/
  - ExpenseTrackerApp.java
  - model/Expense.java
  - dao/ExpenseDAO.java
  - ui/MainDashboard.java
  - ui/AddEditExpenseDialog.java
  - ui/CSVExporter.java
  - util/DatabaseConnection.java

Database (auto-created):
expenses.db
```

---

## ⚙️ SYSTEM REQUIREMENTS

- **Java**: JDK 11 or higher
- **RAM**: 256 MB minimum
- **Storage**: 100 MB free
- **OS**: Windows, macOS, Linux
- **Permissions**: Write access to folder

---

## 🎓 DOCUMENTATION MAP

| Document | Purpose | Read When |
|----------|---------|-----------|
| GET_STARTED.md | Overview | First |
| SETUP.md | Installation | Setting up |
| README.md | User guide | Using app |
| DEVELOPER.md | Architecture | Extending |
| PROJECT_OVERVIEW.md | Summary | Planning |

---

## 💾 DATABASE OPERATIONS

### Access Database (SQLite)
```bash
# Windows (if sqlite3 installed)
sqlite3 expenses.db

# View all expenses
SELECT * FROM Expense;

# Check schema
.schema Expense

# Exit
.quit
```

---

## 📊 COMMON WORKFLOWS

### Daily Usage
```
Open App → Click Add Expense → Fill Form → Save
→ Table Updates → Check Total/Monthly
```

### Review Monthly
```
Click "Next Month ▶" → View Data → Check Analytics
→ Export CSV if needed
```

### Manage Data
```
Select Expense → Click Edit/Delete → Confirm
→ UI Updates Automatically
```

---

## 🔒 SECURITY NOTES

✅ No SQL Injection (PreparedStatements)  
✅ Input Validation  
✅ Proper Error Handling  
✅ Local Database (No Network)  
✅ File Permissions

---

## 💡 TIPS & TRICKS

1. **Quick Export**: Click 📊 anytime
2. **Remember Categories**: Use same ones for analysis
3. **Detailed Description**: Helps finding expenses later
4. **Monthly Review**: Navigate months using buttons
5. **Data Backup**: Export CSV regularly
6. **Search Old Entries**: Use category filter + month nav

---

## 📧 GETTING HELP

1. Check console error message
2. See SETUP.md Troubleshooting
3. Verify Java is installed
4. Check SQLite JAR location
5. Try deleting expenses.db and restart

---

## ✅ SETUP VERIFICATION

After setup, test these:
- [ ] `java -version` shows Java 11+
- [ ] SQLite JAR in lib/ folder
- [ ] compile.bat/run.sh exists
- [ ] Compilation completes
- [ ] Application window opens
- [ ] Can add test expense
- [ ] Table updates automatically
- [ ] Data persists after restart

---

## 🎊 READY TO GO!

**You have everything you need.**

- ✅ Complete source code (7 files)
- ✅ Database solution (SQLite + JDBC)
- ✅ Build scripts (compile.bat, run.sh)
- ✅ Comprehensive docs (4 guides)
- ✅ OOP architecture
- ✅ Professional quality

**Just download SQLite JAR and run!**

---

## 📍 NEXT STEP

→ Go to SETUP.md for detailed installation instructions

---

**Version**: 1.0.0  
**Status**: Complete & Ready ✅  
**Last Updated**: April 2024
