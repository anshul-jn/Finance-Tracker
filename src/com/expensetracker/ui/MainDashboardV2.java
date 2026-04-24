package com.expensetracker.ui;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Budget;
import com.expensetracker.model.Expense;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Enhanced Main Dashboard with full functionality, colors, charts, and improved UI.
 */
public class MainDashboardV2 extends JFrame {
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");
    
    // Colors - Professional Palette
    private static final Color PRIMARY_COLOR   = new Color(30, 41, 59);    // Dark Navy
private static final Color SECONDARY_COLOR = new Color(59, 130, 246);  // Blue
private static final Color SUCCESS_COLOR   = new Color(16, 185, 129);  // Green
private static final Color DANGER_COLOR    = new Color(239, 68, 68);   // Red
private static final Color WARNING_COLOR   = new Color(245, 158, 11);  // Amber

private static final Color BG_COLOR        = new Color(248, 250, 252); // Soft White
private static final Color CARD_COLOR      = Color.WHITE;
private static final Color TEXT_COLOR      = new Color(15, 23, 42);    // Rich Black
private static final Color BORDER_COLOR    = new Color(226, 232, 240); // Light Border

    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;
    private JTable expensesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> categoryFilterCombo;
    private JLabel totalSpendingLabel;
    private JLabel monthlySpendingLabel;
    private JLabel expenseCountLabel;
    private JPanel chartPanel;
    private JTextArea analyticsArea;
    private JLabel statusLabel;
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private LocalDate startDate;
    private LocalDate endDate;

    public MainDashboardV2() {
        this.expenseDAO = new ExpenseDAO();
        this.budgetDAO = new BudgetDAO();
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();
        this.startDate = LocalDate.now().minusMonths(1);
        this.endDate = LocalDate.now();
        
        setTitle("💰 Smart Expense Tracker v2.0 - Advanced");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        
        initializeUI();
        SwingUtilities.invokeLater(this::refreshData);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Menu Bar
        setJMenuBar(createMenuBar());

        // Top Panel
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Middle Panel - Split View
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setLeftComponent(createTablePanel());
        centerSplit.setRightComponent(createAnalyticsPanel());
        centerSplit.setDividerLocation(800);
        centerSplit.setBackground(BG_COLOR);
        mainPanel.add(centerSplit, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Create top panel with buttons and date picker
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add Button
        JButton addBtn = createStyledButton("➕ Add", PRIMARY_COLOR);
        addBtn.addActionListener(e -> addExpense());
        panel.add(addBtn);

        // Edit Button
        JButton editBtn = createStyledButton("✏️ Edit", SECONDARY_COLOR);
        editBtn.addActionListener(e -> editExpense());
        panel.add(editBtn);

        // Delete Button
        JButton deleteBtn = createStyledButton("🗑️ Delete", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteExpense());
        panel.add(deleteBtn);

        panel.add(Box.createHorizontalStrut(15));

        // Quick Filter Buttons
        JButton todayBtn = createStyledButton("📅 Today", SECONDARY_COLOR);
        todayBtn.setToolTipText("Show today's expenses");
        todayBtn.addActionListener(e -> {
            startDate = LocalDate.now();
            endDate = LocalDate.now();
            selectedDate = LocalDate.now();
            refreshData();
        });
        panel.add(todayBtn);

        JButton weekBtn = createStyledButton("📈 Week", SECONDARY_COLOR);
        weekBtn.setToolTipText("Show this week's expenses");
        weekBtn.addActionListener(e -> {
            startDate = LocalDate.now().minusDays(7);
            endDate = LocalDate.now();
            refreshData();
        });
        panel.add(weekBtn);

        JButton monthBtn = createStyledButton("📆 Month", SECONDARY_COLOR);
        monthBtn.setToolTipText("Show this month's expenses");
        monthBtn.addActionListener(e -> {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now();
            refreshData();
        });
        panel.add(monthBtn);

        JButton yearBtn = createStyledButton("📊 Year", SECONDARY_COLOR);
        yearBtn.setToolTipText("Show this year's expenses");
        yearBtn.addActionListener(e -> {
            startDate = LocalDate.now().withDayOfYear(1);
            endDate = LocalDate.now();
            refreshData();
        });
        panel.add(yearBtn);

        panel.add(Box.createHorizontalStrut(15));

        // Date Picker
        JButton datePickerBtn = createStyledButton("📍 Select Dates", WARNING_COLOR);
        datePickerBtn.setToolTipText("Select custom date range");
        datePickerBtn.addActionListener(e -> showDateRangePicker());
        panel.add(datePickerBtn);

        panel.add(Box.createHorizontalStrut(10));

        // Filter Label
        JLabel filterLabel = new JLabel("Category:");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(filterLabel);

        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem("All Categories");
        categoryFilterCombo.addActionListener(e -> applyFilter());
        categoryFilterCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(categoryFilterCombo);

        panel.add(Box.createHorizontalGlue());

        // Refresh Button
        JButton refreshBtn = createStyledButton("🔄 Refresh", SUCCESS_COLOR);
        refreshBtn.setToolTipText("Refresh data from database");
        refreshBtn.addActionListener(e -> {
            loadTable();
            updateAnalytics();
            chartPanel.repaint();
            statusLabel.setText("✅ Data refreshed at " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        panel.add(refreshBtn);

        return panel;
    }

    /**
     * Show date range picker dialog
     */
    private void showDateRangePicker() {
        JDialog dialog = new JDialog(this, "Select Date Range", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel fromLabel = new JLabel("From Date (DD-MM-YYYY):");
        JTextField fromField = new JTextField(startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), 15);
        contentPanel.add(fromLabel);
        contentPanel.add(fromField);

        JLabel toLabel = new JLabel("To Date (DD-MM-YYYY):");
        JTextField toField = new JTextField(endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), 15);
        contentPanel.add(toLabel);
        contentPanel.add(toField);

        JButton okBtn = new JButton("Apply");
        okBtn.addActionListener(e -> {
            try {
                startDate = LocalDate.parse(fromField.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                endDate = LocalDate.parse(toField.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                refreshData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use DD-MM-YYYY", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        contentPanel.add(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        contentPanel.add(cancelBtn);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    /**
     * Create table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Expenses"));

        String[] columnNames = {"ID", "Amount", "Category", "Date", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expensesTable = new JTable(tableModel);
        expensesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        expensesTable.setRowHeight(28);
        expensesTable.setGridColor(new Color(189, 195, 199));
        expensesTable.setSelectionBackground(PRIMARY_COLOR);
        expensesTable.setSelectionForeground(Color.WHITE);

        sorter = new TableRowSorter<>(tableModel);
        expensesTable.setRowSorter(sorter);

        // Double-click to edit
        expensesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editExpense();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(expensesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Drag-drop support
        enableDragDrop();

        return panel;
    }

    /**
     * Create analytics panel with chart and stats
     */
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Analytics & Budget"));

        // Top: Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        summaryPanel.setBackground(CARD_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total Spending Card
        totalSpendingLabel = new JLabel("$0.00");
        summaryPanel.add(createSummaryCard("Total Spending", totalSpendingLabel, PRIMARY_COLOR));

        // Monthly Spending Card
        monthlySpendingLabel = new JLabel("$0.00");
        summaryPanel.add(createSummaryCard("This Month", monthlySpendingLabel, SECONDARY_COLOR));

        // Expense Count Card
        expenseCountLabel = new JLabel("0");
        summaryPanel.add(createSummaryCard("Total Expenses", expenseCountLabel, WARNING_COLOR));

        panel.add(summaryPanel, BorderLayout.NORTH);

        // Middle: Chart
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart((Graphics2D) g);
            }
        };
        chartPanel.setBackground(Color.WHITE);
        panel.add(new JScrollPane(chartPanel), BorderLayout.CENTER);

        // Bottom: Category breakdown
        analyticsArea = new JTextArea();
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        analyticsArea.setEditable(false);
        analyticsArea.setBackground(new Color(245, 245, 245));
        analyticsArea.setForeground(TEXT_COLOR);
        JScrollPane analyticsScroll = new JScrollPane(analyticsArea);
        analyticsScroll.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));
        panel.add(analyticsScroll, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create a summary card with color
     */
    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Draw chart
     */
    private void drawChart(Graphics2D g) {
        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        if (width <= 0 || height <= 0) return;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<String> categories = expenseDAO.getAllCategories();
        double totalSpending = expenseDAO.getTotalSpending();

        if (categories.isEmpty() || totalSpending == 0) {
            g.setColor(TEXT_COLOR);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("No data available. Add expenses to see chart.", width / 2 - 150, height / 2);
            return;
        }

        // Chart Title
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("📊 Category Spending vs Budget", 60, 25);

        // Bar chart dimensions
        int barWidth = Math.max(25, width / (categories.size() * 2));
        int startX = 70;
        int startY = height - 100;
        int barSpacing = width / categories.size();
        int maxBarHeight = startY - 60;

        // Draw Y-axis label
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Amount ($)", 10, 50);

        // Draw axes
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawLine(startX, startY, width - 30, startY);  // X-axis
        g.drawLine(startX, 60, startX, startY);          // Y-axis

        // Calculate max value for scaling (include budget limits)
        double maxValue = totalSpending;
        for (String category : categories) {
            Budget budget = budgetDAO.getBudget(category, currentMonth);
            if (budget != null && budget.getLimit() > maxValue) {
                maxValue = budget.getLimit();
            }
        }
        if (maxValue == 0) maxValue = 1;

        // Draw grid lines and values
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        g.setColor(new Color(189, 195, 199));
        for (int i = 0; i <= 5; i++) {
            int y = startY - (i * maxBarHeight / 5);
            g.drawLine(startX - 5, y, startX, y);
            g.setColor(TEXT_COLOR);
            g.drawString(String.format("%.0f", maxValue * i / 5), 15, y + 3);
            g.setColor(new Color(189, 195, 199));
        }

        // Draw bars
        int x = startX + barSpacing / 2;
        int colorIndex = 0;
        Color[] barColors = {PRIMARY_COLOR, SECONDARY_COLOR, WARNING_COLOR, DANGER_COLOR};
        
        for (String category : categories) {
            double spending = expenseDAO.getSpendingByCategory(category);
            double percentage = (spending / totalSpending) * 100;
            int barHeight = (int) ((spending / maxValue) * maxBarHeight);

            // Draw spending bar with professional color
            Color barColor = barColors[colorIndex % barColors.length];
            g.setColor(barColor);
            g.fillRect(x - barWidth / 2, startY - barHeight, barWidth, barHeight);

            // Draw budget line if exists
            Budget budget = budgetDAO.getBudget(category, currentMonth);
            if (budget != null) {
                int budgetLineY = startY - (int) ((budget.getLimit() / maxValue) * maxBarHeight);
                g.setColor(WARNING_COLOR);
                g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{5}, 0));
                g.drawLine(x - barWidth / 2 - 5, budgetLineY, x + barWidth / 2 + 5, budgetLineY);
                
                // Show budget label
                g.setColor(WARNING_COLOR);
                g.setFont(new Font("Arial", Font.BOLD, 8));
                g.drawString("$" + String.format("%.0f", budget.getLimit()), x - 15, budgetLineY - 3);
            }

            // Draw border
            g.setColor(new Color(52, 73, 94));
            g.setStroke(new BasicStroke(1));
            g.drawRect(x - barWidth / 2, startY - barHeight, barWidth, barHeight);

            // Draw category label
            g.setColor(TEXT_COLOR);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = category.length() > 5 ? category.substring(0, 5) : category;
            g.drawString(label, x - 20, startY + 20);
            g.drawString(String.format("$%.0f", spending), x - 22, startY + 32);

            x += barSpacing;
            colorIndex++;
        }

        // Legend
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString("■ = Spending   ═ = Budget Limit", width - 200, startY + 50);

        // X-axis label
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Category →", width - 120, startY + 50);
    }

    /**
     * Create bottom status panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_COLOR);
        panel.add(statusLabel);

        return panel;
    }

    /**
     * Create styled button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Create menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem importItem = new JMenuItem("Import");
        importItem.addActionListener(e -> importFile());
        JMenuItem exportCSVItem = new JMenuItem("Export CSV");
        exportCSVItem.addActionListener(e -> exportCSV());
        JMenuItem exportPDFItem = new JMenuItem("Export PDF");
        exportPDFItem.addActionListener(e -> exportPDF());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(importItem);
        fileMenu.add(exportCSVItem);
        fileMenu.add(exportPDFItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Budget Menu
        JMenu budgetMenu = new JMenu("Budget");
        JMenuItem setBudgetItem = new JMenuItem("Set Budget");
        setBudgetItem.addActionListener(e -> setBudget());
        JMenuItem viewBudgetItem = new JMenuItem("View Budgets");
        viewBudgetItem.addActionListener(e -> viewBudgets());

        budgetMenu.add(setBudgetItem);
        budgetMenu.add(viewBudgetItem);
        menuBar.add(budgetMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Add new expense
     */
    private void addExpense() {
        new AddEditExpenseDialog(this, null, this::refreshData);
    }

    /**
     * Edit selected expense
     */
    private void editExpense() {
        int selectedRow = expensesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = expensesTable.convertRowIndexToModel(selectedRow);
        int expenseId = (Integer) tableModel.getValueAt(modelRow, 0);
        Expense expense = expenseDAO.getExpenseById(expenseId);

        if (expense != null) {
            new AddEditExpenseDialog(this, expense, this::refreshData);
        }
    }

    /**
     * Delete selected expense
     */
    private void deleteExpense() {
        int selectedRow = expensesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Delete this expense?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            int modelRow = expensesTable.convertRowIndexToModel(selectedRow);
            int expenseId = (Integer) tableModel.getValueAt(modelRow, 0);
            if (expenseDAO.deleteExpense(expenseId)) {
                refreshData();
            }
        }
    }

    /**
     * Apply category filter
     */
    private void applyFilter() {
        String selected = (String) categoryFilterCombo.getSelectedItem();
        tableModel.setRowCount(0);
        
        List<Expense> allExpenses = expenseDAO.getAllExpenses();
        List<Expense> filteredExpenses = allExpenses.stream()
            .filter(exp -> !exp.getDate().isBefore(startDate) && !exp.getDate().isAfter(endDate))
            .collect(Collectors.toList());
        
        if (selected != null && !selected.equals("All Categories")) {
            filteredExpenses = filteredExpenses.stream()
                .filter(exp -> exp.getCategory().equals(selected))
                .collect(Collectors.toList());
        }
        
        for (Expense exp : filteredExpenses) {
            tableModel.addRow(new Object[]{
                exp.getId(),
                String.format("$%.2f", exp.getAmount()),
                exp.getCategory(),
                exp.getDate().format(DATE_FORMAT),
                exp.getDescription()
            });
        }
    }

    /**
     * Set budget
     */
    private void setBudget() {
        new BudgetDialog(this, currentMonth, this::refreshData);
    }

    /**
     * View budgets
     */
    private void viewBudgets() {
        new BudgetManagementDialog(this, currentMonth, this::refreshData);
    }

    /**
     * Import file
     */
    private void importFile() {
        new DataImportDialog(this, this::refreshData);
    }

    /**
     * Export to CSV
     */
    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVExporter.exportExpenses(expenseDAO.getAllExpenses(), fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Export to PDF
     */
    private void exportPDF() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                PDFExporter.exportExpensesToPDF(expenseDAO.getAllExpenses(), fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Enable drag-drop
     */
    private void enableDragDrop() {
        new DropTarget(expensesTable, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent e) {
                try {
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    if (!files.isEmpty() && files.get(0).getName().endsWith(".csv")) {
                        new DataImportDialog(MainDashboardV2.this, MainDashboardV2.this::refreshData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Show about dialog
     */
    private void showAbout() {
        JOptionPane.showMessageDialog(this, 
            "💰 Smart Expense Tracker v2.0\n\n" +
            "Features:\n" +
            "• Expense tracking\n" +
            "• Budget management\n" +
            "• Charts and analytics\n" +
            "• CSV/PDF export\n" +
            "• Import data",
            "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refresh all data
     */
    public void refreshData() {
        loadTable();
        updateAnalytics();
        chartPanel.repaint();
    }

    /**
     * Load expenses into table
     */
    private void loadTable() {
        tableModel.setRowCount(0);
        
        List<Expense> expenses = expenseDAO.getAllExpenses();
        
        // Filter by date range
        List<Expense> filteredExpenses = expenses.stream()
            .filter(exp -> !exp.getDate().isBefore(startDate) && !exp.getDate().isAfter(endDate))
            .collect(Collectors.toList());
        
        for (Expense exp : filteredExpenses) {
            tableModel.addRow(new Object[]{
                exp.getId(),
                String.format("$%.2f", exp.getAmount()),
                exp.getCategory(),
                exp.getDate().format(DATE_FORMAT),
                exp.getDescription()
            });
        }

        // Reload categories without triggering filter events
        Object currentSelection = categoryFilterCombo.getSelectedItem();
        categoryFilterCombo.removeAllItems();
        categoryFilterCombo.addItem("All Categories");
        
        List<String> categories = expenseDAO.getAllCategories();
        for (String cat : categories) {
            categoryFilterCombo.addItem(cat);
        }
        
        // Restore previous selection if it still exists
        if (currentSelection != null) {
            categoryFilterCombo.setSelectedItem(currentSelection);
        } else {
            categoryFilterCombo.setSelectedItem("All Categories");
        }
        
        // Update status
        statusLabel.setText("📊 Showing " + filteredExpenses.size() + " expenses from " + 
            startDate.format(DATE_FORMAT) + " to " + endDate.format(DATE_FORMAT));
    }

    /**
     * Update analytics
     */
    private void updateAnalytics() {
        // Filter expenses by date range
        List<Expense> allExpenses = expenseDAO.getAllExpenses();
        List<Expense> filteredExpenses = allExpenses.stream()
            .filter(exp -> !exp.getDate().isBefore(startDate) && !exp.getDate().isAfter(endDate))
            .collect(Collectors.toList());
        
        // Calculate totals for filtered data
        double total = filteredExpenses.stream().mapToDouble(Expense::getAmount).sum();
        int count = filteredExpenses.size();

        totalSpendingLabel.setText(String.format("$%.2f", total));
        monthlySpendingLabel.setText(String.format("$%.2f", total));
        expenseCountLabel.setText(String.valueOf(count));

        StringBuilder breakdown = new StringBuilder("📈 Category Breakdown & Budget:\n");
        breakdown.append("═══════════════════════════════════════════════\n");

        // Calculate by category for filtered data
        for (String category : expenseDAO.getAllCategories()) {
            double spending = filteredExpenses.stream()
                .filter(exp -> exp.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();
            double percentage = total > 0 ? (spending / total) * 100 : 0;
            
            Budget budget = budgetDAO.getBudget(category, currentMonth);
            
            if (spending > 0) {
                if (budget != null) {
                    double remaining = Math.max(0, budget.getLimit() - spending);
                    String status = spending > budget.getLimit() ? "❌ OVER" : "✅ OK";
                    breakdown.append(String.format("%-12s: $%7.2f | Budget: $%7.2f | %s\n", 
                        category, spending, budget.getLimit(), status));
                    breakdown.append(String.format("   Remaining: $%7.2f (%.1f%%)\n", 
                        remaining, percentage));
                } else {
                    breakdown.append(String.format("%-12s: $%7.2f | Budget: Not Set\n", 
                        category, spending));
                }
            }
        }
        
        breakdown.append("═══════════════════════════════════════════════\n");
        breakdown.append(String.format("TOTAL SPENDING: $%8.2f\n", total));

        analyticsArea.setText(breakdown.toString());
    }
}
