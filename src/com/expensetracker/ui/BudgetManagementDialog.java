package com.expensetracker.ui;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Budget;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog for managing budgets - view, edit, and delete.
 */
public class BudgetManagementDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(30, 41, 59);
    private static final Color SECONDARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    private BudgetDAO budgetDAO;
    private ExpenseDAO expenseDAO;
    private YearMonth currentMonth;
    private Runnable refreshCallback;
    private JTable budgetTable;
    private DefaultTableModel tableModel;

    public BudgetManagementDialog(JFrame parent, YearMonth month, Runnable refreshCallback) {
        super(parent, "Manage Budgets", true);
        this.budgetDAO = new BudgetDAO();
        this.expenseDAO = new ExpenseDAO();
        this.currentMonth = month;
        this.refreshCallback = refreshCallback;
        
        initializeUI();
        loadBudgets();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(true);
    }

    private void initializeUI() {
        setContentPane(new JPanel(new BorderLayout(10, 10)));
        getContentPane().setBackground(BG_COLOR);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("📊 Budget Management for " + currentMonth);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        getContentPane().add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Category", "Budget", "Spending", "Remaining", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        budgetTable = new JTable(tableModel);
        budgetTable.setRowHeight(25);
        budgetTable.setFont(new Font("Arial", Font.PLAIN, 11));
        budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        budgetTable.setGridColor(BORDER_COLOR);

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(BG_COLOR);

        JButton editBtn = new JButton("✏️ Edit");
        editBtn.setFont(new Font("Arial", Font.BOLD, 11));
        editBtn.setBackground(SECONDARY_COLOR);
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorder(null);
        editBtn.setFocusPainted(false);
        editBtn.setPreferredSize(new Dimension(90, 35));
        editBtn.addActionListener(e -> editBudget());

        JButton deleteBtn = new JButton("🗑️ Delete");
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 11));
        deleteBtn.setBackground(DANGER_COLOR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBorder(null);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(90, 35));
        deleteBtn.addActionListener(e -> deleteBudget());

        JButton closeBtn = new JButton("❌ Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 11));
        closeBtn.setBackground(PRIMARY_COLOR);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(90, 35));
        closeBtn.addActionListener(e -> dispose());

        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(closeBtn);
        
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadBudgets() {
        tableModel.setRowCount(0);
        List<Budget> budgets = budgetDAO.getBudgetsByMonth(currentMonth);
        
        for (Budget budget : budgets) {
            String status = budget.isBudgetExceeded() ? "❌ Over" : 
                           budget.isApproachingLimit() ? "⚠️ Warning" : "✅ OK";
            
            tableModel.addRow(new Object[]{
                budget.getCategory(),
                String.format("$%.2f", budget.getLimit()),
                String.format("$%.2f", budget.getCurrentSpending()),
                String.format("$%.2f", budget.getRemainingBudget()),
                status
            });
        }
    }

    private void editBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a budget to edit", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String category = (String) tableModel.getValueAt(selectedRow, 0);
        Budget budget = budgetDAO.getBudget(category, currentMonth);

        if (budget != null) {
            String newLimitStr = JOptionPane.showInputDialog(this, 
                "Enter new budget limit for " + category + ":", 
                String.format("%.2f", budget.getLimit()));
            
            if (newLimitStr != null && !newLimitStr.trim().isEmpty()) {
                try {
                    double newLimit = Double.parseDouble(newLimitStr.trim());
                    if (newLimit <= 0) {
                        JOptionPane.showMessageDialog(this, "Budget must be greater than 0",
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    budget.setLimit(newLimit);
                    if (budgetDAO.updateBudget(budget)) {
                        JOptionPane.showMessageDialog(this, "Budget updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadBudgets();
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update budget",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    private void deleteBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a budget to delete", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String category = (String) tableModel.getValueAt(selectedRow, 0);
        Budget budget = budgetDAO.getBudget(category, currentMonth);

        if (budget != null) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Delete budget for " + category + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                if (budgetDAO.deleteBudget(budget.getId())) {
                    JOptionPane.showMessageDialog(this, "Budget deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBudgets();
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete budget",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
