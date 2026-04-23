package com.expensetracker;

import com.expensetracker.ui.MainDashboard;
import com.expensetracker.util.DatabaseConnection;
import javax.swing.*;

/**
 * Main application entry point.
 * Initializes the database and launches the GUI.
 */
public class ExpenseTrackerApp {

    public static void main(String[] args) {
        // Initialize database
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   Smart Expense Tracker with Analytics              ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println("\n[*] Initializing application...\n");

        // Test database connection and create tables
        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n✗ FATAL: Database connection failed!");
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to database.\nPlease ensure SQLite driver is available.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Initialize database tables
        DatabaseConnection.initializeDatabase();

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainDashboard dashboard = new MainDashboard();
                dashboard.setVisible(true);
                System.out.println("\n✓ Application started successfully!");
                System.out.println("✓ Main Dashboard is now open.\n");
            } catch (Exception e) {
                System.err.println("✗ Error launching application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error launching application: " + e.getMessage(),
                        "Launch Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
