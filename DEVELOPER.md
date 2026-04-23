# Smart Expense Tracker - Developer Guide

This guide is for developers who want to understand, extend, or contribute to the Smart Expense Tracker project.

## 📚 Architecture Overview

The application follows the **Data Access Object (DAO)** pattern and **Model-View-Controller (MVC)** principles:

```
┌─────────────────────────────────────────────────────┐
│           Presentation Layer (UI)                   │
│  ┌─────────────────────────────────────────────┐   │
│  │  MainDashboard      AddEditExpenseDialog     │   │
│  │  CSVExporter        SwingComponents          │   │
│  └─────────────────────────────────────────────┘   │
└────────────────┬────────────────────────────────────┘
                 │ Uses
                 ▼
┌─────────────────────────────────────────────────────┐
│           Business Logic Layer (DAO)                │
│  ┌─────────────────────────────────────────────┐   │
│  │  ExpenseDAO                                  │   │
│  │  (CRUD Operations, Analytics, Queries)      │   │
│  └─────────────────────────────────────────────┘   │
└────────────────┬────────────────────────────────────┘
                 │ Uses
                 ▼
┌─────────────────────────────────────────────────────┐
│           Model Layer                               │
│  ┌─────────────────────────────────────────────┐   │
│  │  Expense (Entity Class)                      │   │
│  └─────────────────────────────────────────────┘   │
└────────────────┬────────────────────────────────────┘
                 │ Uses
                 ▼
┌─────────────────────────────────────────────────────┐
│           Data Layer (Database)                     │
│  ┌─────────────────────────────────────────────┐   │
│  │  DatabaseConnection (JDBC)                   │   │
│  │  SQLite Database (expenses.db)               │   │
│  └─────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

## 📦 Package Structure

### `com.expensetracker` (Root Package)
- **ExpenseTrackerApp.java**: Application entry point
  - Initializes database
  - Launches GUI on Event Dispatch Thread
  - Handles startup errors

### `com.expensetracker.model`
- **Expense.java**: Data model class
  - Represents a single expense entity
  - Properties: id, amount, category, date, description
  - Provides constructors for new and existing expenses

### `com.expensetracker.dao`
- **ExpenseDAO.java**: Data Access Object
  - Implements CRUD operations (Create, Read, Update, Delete)
  - Implements business logic queries
  - Methods:**
    - `addExpense()` - Insert new expense
    - `getAllExpenses()` - Retrieve all expenses
    - `getExpenseById()` - Get single expense
    - `updateExpense()` - Modify existing expense
    - `deleteExpense()` - Remove expense
    - `getExpensesByCategory()` - Filter by category
    - `getExpensesByDateRange()` - Date range queries
    - `getTotalSpending()` - Calculate total
    - `getMonthlySpending()` - Monthly analysis
    - `getSpendingByCategory()` - Category analysis
    - `searchByDescription()` - Text search

### `com.expensetracker.ui`
- **MainDashboard.java**: Main application window
  - Creates JFrame and layout
  - Manages table and filters
  - Displays analytics
  - Handles user interactions

- **AddEditExpenseDialog.java**: Modal dialog for data entry
  - Form validation
  - Add and edit modes
  - Callbacks for automatic refresh

- **CSVExporter.java**: CSV export utility
  - Converts expenses to CSV format
  - Handles file I/O

### `com.expensetracker.util`
- **DatabaseConnection.java**: Database connectivity
  - Connection pooling
  - Driver initialization
  - Table creation

## 🔄 Data Flow

### Adding an Expense:
```
User Click "Add Expense"
        ↓
AddEditExpenseDialog appears
        ↓
User fills form and clicks Save
        ↓
Validation in AddEditExpenseDialog
        ↓
New Expense object created
        ↓
ExpenseDAO.addExpense() called
        ↓
Database INSERT executed
        ↓
MainDashboard.refreshData() callback
        ↓
Table and analytics updated
```

### Retrieving and Displaying:
```
Application Start
        ↓
DatabaseConnection.initializeDatabase()
        ↓
MainDashboard created
        ↓
loadExpensesIntoTable() calls ExpenseDAO.getAllExpenses()
        ↓
Query database
        ↓
Create Expense objects from ResultSet
        ↓
Populate JTable
        ↓
Display on screen
```

## 💾 Database Design

### Schema
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

### Key Considerations:
- `id`: Auto-generated primary key
- `amount`: REAL for decimal precision
- `date`: TEXT stored as ISO 8601 format (YYYY-MM-DD)
- `created_at`: Auto-timestamp for audit trail
- Indexes could be added for: category, date performance

## 🔌 Key Classes & Methods

### Expense.java
```java
// Constructors
Expense(double amount, String category, LocalDate date, String desc)
Expense(int id, double amount, String category, LocalDate date, String desc)

// Key Methods
getId(), setId()
getAmount(), setAmount()
getCategory(), setCategory()
getDate(), setDate()
getDescription(), setDescription()
```

### ExpenseDAO.java
```java
// Core CRUD
boolean addExpense(Expense expense)
Expense getExpenseById(int id)
boolean updateExpense(Expense expense)
boolean deleteExpense(int id)
List<Expense> getAllExpenses()

// Filters & Search
List<Expense> getExpensesByCategory(String category)
List<Expense> getExpensesByDateRange(LocalDate start, LocalDate end)
List<Expense> searchByDescription(String keyword)

// Analytics
double getTotalSpending()
double getMonthlySpending(int year, int month)
double getSpendingByCategory(String category)
List<String> getAllCategories()
```

### MainDashboard.java
```java
// UI Creation
private void initializeUI()
private JPanel createTopPanel()
private JPanel createMiddlePanel()
private JPanel createTablePanel()
private JPanel createAnalyticsPanel()

// Data Operations
public void refreshData()
private void loadExpensesIntoTable()
private void applyFilter()
private void updateAnalytics()
private void editSelectedExpense()
private void deleteSelectedExpense()
private void exportToCSV()
```

## 🔧 Extending the Application

### Adding a New Category
**File**: `AddEditExpenseDialog.java`
```java
private static final String[] CATEGORIES = {
    "Food & Dining",
    "Travel",
    // ... existing categories
    "Your New Category"  // Add here
};
```

### Adding a New DAO Method
**File**: `ExpenseDAO.java`
```java
public List<Expense> getExpensesByAmount(double minAmount, double maxAmount) {
    List<Expense> expenses = new ArrayList<>();
    String sql = "SELECT * FROM Expense WHERE amount BETWEEN ? AND ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setDouble(1, minAmount);
        pstmt.setDouble(2, maxAmount);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Expense expense = new Expense(...);  // create from ResultSet
            expenses.add(expense);
        }
    } catch (SQLException e) {
        System.err.println("Error: " + e.getMessage());
    }
    
    return expenses;
}
```

### Adding a New UI Panel
**File**: `MainDashboard.java`
```java
private JPanel createReportsPanel() {
    JPanel reportsPanel = new JPanel(new BorderLayout());
    reportsPanel.setBorder(BorderFactory.createTitledBorder("Reports"));
    
    // Add your components here
    JButton generateBtn = new JButton("Generate Report");
    generateBtn.addActionListener(e -> generateReport());
    reportsPanel.add(generateBtn, BorderLayout.NORTH);
    
    return reportsPanel;
}
```

### Adding a Search Feature to UI
1. Add search field to `MainDashboard`:
```java
JTextField searchField = new JTextField(20);
searchField.addActionListener(e -> {
    String keyword = searchField.getText().trim();
    if (!keyword.isEmpty()) {
        List<Expense> results = expenseDAO.searchByDescription(keyword);
        displayResults(results);
    }
});
topPanel.add(searchField);
```

## 🐛 Debugging Tips

### Enable SQL Logging
Add to `ExpenseDAO` methods:
```java
System.out.println("SQL: " + sql);
System.out.println("Parameters: " + param1 + ", " + param2);
```

### Database Inspection
View database file:
```bash
sqlite3 expenses.db
sqlite> SELECT * FROM Expense;
sqlite> .schema Expense
```

### Memory Usage
Monitor with Java flags:
```bash
java -Xmx512m -cp "bin:lib/*" com.expensetracker.ExpenseTrackerApp
```

## ✅ Code Quality Checklist

When adding features:
- [ ] Follow existing naming conventions
- [ ] Add JavaDoc comments to public methods
- [ ] Use try-with-resources for database connections
- [ ] Validate user input before database operations
- [ ] Close ResultSet and Statement objects
- [ ] Handle SQLException properly
- [ ] Use PreparedStatement to prevent SQL injection
- [ ] Test with various data types
- [ ] Check for null pointers
- [ ] Log errors appropriately

## 🧪 Testing Guidelines

### Manual Testing
1. Start application and verify database creation
2. Add expenses with different categories and dates
3. Test filtering by category
4. Verify monthly calculations
5. Test editing and deleting
6. Export to CSV and verify format

### Edge Cases
- Empty database behavior
- Very large amounts (999999.99)
- Special characters in description
- Leap year dates
- Month boundaries

## 📖 Documentation Standards

### JavaDoc Format
```java
/**
 * Brief description of method.
 * 
 * Detailed description if needed.
 * 
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType description of when thrown
 */
```

### Inline Comments
```java
// Reset model to clear existing rows
tableModel.setRowCount(0);

// Fetch from database with date ordering
List<Expense> expenses = expenseDAO.getAllExpenses();
```

## 🚀 Performance Optimization

### Database Improvements
1. Add indexes on frequently queried columns:
```sql
CREATE INDEX idx_category ON Expense(category);
CREATE INDEX idx_date ON Expense(date);
```

2. Use connection pooling for multiple queries

3. Cache category list if not changing frequently

### UI Improvements
1. Load data asynchronously for large datasets
2. Use pagination instead of loading all expenses
3. Implement table sorting without reloading
4. Use SwingWorker for heavy operations

## 🔐 Security Considerations

- [ ] SQL Injection: Always use PreparedStatement ✓
- [ ] Input Validation: Validate all user inputs ✓
- [ ] Error Messages: Don't expose database details
- [ ] File Permissions: Secure database file
- [ ] Password Protection: Could add for future versions

## 📋 Future Enhancement Ideas

1. **User Accounts**: Multi-user support with login
2. **Budget Alerts**: Notify when spending exceeds budget
3. **Recurring Expenses**: Auto-add monthly bills
4. **Charts**: Visual spending trends (JFreeChart)
5. **Import/Export**: Support CSV import
6. **Mobile Sync**: Cloud backup and mobile access
7. **Receipt Scanning**: OCR for receipt amounts
8. **Categories Management**: Add/edit categories in UI
9. **Notes & Tags**: Custom categorization
10. **Export Reports**: PDF/Excel generation

## 📞 Contributing Guidelines

1. Fork the project
2. Create feature branch: `git checkout -b feature/YourFeature`
3. Follow code style (camelCase, meaningful names)
4. Add JavaDoc comments
5. Test thoroughly
6. Commit with clear messages
7. Push and create Pull Request

---

**Happy coding! 🚀**
