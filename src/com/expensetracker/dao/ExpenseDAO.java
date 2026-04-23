package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import com.expensetracker.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Expense entity.
 * Handles all database operations (CRUD) for expenses.
 */
public class ExpenseDAO {

    /**
     * Add a new expense to the database.
     * @param expense The expense object to add
     * @return true if successful, false otherwise
     */
    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO Expense (amount, category, date, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, expense.getAmount());
            pstmt.setString(2, expense.getCategory());
            pstmt.setString(3, expense.getDate().toString());
            pstmt.setString(4, expense.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all expenses from the database.
     * @return List of all expenses
     */
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, date, description FROM Expense ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description")
                );
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return expenses;
    }

    /**
     * Get an expense by its ID.
     * @param id The expense ID
     * @return The Expense object, or null if not found
     */
    public Expense getExpenseById(int id) {
        String sql = "SELECT id, amount, category, date, description FROM Expense WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Expense(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expense by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Update an existing expense.
     * @param expense The expense object with updated values
     * @return true if successful, false otherwise
     */
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE Expense SET amount = ?, category = ?, date = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, expense.getAmount());
            pstmt.setString(2, expense.getCategory());
            pstmt.setString(3, expense.getDate().toString());
            pstmt.setString(4, expense.getDescription());
            pstmt.setInt(5, expense.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete an expense by its ID.
     * @param id The expense ID
     * @return true if successful, false otherwise
     */
    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM Expense WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get expenses filtered by category.
     * @param category The category to filter by
     * @return List of expenses in that category
     */
    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, date, description FROM Expense WHERE category = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description")
                );
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses by category: " + e.getMessage());
            e.printStackTrace();
        }
        
        return expenses;
    }

    /**
     * Get expenses within a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of expenses within the date range
     */
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, date, description FROM Expense WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description")
                );
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses by date range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return expenses;
    }

    /**
     * Get all unique categories from expenses.
     * @return List of unique category names
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM Expense ORDER BY category";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }

    /**
     * Calculate total spending.
     * @return Total amount of all expenses
     */
    public double getTotalSpending() {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM Expense";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total spending: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }

    /**
     * Calculate spending by category.
     * @param category The category to calculate spending for
     * @return Total spending in that category
     */
    public double getSpendingByCategory(String category) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM Expense WHERE category = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating spending by category: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }

    /**
     * Calculate monthly spending.
     * @param year The year
     * @param month The month (1-12)
     * @return Total spending in that month
     */
    public double getMonthlySpending(int year, int month) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM Expense WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%04d", year));
            pstmt.setString(2, String.format("%02d", month));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating monthly spending: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }

    /**
     * Search expenses by description.
     * @param keyword The search keyword (case-insensitive)
     * @return List of matching expenses
     */
    public List<Expense> searchByDescription(String keyword) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, date, description FROM Expense WHERE LOWER(description) LIKE ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description")
                );
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return expenses;
    }
}
