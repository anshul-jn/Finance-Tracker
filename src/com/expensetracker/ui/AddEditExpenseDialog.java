package com.expensetracker.ui;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

/**
 * Dialog for adding or editing expenses.
 * Modal dialog that appears on top of the main dashboard.
 */
public class AddEditExpenseDialog extends JDialog {
    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 350;

    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JSpinner dateSpinner;
    private JTextArea descriptionArea;
    private ExpenseDAO expenseDAO;
    private Expense expenseToEdit;
    private Runnable refreshCallback;

    // Predefined categories
    private static final String[] CATEGORIES = {
            "Food & Dining",
            "Travel",
            "Bills & Utilities",
            "Shopping",
            "Entertainment",
            "Healthcare",
            "Education",
            "Personal Care",
            "Fitness",
            "Other"
    };

    public AddEditExpenseDialog(Frame parent, Expense expense, Runnable refreshCallback) {
        super(parent, true);
        this.expenseDAO = new ExpenseDAO();
        this.expenseToEdit = expense;
        this.refreshCallback = refreshCallback;

        if (expense != null) {
            setTitle("Edit Expense");
        } else {
            setTitle("Add New Expense");
        }

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initializeUI();
        if (expense != null) {
            populateFields(expense);
        }
        
        // Show the dialog
        setVisible(true);
    }

    /**
     * Initialize the dialog UI components.
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Amount field
        JPanel amountPanel = createLabeledPanel("Amount:");
        amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 12));
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Category combo
        JPanel categoryPanel = createLabeledPanel("Category:");
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        categoryPanel.add(categoryCombo);
        mainPanel.add(categoryPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Date picker
        JPanel datePanel = createLabeledPanel("Date:");
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        dateSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        datePanel.add(dateSpinner);
        mainPanel.add(datePanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Description area
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        descriptionPanel.add(descLabel, BorderLayout.NORTH);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 11));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        descriptionPanel.add(scrollPane, BorderLayout.CENTER);
        descriptionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        mainPanel.add(descriptionPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton saveBtn = new JButton("💾 Save");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        saveBtn.addActionListener(e -> saveExpense());

        JButton cancelBtn = new JButton("❌ Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.addActionListener(e -> dispose());

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);
        mainPanel.add(buttonsPanel);

        // Scroll pane for main panel
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setBorder(null);
        add(mainScrollPane);
    }

    /**
     * Create a labeled panel.
     */
    private JPanel createLabeledPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setPreferredSize(new Dimension(80, 30));
        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    /**
     * Populate fields with existing expense data.
     */
    private void populateFields(Expense expense) {
        amountField.setText(String.valueOf(expense.getAmount()));
        categoryCombo.setSelectedItem(expense.getCategory());
        dateSpinner.setValue(java.sql.Date.valueOf(expense.getDate()));
        descriptionArea.setText(expense.getDescription());
    }

    /**
     * Save the expense (add or update).
     */
    private void saveExpense() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            String category = (String) categoryCombo.getSelectedItem();
            java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
            LocalDate date = new java.sql.Date(utilDate.getTime()).toLocalDate();
            String description = descriptionArea.getText().trim();

            if (expenseToEdit == null) {
                // Add new expense
                Expense newExpense = new Expense(amount, category, date, description);
                if (expenseDAO.addExpense(newExpense)) {
                    JOptionPane.showMessageDialog(this, "Expense added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCallback.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add expense.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing expense
                expenseToEdit.setAmount(amount);
                expenseToEdit.setCategory(category);
                expenseToEdit.setDate(date);
                expenseToEdit.setDescription(description);

                if (expenseDAO.updateExpense(expenseToEdit)) {
                    JOptionPane.showMessageDialog(this, "Expense updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCallback.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update expense.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validate input fields.
     */
    private boolean validateInputs() {
        String amount = amountField.getText().trim();

        if (amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double parsedAmount = Double.parseDouble(amount);
            if (parsedAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}
