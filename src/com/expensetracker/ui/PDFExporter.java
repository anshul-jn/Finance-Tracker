package com.expensetracker.ui;

import com.expensetracker.model.Expense;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced PDF Exporter using iText library.
 * Exports expense reports to PDF format with formatting.
 */
public class PDFExporter {
    
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Export expenses to PDF file.
     * Requires iText library: com.itextpdf.itextpdf
     * 
     * @param expenses List of expenses to export
     * @param filePath Path where PDF file will be saved
     * @throws IOException If file writing fails
     * @throws ClassNotFoundException If iText is not in classpath
     */
    public static void exportExpensesToPDF(List<Expense> expenses, String filePath) 
            throws IOException, ClassNotFoundException {
        
        try {
            // Try to load iText classes
            Class.forName("com.itextpdf.text.Document");
            
            // Use reflection to call iText methods (in case library is not available)
            exportWithIText(expenses, filePath);
            
        } catch (ClassNotFoundException e) {
            // Fallback to HTML-based PDF generation
            System.err.println("Warning: iText library not found. Generating HTML report instead.");
            exportAsHTML(expenses, filePath.replace(".pdf", ".html"));
        }
    }

    /**
     * Export using iText library (if available).
     */
    @SuppressWarnings("all")
    private static void exportWithIText(List<Expense> expenses, String filePath) throws IOException {
        try {
            // This requires iText library to be in classpath
            // com.itextpdf.text.Document
            // com.itextpdf.text.Paragraph
            // com.itextpdf.text.Table
            // com.itextpdf.text.pdf.PdfWriter
            
            Class<?> documentClass = Class.forName("com.itextpdf.text.Document");
            Class<?> pdfWriterClass = Class.forName("com.itextpdf.text.pdf.PdfWriter");
            Class<?> paragraphClass = Class.forName("com.itextpdf.text.Paragraph");
            Class<?> tableClass = Class.forName("com.itextpdf.text.Table");
            
            // Create document
            Object document = documentClass.newInstance();
            FileOutputStream fos = new FileOutputStream(filePath);
            
            Object writer = pdfWriterClass.getMethod("getInstance", 
                documentClass, FileOutputStream.class).invoke(null, document, fos);
            
            // Open document
            documentClass.getMethod("open").invoke(document);
            
            // Add title
            Object title = paragraphClass.getConstructor(String.class).newInstance("Expense Report");
            documentClass.getMethod("add", Class.forName("com.itextpdf.text.Element"))
                .invoke(document, title);
            
            // Add spacer
            Object spacer = paragraphClass.getConstructor(String.class).newInstance("\n");
            documentClass.getMethod("add", Class.forName("com.itextpdf.text.Element"))
                .invoke(document, spacer);
            
            // Add table
            Object table = tableClass.getConstructor(int.class).newInstance(5);
            
            // Add headers
            addTableCell(table, "ID");
            addTableCell(table, "Amount");
            addTableCell(table, "Category");
            addTableCell(table, "Date");
            addTableCell(table, "Description");
            
            // Add data rows
            for (Expense expense : expenses) {
                addTableCell(table, String.valueOf(expense.getId()));
                addTableCell(table, String.format("$%.2f", expense.getAmount()));
                addTableCell(table, expense.getCategory());
                addTableCell(table, dateFormatter.format(expense.getDate()));
                addTableCell(table, expense.getDescription());
            }
            
            documentClass.getMethod("add", Class.forName("com.itextpdf.text.Element"))
                .invoke(document, table);
            
            // Close document
            documentClass.getMethod("close").invoke(document);
            fos.close();
            
            System.out.println("✓ PDF exported successfully to: " + filePath);
            
        } catch (Exception e) {
            throw new IOException("Error generating PDF with iText: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to add table cell using reflection.
     */
    private static void addTableCell(Object table, String text) {
        try {
            Class<?> cellClass = Class.forName("com.itextpdf.text.Cell");
            Object cell = cellClass.getConstructor(String.class).newInstance(text);
            table.getClass().getMethod("addCell", cellClass).invoke(table, cell);
        } catch (Exception e) {
            System.err.println("Error adding table cell: " + e.getMessage());
        }
    }

    /**
     * Fallback: Export as HTML file (can be opened in browser and printed to PDF).
     */
    private static void exportAsHTML(List<Expense> expenses, String filePath) throws IOException {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filePath))) {
            
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("  <title>Expense Report</title>\n");
            writer.write("  <style>\n");
            writer.write("    body { font-family: Arial, sans-serif; margin: 20px; }\n");
            writer.write("    h1 { color: #333; }\n");
            writer.write("    table { border-collapse: collapse; width: 100%; }\n");
            writer.write("    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n");
            writer.write("    th { background-color: #4CAF50; color: white; }\n");
            writer.write("    tr:nth-child(even) { background-color: #f2f2f2; }\n");
            writer.write("  </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            
            writer.write("<h1>Expense Report</h1>\n");
            writer.write("<p>Generated on: " + new java.util.Date() + "</p>\n");
            
            writer.write("<table>\n");
            writer.write("  <tr>\n");
            writer.write("    <th>ID</th>\n");
            writer.write("    <th>Amount</th>\n");
            writer.write("    <th>Category</th>\n");
            writer.write("    <th>Date</th>\n");
            writer.write("    <th>Description</th>\n");
            writer.write("  </tr>\n");
            
            for (Expense expense : expenses) {
                writer.write("  <tr>\n");
                writer.write("    <td>" + expense.getId() + "</td>\n");
                writer.write("    <td>$" + String.format("%.2f", expense.getAmount()) + "</td>\n");
                writer.write("    <td>" + expense.getCategory() + "</td>\n");
                writer.write("    <td>" + dateFormatter.format(expense.getDate()) + "</td>\n");
                writer.write("    <td>" + expense.getDescription() + "</td>\n");
                writer.write("  </tr>\n");
            }
            
            writer.write("</table>\n");
            
            // Summary
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            writer.write("<p><strong>Total Expenses: $" + String.format("%.2f", total) + "</strong></p>\n");
            
            writer.write("</body>\n");
            writer.write("</html>\n");
        }
        
        System.out.println("✓ HTML report exported to: " + filePath);
        System.out.println("  Tip: Open in browser and use Print > Save as PDF for better formatting");
    }
}
