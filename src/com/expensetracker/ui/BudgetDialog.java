package com.expensetracker.ui;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.model.Budget;
import java.awt.*;
import java.time.YearMonth;
import javax.swing.*;

/**
 * Dialog for setting and managing budgets.
 */
public class BudgetDialog extends JDialog {
    private static final int DIALOG_WIDTH = 450;
    private static final int DIALOG_HEIGHT = 300;
    
    private JComboBox<String> categoryCombo;
    private JTextField limitField;
    private JCheckBox alertCheckBox;
    private BudgetDAO budgetDAO;
    private YearMonth month;
    private Runnable refreshCallback;

    public BudgetDialog(Frame parent, YearMonth month, Runnable refreshCallback) {
        super(parent, "Set Budget", true);
        this.budgetDAO = new BudgetDAO();
        this.month = month;
        this.refreshCallback = refreshCallback;

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initializeUI();
        setVisible(true);
    }

    /**
     * Initialize UI.
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Category
        JPanel categoryPanel = createLabeledPanel("Category:");
        categoryCombo = new JComboBox<>(new String[]{
            "Food & Dining", "Travel", "Bills & Utilities", "Shopping",
            "Entertainment", "Healthcare", "Education", "Personal Care", "Fitness", "Other"
        });
        categoryPanel.add(categoryCombo);
        mainPanel.add(categoryPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Budget limit
        JPanel limitPanel = createLabeledPanel("Budget Limit ($):");
        limitField = new JTextField(15);
        limitField.setFont(new Font("Arial", Font.PLAIN, 12));
        limitPanel.add(limitField);
        mainPanel.add(limitPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Alert checkbox
        alertCheckBox = new JCheckBox("Enable Budget Alerts");
        alertCheckBox.setSelected(true);
        mainPanel.add(alertCheckBox);
        mainPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton saveBtn = new JButton("💾 Save");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        saveBtn.addActionListener(e -> saveBudget());
        
        JButton cancelBtn = new JButton("❌ Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    /**
     * Create labeled panel.
     */
    private JPanel createLabeledPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setPreferredSize(new Dimension(100, 30));
        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    /**
     * Save budget.
     */
    private void saveBudget() {
        try {
            String category = (String) categoryCombo.getSelectedItem();
            double limit = Double.parseDouble(limitField.getText().trim());
            boolean alertEnabled = alertCheckBox.isSelected();

            if (limit <= 0) {
                JOptionPane.showMessageDialog(this, "Budget limit must be greater than 0.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Budget budget = new Budget(category, limit, month);
            budget.setAlertEnabled(alertEnabled);

            if (budgetDAO.addBudget(budget)) {
                JOptionPane.showMessageDialog(this, "Budget saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save budget.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for budget limit.",
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
}
