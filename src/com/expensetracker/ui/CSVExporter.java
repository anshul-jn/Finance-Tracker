package com.expensetracker.ui;

import com.expensetracker.model.Expense;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting expenses to CSV format.
 */
public class CSVExporter {

    /**
     * Export expenses to a CSV file.
     * @param expenses List of expenses to export
     * @param filePath Path where CSV file will be saved
     * @throws IOException If file writing fails
     */
    public static void exportExpenses(List<Expense> expenses, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Amount,Category,Date,Description");
            writer.newLine();

            // Write data rows
            for (Expense expense : expenses) {
                StringBuilder line = new StringBuilder();
                line.append(expense.getId()).append(",");
                line.append(expense.getAmount()).append(",");
                line.append("\"").append(expense.getCategory()).append("\"").append(",");
                line.append(expense.getDate()).append(",");
                line.append("\"").append(expense.getDescription()).append("\"");

                writer.write(line.toString());
                writer.newLine();
            }
        }
    }
}
