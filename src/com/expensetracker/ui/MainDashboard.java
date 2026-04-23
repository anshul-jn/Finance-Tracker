package com.expensetracker.ui;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Main application dashboard.
 * Displays expenses table, filters, and summary information.
 */
public class MainDashboard extends JFrame {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    
    private ExpenseDAO expenseDAO;
    private JTable expensesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryFilterCombo;
    private JLabel totalSpendingLabel;
    private JLabel monthlySpendingLabel;
    private JTextArea analyticsArea;
    private YearMonth currentMonth;

    public MainDashboard() {
        this.expenseDAO = new ExpenseDAO();
        this.currentMonth = YearMonth.now();
        initializeUI();
        refreshData();
    }

    /**
     * Initialize the user interface.
     */
    private void initializeUI() {
        setTitle("Smart Expense Tracker with Analytics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with buttons and filters
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Middle panel with table and analytics
        JPanel middlePanel = createMiddlePanel();
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        // Bottom panel with summary
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Create the top panel with action buttons and filters.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Add Expense button
        JButton addBtn = new JButton("➕ Add Expense");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> new AddEditExpenseDialog(this, null, this::refreshData));
        topPanel.add(addBtn);

        // Edit Expense button
        JButton editBtn = new JButton("✏️ Edit Expense");
        editBtn.setFont(new Font("Arial", Font.BOLD, 12));
        editBtn.addActionListener(e -> editSelectedExpense());
        topPanel.add(editBtn);

        // Delete Expense button
        JButton deleteBtn = new JButton("🗑️ Delete Expense");
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 12));
        deleteBtn.addActionListener(e -> deleteSelectedExpense());
        topPanel.add(deleteBtn);

        topPanel.add(new JSeparator(SwingConstants.VERTICAL));

        // Category filter
        topPanel.add(new JLabel("Filter by Category:"));
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem("All Categories");
        categoryFilterCombo.addActionListener(e -> applyFilter());
        categoryFilterCombo.setPreferredSize(new Dimension(150, 30));
        topPanel.add(categoryFilterCombo);

        topPanel.add(new JSeparator(SwingConstants.VERTICAL));

        // Month Navigation
        JButton prevBtn = new JButton("◀ Previous Month");
        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshData();
        });
        topPanel.add(prevBtn);

        JLabel monthLabel = new JLabel(currentMonth.toString());
        monthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(monthLabel);

        JButton nextBtn = new JButton("Next Month ▶");
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshData();
        });
        topPanel.add(nextBtn);

        // Export button
        JButton exportBtn = new JButton("📊 Export to CSV");
        exportBtn.addActionListener(e -> exportToCSV());
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(exportBtn);

        return topPanel;
    }

    /**
     * Create the middle panel with table and analytics.
     */
    private JPanel createMiddlePanel() {
        JPanel middlePanel = new JPanel(new BorderLayout(10, 10));

        // Left side - Table
        JPanel tablePanel = createTablePanel();
        
        // Right side - Analytics
        JPanel analyticsPanel = createAnalyticsPanel();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, analyticsPanel);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.7);

        middlePanel.add(splitPane, BorderLayout.CENTER);
        return middlePanel;
    }

    /**
     * Create the table panel showing all expenses.
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Expenses"));

        // Define table columns
        String[] columnNames = {"ID", "Amount", "Category", "Date", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        expensesTable = new JTable(tableModel);
        expensesTable.setFont(new Font("Arial", Font.PLAIN, 11));
        expensesTable.setRowHeight(25);
        expensesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        expensesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        expensesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        expensesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        expensesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        expensesTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(expensesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    /**
     * Create the analytics panel showing spending summaries.
     */
    private JPanel createAnalyticsPanel() {
        JPanel analyticsPanel = new JPanel(new BorderLayout(5, 5));
        analyticsPanel.setBorder(BorderFactory.createTitledBorder("Analytics & Summary"));

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        summaryPanel.setBackground(Color.WHITE);

        // Total Spending Card
        JPanel totalCard = createSummaryCard("Total Spending", "$0.00");
        totalSpendingLabel = (JLabel) totalCard.getComponent(1);
        summaryPanel.add(totalCard);

        // Monthly Spending Card
        JPanel monthlyCard = createSummaryCard("Monthly Spending (" + currentMonth + ")", "$0.00");
        monthlySpendingLabel = (JLabel) monthlyCard.getComponent(1);
        summaryPanel.add(monthlyCard);

        analyticsPanel.add(summaryPanel, BorderLayout.NORTH);

        // Category breakdown
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));

        analyticsArea = new JTextArea();
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        analyticsArea.setEditable(false);
        analyticsArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(analyticsArea);
        categoryPanel.add(scrollPane, BorderLayout.CENTER);

        analyticsPanel.add(categoryPanel, BorderLayout.CENTER);

        return analyticsPanel;
    }

    /**
     * Create a summary card for displaying key metrics.
     */
    private JPanel createSummaryCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(200, 220, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Create the bottom status panel.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel statusLabel = new JLabel("Ready | Database: Connected | Last Updated: " + LocalDate.now());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        bottomPanel.add(statusLabel, BorderLayout.WEST);

        return bottomPanel;
    }

    /**
     * Refresh all data in the dashboard.
     */
    public void refreshData() {
        loadExpensesIntoTable();
        updateCategoryFilter();
        updateAnalytics();
    }

    /**
     * Load expenses into the table.
     */
    private void loadExpensesIntoTable() {
        tableModel.setRowCount(0); // Clear existing rows

        List<Expense> expenses = expenseDAO.getAllExpenses();

        for (Expense expense : expenses) {
            Object[] row = {
                expense.getId(),
                String.format("$%.2f", expense.getAmount()),
                expense.getCategory(),
                expense.getDate(),
                expense.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Update category filter dropdown.
     */
    private void updateCategoryFilter() {
        categoryFilterCombo.removeAllItems();
        categoryFilterCombo.addItem("All Categories");

        List<String> categories = expenseDAO.getAllCategories();
        for (String category : categories) {
            categoryFilterCombo.addItem(category);
        }
    }

    /**
     * Apply category filter to the table.
     */
    private void applyFilter() {
        tableModel.setRowCount(0); // Clear existing rows

        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();
        List<Expense> expenses;

        if ("All Categories".equals(selectedCategory)) {
            expenses = expenseDAO.getAllExpenses();
        } else {
            expenses = expenseDAO.getExpensesByCategory(selectedCategory);
        }

        for (Expense expense : expenses) {
            Object[] row = {
                expense.getId(),
                String.format("$%.2f", expense.getAmount()),
                expense.getCategory(),
                expense.getDate(),
                expense.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Update analytics display.
     */
    private void updateAnalytics() {
        double totalSpending = expenseDAO.getTotalSpending();
        double monthlySpending = expenseDAO.getMonthlySpending(currentMonth.getYear(), currentMonth.getMonthValue());

        totalSpendingLabel.setText(String.format("$%.2f", totalSpending));
        monthlySpendingLabel.setText(String.format("$%.2f", monthlySpending));

        // Build category breakdown
        StringBuilder breakdown = new StringBuilder("Category Breakdown:\n");
        breakdown.append("──────────────────────────────").append("\n");

        List<String> categories = expenseDAO.getAllCategories();
        for (String category : categories) {
            double spending = expenseDAO.getSpendingByCategory(category);
            double percentage = totalSpending > 0 ? (spending / totalSpending) * 100 : 0;
            breakdown.append(String.format("%-15s: $%8.2f (%.1f%%)\n", category, spending, percentage));
        }

        analyticsArea.setText(breakdown.toString());
    }

    /**
     * Edit the selected expense from the table.
     */
    private void editSelectedExpense() {
        int selectedRow = expensesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Expense expense = expenseDAO.getExpenseById(expenseId);

        if (expense != null) {
            new AddEditExpenseDialog(this, expense, this::refreshData);
        }
    }

    /**
     * Delete the selected expense from the table.
     */
    private void deleteSelectedExpense() {
        int selectedRow = expensesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (Integer) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (expenseDAO.deleteExpense(expenseId)) {
                JOptionPane.showMessageDialog(this, "Expense deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete expense.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Export expenses to CSV file.
     */
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("expenses.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                CSVExporter.exportExpenses(expenseDAO.getAllExpenses(), filePath);
                JOptionPane.showMessageDialog(this, "Expenses exported successfully to:\n" + filePath,
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + ex.getMessage(),
                        "Export Failed", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
