package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Utility class.
 * Handles SQLite database connections and initialization.
 */
public class DatabaseConnection {
    // Database file location (will be created automatically if it doesn't exist)
    private static final String DATABASE_URL = "jdbc:sqlite:expenses.db";

    /**
     * Get a database connection.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DATABASE_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found. Make sure sqlite-jdbc JAR is in classpath.", e);
        }
    }

    /**
     * Initialize the database by creating the Expense table if it doesn't exist.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // SQL to create Expense table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Expense (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "amount REAL NOT NULL," +
                    "category TEXT NOT NULL," +
                    "date DATE NOT NULL," +
                    "description TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";
            
            stmt.execute(createTableSQL);
            System.out.println("✓ Database initialized successfully. Table 'Expense' ready.");
            
        } catch (SQLException e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test the database connection.
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null;
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
