package com.expensetracker.dao;

import com.expensetracker.model.Budget;
import com.expensetracker.util.DatabaseConnection;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Budget entity.
 * Handles all database operations for budgets.
 */
public class BudgetDAO {

    /**
     * Create the Budget table if it doesn't exist.
     */
    public static void initializeBudgetTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Budget (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "category TEXT NOT NULL," +
                    "budget_limit REAL NOT NULL," +
                    "month TEXT NOT NULL," +
                    "alert_enabled INTEGER DEFAULT 1," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE(category, month)" +
                    ");";
            
            stmt.execute(createTableSQL);
            System.out.println("✓ Budget table initialized.");
            
        } catch (SQLException e) {
            System.err.println("Error initializing budget table: " + e.getMessage());
        }
    }

    /**
     * Add a new budget or update if exists.
     */
    public boolean addBudget(Budget budget) {
        // Check if budget exists first
        Budget existing = getBudget(budget.getCategory(), budget.getMonth());
        
        if (existing != null) {
            // Update existing budget
            return updateBudget(budget);
        }
        
        String sql = "INSERT INTO Budget (category, budget_limit, month, alert_enabled) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, budget.getCategory());
            pstmt.setDouble(2, budget.getLimit());
            pstmt.setString(3, budget.getMonth().toString());
            pstmt.setInt(4, budget.isAlertEnabled() ? 1 : 0);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding budget: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all budgets for a specific month.
     */
    public List<Budget> getBudgetsByMonth(YearMonth month) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT id, category, budget_limit, month, alert_enabled FROM Budget WHERE month = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, month.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Budget budget = new Budget(
                    rs.getInt("id"),
                    rs.getString("category"),
                    rs.getDouble("budget_limit"),
                    YearMonth.parse(rs.getString("month")),
                    0, // currentSpending will be set separately
                    rs.getInt("alert_enabled") == 1
                );
                budgets.add(budget);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving budgets: " + e.getMessage());
        }
        
        return budgets;
    }

    /**
     * Get budget for a specific category and month.
     */
    public Budget getBudget(String category, YearMonth month) {
        String sql = "SELECT id, category, budget_limit, month, alert_enabled FROM Budget WHERE category = ? AND month = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            pstmt.setString(2, month.toString());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Budget(
                    rs.getInt("id"),
                    rs.getString("category"),
                    rs.getDouble("budget_limit"),
                    YearMonth.parse(rs.getString("month")),
                    0,
                    rs.getInt("alert_enabled") == 1
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving budget: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Update an existing budget.
     */
    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE Budget SET budget_limit = ?, alert_enabled = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, budget.getLimit());
            pstmt.setInt(2, budget.isAlertEnabled() ? 1 : 0);
            pstmt.setInt(3, budget.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating budget: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a budget.
     */
    public boolean deleteBudget(int id) {
        String sql = "DELETE FROM Budget WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting budget: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all budgets across all months.
     */
    public List<Budget> getAllBudgets() {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT id, category, budget_limit, month, alert_enabled FROM Budget ORDER BY month DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Budget budget = new Budget(
                    rs.getInt("id"),
                    rs.getString("category"),
                    rs.getDouble("budget_limit"),
                    YearMonth.parse(rs.getString("month")),
                    0,
                    rs.getInt("alert_enabled") == 1
                );
                budgets.add(budget);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving budgets: " + e.getMessage());
        }
        
        return budgets;
    }
}
