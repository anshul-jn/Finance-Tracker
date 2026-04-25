package com.expensetracker.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simple database viewer utility to display all data stored in the database.
 */
public class DatabaseViewer {

    public static void main(String[] args) {
        displayDatabaseData();
    }

    public static void displayDatabaseData() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 DATABASE VIEWER - Smart Expense Tracker");
        System.out.println("=".repeat(80) + "\n");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Display Expense Table
            System.out.println("\n📋 EXPENSE TABLE DATA:");
            System.out.println("-".repeat(80));
            displayExpenses(stmt);

            // Display Budget Table
            System.out.println("\n\n💰 BUDGET TABLE DATA:");
            System.out.println("-".repeat(80));
            displayBudgets(stmt);

            System.out.println("\n" + "=".repeat(80) + "\n");

        } catch (SQLException e) {
            System.err.println("❌ Error reading database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayExpenses(Statement stmt) throws SQLException {
        String query = "SELECT id, amount, category, date, description, created_at FROM Expense ORDER BY id DESC";
        try (ResultSet rs = stmt.executeQuery(query)) {
            boolean hasData = false;
            
            // Print header
            System.out.printf("%-5s | %-10s | %-15s | %-12s | %-30s | %-19s%n",
                    "ID", "Amount", "Category", "Date", "Description", "Created At");
            System.out.println("-".repeat(100));

            // Print data
            while (rs.next()) {
                hasData = true;
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                String date = rs.getString("date");
                String description = rs.getString("description");
                String createdAt = rs.getString("created_at");

                System.out.printf("%-5d | %-10.2f | %-15s | %-12s | %-30s | %-19s%n",
                        id, amount, category, date, 
                        (description != null ? description.substring(0, Math.min(30, description.length())) : "N/A"),
                        createdAt);
            }
            
            if (!hasData) {
                System.out.println("   ⚠️  (No expenses found in database)");
            }
        }
    }

    private static void displayBudgets(Statement stmt) throws SQLException {
        String query = "SELECT id, category, budget_limit, month, alert_enabled, created_at FROM Budget ORDER BY id DESC";
        try (ResultSet rs = stmt.executeQuery(query)) {
            boolean hasData = false;
            
            // Print header
            System.out.printf("%-5s | %-15s | %-12s | %-10s | %-13s | %-19s%n",
                    "ID", "Category", "Budget Limit", "Month", "Alert Enabled", "Created At");
            System.out.println("-".repeat(90));

            // Print data
            while (rs.next()) {
                hasData = true;
                int id = rs.getInt("id");
                String category = rs.getString("category");
                double budgetLimit = rs.getDouble("budget_limit");
                String month = rs.getString("month");
                int alertEnabled = rs.getInt("alert_enabled");
                String createdAt = rs.getString("created_at");

                System.out.printf("%-5d | %-15s | %-12.2f | %-10s | %-13s | %-19s%n",
                        id, category, budgetLimit, month, 
                        (alertEnabled == 1 ? "Yes" : "No"), createdAt);
            }
            
            if (!hasData) {
                System.out.println("   ⚠️  (No budgets found in database)");
            }
        }
    }
}
