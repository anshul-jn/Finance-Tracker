# Smart Expense Tracker with Analytics

A comprehensive Java desktop application for managing personal expenses with real-time analytics, built using Swing GUI and SQLite database.

## 📋 Features

### Core Features
- ✅ **Add Expenses** - Add new expense entries with amount, category, date, and description
- ✅ **View Expenses** - Display all expenses in a sortable table
- ✅ **Edit Expenses** - Modify existing expense records
- ✅ **Delete Expenses** - Remove unwanted expense entries
- ✅ **Category Filter** - Filter expenses by predefined categories
- ✅ **Date Navigation** - View expenses for specific months

### Analytics Features
- 📊 **Total Spending** - Track total expenses across all time
- 📈 **Monthly Summary** - View spending for current month
- 📉 **Category Breakdown** - See spending distribution by category
- 💾 **CSV Export** - Export expense data to CSV format
- 🔍 **Search Functionality** - Search expenses by description

### Database Features
- 🗄️ **SQLite Integration** - Lightweight, file-based database
- 💾 **Data Persistence** - All data persists after application restart
- ⚡ **Real-time Updates** - UI refreshes automatically when data changes
- 🔒 **JDBC Connection Pool** - Efficient database access

## 📁 Project Structure

```
SmartExpenseTracker/
├── src/
│   └── com/expensetracker/
│       ├── ExpenseTrackerApp.java (Main entry point)
│       ├── model/
│       │   └── Expense.java (Data model)
│       ├── dao/
│       │   └── ExpenseDAO.java (Database operations)
│       ├── ui/
│       │   ├── MainDashboard.java (Main GUI)
│       │   ├── AddEditExpenseDialog.java (Add/Edit dialog)
│       │   └── CSVExporter.java (Export utility)
│       └── util/
│           └── DatabaseConnection.java (DB connection)
├── expenses.db (SQLite database - auto-created)
├── README.md (This file)
└── SETUP.md (Setup instructions)
```

## 🛠️ Prerequisites

### System Requirements
- Java Development Kit (JDK) 11 or higher
- Windows, macOS, or Linux operating system
- At least 100 MB free disk space

### Required Libraries
- **sqlite-jdbc** (version 3.40.0 or higher) - SQLite JDBC driver

## ⚙️ Setup Instructions

### Step 1: Download and Install Java
1. Download JDK 11 or higher from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [OpenJDK](https://adoptopenjdk.net/)
2. Install JDK and note the installation path
3. Add Java to system PATH (optional, but recommended)

### Step 2: Set Up the Project

#### Option A: Using Command Line

1. **Extract/Create Project Directory**
   ```bash
   # Create project directory
   mkdir SmartExpenseTracker
   cd SmartExpenseTracker
   ```

2. **Create Directory Structure**
   ```bash
   mkdir -p src/com/expensetracker/{model,dao,ui,util}
   mkdir -p lib
   mkdir -p bin
   ```

3. **Download SQLite JDBC Driver**
   - Download `sqlite-jdbc-3.40.0.0.jar` from [SQLite JDBC releases](https://github.com/xerial/sqlite-jdbc/releases)
   - Place the JAR file in the `lib/` directory

4. **Copy Project Files**
   - Copy all Java files from the provided code into their respective directories under `src/`

5. **Compile the Project**
   ```bash
   # Windows
   javac -d bin -cp "lib/sqlite-jdbc-3.40.0.0.jar" src/com/expensetracker/**/*.java

   # macOS/Linux
   javac -d bin -cp "lib/sqlite-jdbc-3.40.0.0.jar" src/com/expensetracker/**/*.java
   ```

6. **Run the Application**
   ```bash
   # Windows
   java -cp "bin;lib/sqlite-jdbc-3.40.0.0.jar" com.expensetracker.ExpenseTrackerApp

   # macOS/Linux
   java -cp "bin:lib/sqlite-jdbc-3.40.0.0.jar" com.expensetracker.ExpenseTrackerApp
   ```

#### Option B: Using an IDE (Recommended)

**IntelliJ IDEA:**
1. Open IntelliJ IDEA and select "Open Project"
2. Navigate to the SmartExpenseTracker directory
3. Right-click on `Project Structure` → Libraries
4. Add the sqlite-jdbc JAR file
5. Right-click on `ExpenseTrackerApp.java` and select "Run"

**Eclipse:**
1. Create a new Java project named "SmartExpenseTracker"
2. Copy the source files into the `src/` folder
3. Right-click Project → Properties → Java Build Path
4. Add the sqlite-jdbc JAR to the build path
5. Right-click `ExpenseTrackerApp.java` → Run As → Java Application

**VS Code:**
1. Install the "Extension Pack for Java" extension
2. Open the project folder in VS Code
3. Create `.classpath` file with SQLite JAR reference
4. Use the "Run and Debug" feature to run the project

### Step 3: First Run

On first run, the application will:
1. Check database connectivity
2. Create the SQLite database file (`expenses.db`) automatically
3. Create the `Expense` table with proper schema
4. Launch the main dashboard

## 📖 Usage Guide

### Adding an Expense

1. Click the **"➕ Add Expense"** button in the toolbar
2. Fill in the form:
   - **Amount**: Enter the expense amount (e.g., 25.50)
   - **Category**: Select from predefined categories
   - **Date**: Choose the expense date
   - **Description**: Add details about the expense
3. Click **"💾 Save"** to add the expense
4. The table updates automatically

### Editing an Expense

1. Select an expense from the table
2. Click the **"✏️ Edit Expense"** button
3. Modify the fields as desired
4. Click **"💾 Save"** to update

### Deleting an Expense

1. Select an expense from the table
2. Click the **"🗑️ Delete Expense"** button
3. Confirm the deletion in the dialog
4. The expense is removed immediately

### Filtering by Category

1. Use the **"Filter by Category"** dropdown in the toolbar
2. Select a category to view only expenses in that category
3. Select **"All Categories"** to view all expenses

### Viewing Monthly Summary

1. Use the **"◀ Previous Month"** and **"Next Month ▶"** buttons to navigate months
2. The **"Monthly Spending"** card shows total for that month
3. The table updates to show all expenses
4. The analytics panel shows category breakdown

### Exporting to CSV

1. Click **"📊 Export to CSV"** button
2. Choose a location to save the file
3. All expenses are exported in CSV format
4. Open with Excel or any spreadsheet application

## 📊 Database Schema

### Expense Table

```sql
CREATE TABLE Expense (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL NOT NULL,
    category TEXT NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Predefined Categories
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

## 🔧 Advanced Features

### Searching Expenses

While not shown in UI buttons, the DAO supports search:
```java
List<Expense> results = expenseDAO.searchByDescription("groceries");
```

### Custom Analysis

Access the DAO directly for custom analysis:
```java
ExpenseDAO dao = new ExpenseDAO();
double monthlyTotal = dao.getMonthlySpending(2024, 3); // March 2024
List<Expense> foodExpenses = dao.getExpensesByCategory("Food & Dining");
```

## 🐛 Troubleshooting

### Issue: "SQLite JDBC driver not found"
**Solution**: Ensure `sqlite-jdbc-3.40.0.0.jar` is in the `lib/` directory and in the classpath

### Issue: Database file not created
**Solution**: 
- Ensure write permissions in the project directory
- Try running from the project root directory
- Check error logs in the console

### Issue: Table doesn't refresh after adding expense
**Solution**:
- The refresh is automatic; if not working, restart the application
- Check console for SQL errors

### Issue: "Access denied" to expenses.db
**Solution**:
- Close the application
- Delete the `expenses.db` file
- Restart the application (database will be recreated)

## 📝 Code Structure & OOP Principles

### Model Layer (model/)
- **Expense.java**: Encapsulates expense data with getters/setters
- Implements Serializable for future enhancement

### Data Access Layer (dao/)
- **ExpenseDAO.java**: Implements Data Access Object pattern
- Separates business logic from database operations
- Provides all CRUD operations

### Presentation Layer (ui/)
- **MainDashboard.java**: Main GUI frame
- **AddEditExpenseDialog.java**: Modal dialog for data entry
- **CSVExporter.java**: Export utilities

### Utility Layer (util/)
- **DatabaseConnection.java**: Centralized database connection management

## 🔐 Security Features

- Input validation on all forms
- SQL injection prevention through PreparedStatements
- Proper error handling and logging
- Database connection pooling

## 📈 Future Enhancements

Potential features for future versions:
1. **Budget Tracking** - Set and monitor budgets per category
2. **Data Visualization** - Charts using JFreeChart
3. **Receipt OCR** - Extract amounts from receipt images
4. **Cloud Sync** - Sync data across devices
5. **Multi-user Support** - Login system with user accounts
6. **Recurring Expenses** - Set up automatic recurring expenses
7. **Reports** - Generate monthly/yearly reports
8. **Mobile App** - Companion mobile application

## 🤝 Contributing

To extend this project:

1. **Add New Categories**: Modify `CATEGORIES` array in `AddEditExpenseDialog.java`
2. **Add New Features**: Follow the existing package structure
3. **Enhance Analytics**: Add methods to `ExpenseDAO.java`
4. **Improve UI**: Modify components in `MainDashboard.java`

## 📄 License

This project is provided as-is for educational and personal use.

## 📞 Support

For issues or questions:
1. Check the Troubleshooting section
2. Review the console error logs
3. Verify all prerequisites are installed
4. Check the database file permissions

## 🎓 Learning Resources

- [Java Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [Design Patterns in Java](https://www.oracle.com/java/technologies/)

---

**Version**: 1.0.0  
**Last Updated**: April 2024  
**Author**: Smart Expense Tracker Development Team
