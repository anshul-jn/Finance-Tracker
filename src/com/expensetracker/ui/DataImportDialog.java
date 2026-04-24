package com.expensetracker.ui;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for importing expenses from CSV and Excel files.
 * Supports drag-and-drop file import.
 */
public class DataImportDialog extends JDialog {
    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 500;
    
    private JLabel filePathLabel;
    private JTextArea previewArea;
    private JButton importBtn;
    private JProgressBar progressBar;
    private ExpenseDAO expenseDAO;
    private File selectedFile;
    private Runnable refreshCallback;

    public DataImportDialog(Frame parent, Runnable refreshCallback) {
        super(parent, "Import Expenses from File", true);
        this.expenseDAO = new ExpenseDAO();
        this.refreshCallback = refreshCallback;
        
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        initializeUI();
        setVisible(true);
    }

    /**
     * Initialize the dialog UI components.
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel - File selection
        JPanel filePanel = createFilePanel();
        mainPanel.add(filePanel, BorderLayout.NORTH);

        // Middle panel - Preview
        JPanel previewPanel = createPreviewPanel();
        mainPanel.add(previewPanel, BorderLayout.CENTER);

        // Bottom panel - Progress and buttons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Create file selection panel with drag-and-drop.
     */
    private JPanel createFilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(new TitledBorder("1. Select File"));

        // Drag-drop area
        JPanel dropArea = new JPanel(new BorderLayout());
        dropArea.setBackground(new Color(230, 240, 250));
        dropArea.setBorder(BorderFactory.createDashedBorder(Color.BLUE, 2, 5));
        
        JLabel dropLabel = new JLabel("<html><center>📁 Drag & Drop CSV or Excel file here<br/>or click Browse</center></html>");
        dropLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dropLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dropArea.add(dropLabel, BorderLayout.CENTER);

        // Enable drag-and-drop
        enableDragDrop(dropArea);

        // Browse button
        JButton browseBtn = new JButton("📂 Browse");
        browseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        browseBtn.addActionListener(e -> browseFile());
        dropArea.add(browseBtn, BorderLayout.EAST);

        panel.add(dropArea, BorderLayout.CENTER);

        // File path label
        filePathLabel = new JLabel("No file selected");
        filePathLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(filePathLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create preview panel.
     */
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("2. Preview Data"));

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        previewArea.setText("Select a file to preview data...");
        
        JScrollPane scrollPane = new JScrollPane(previewArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create bottom panel with progress bar and buttons.
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        panel.add(progressBar, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        importBtn = new JButton("✓ Import");
        importBtn.setFont(new Font("Arial", Font.BOLD, 12));
        importBtn.setEnabled(false);
        importBtn.addActionListener(e -> importData());
        buttonPanel.add(importBtn);

        JButton cancelBtn = new JButton("✗ Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Enable drag-and-drop functionality.
     */
    private void enableDragDrop(JComponent component) {
        new DropTarget(component, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent e) {
                try {
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) e.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    if (!files.isEmpty()) {
                        selectedFile = files.get(0);
                        loadFile(selectedFile);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DataImportDialog.this,
                        "Error reading file: " + ex.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Browse for file.
     */
    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Excel Files (*.xlsx, *.xls)", "xlsx", "xls"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            loadFile(selectedFile);
        }
    }

    /**
     * Load and preview file.
     */
    private void loadFile(File file) {
        try {
            String fileName = file.getName();
            filePathLabel.setText("Selected: " + fileName);

            if (fileName.endsWith(".csv")) {
                loadCSV(file);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                loadExcel(file);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Use CSV or Excel.");
            }

            importBtn.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error reading file: " + e.getMessage(),
                "Import Error", JOptionPane.ERROR_MESSAGE);
            importBtn.setEnabled(false);
        }
    }

    /**
     * Load CSV file.
     */
    private void loadCSV(File file) throws IOException {
        StringBuilder preview = new StringBuilder();
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && lineCount < 10) {
                preview.append(line).append("\n");
                lineCount++;
            }
        }

        if (lineCount < 10) {
            previewArea.setText(preview.toString());
        } else {
            previewArea.setText(preview.toString() + "... (" + "more lines" + ")");
        }
    }

    /**
     * Load Excel file (requires Apache POI library).
     */
    private void loadExcel(File file) throws IOException {
        try {
            // Try to load using Apache POI if available
            Class.forName("org.apache.poi.ss.usermodel.Workbook");
            loadExcelWithPOI(file);
        } catch (ClassNotFoundException e) {
            previewArea.setText("Note: Apache POI library not found.\n\n" +
                "Excel support requires: poi-5.0.0.jar and dependencies\n\n" +
                "Please use CSV format for import, or add POI library to lib/ folder.\n\n" +
                "CSV Format:\nID,Amount,Category,Date,Description\n" +
                "1,50.00,Food,2024-01-15,Lunch at restaurant");
        }
    }

    /**
     * Load Excel file using Apache POI (if available).
     */
    @SuppressWarnings("all")
    private void loadExcelWithPOI(File file) throws IOException {
        try {
            // This requires Apache POI libraries
            // For now, show a placeholder message
            previewArea.setText("Excel file selected: " + file.getName() + "\n\n" +
                "Note: Excel import requires Apache POI library.\n\n" +
                "If not installed, please use CSV format instead.");
        } catch (Exception e) {
            throw new IOException("Error reading Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Import data into database.
     */
    private void importData() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", 
                "No File", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private int importedCount = 0;

            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setVisible(true);
                importBtn.setEnabled(false);

                String fileName = selectedFile.getName();
                if (fileName.endsWith(".csv")) {
                    importCSV(selectedFile);
                } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                    importExcel(selectedFile);
                }

                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                importBtn.setEnabled(true);
                
                try {
                    get();
                    JOptionPane.showMessageDialog(DataImportDialog.this,
                        "Successfully imported " + importedCount + " expenses!",
                        "Import Success", JOptionPane.INFORMATION_MESSAGE);
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    dispose();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DataImportDialog.this,
                        "Error during import: " + e.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            private void importCSV(File file) throws IOException {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String headerLine = reader.readLine();
                    if (headerLine == null) {
                        throw new IOException("CSV file is empty");
                    }

                    String line;
                    int lineNumber = 2;
                    importedCount = 0;

                    while ((line = reader.readLine()) != null) {
                        try {
                            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                            
                            if (parts.length >= 4) {
                                double amount = Double.parseDouble(parts[1].trim());
                                String category = parts[2].trim().replaceAll("\"", "");
                                LocalDate date = LocalDate.parse(parts[3].trim());
                                String description = parts.length > 4 ? 
                                    parts[4].trim().replaceAll("\"", "") : "";

                                Expense expense = new Expense(amount, category, date, description);
                                if (expenseDAO.addExpense(expense)) {
                                    importedCount++;
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing line " + lineNumber + ": " + e.getMessage());
                        }
                        lineNumber++;
                    }
                }
            }

            private void importExcel(File file) throws IOException {
                // Placeholder for Excel import using Apache POI
                JOptionPane.showMessageDialog(DataImportDialog.this,
                    "Excel import is currently under development.\n" +
                    "Please use CSV format for now.",
                    "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
    }
}
