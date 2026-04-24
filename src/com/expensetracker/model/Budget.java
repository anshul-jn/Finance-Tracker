package com.expensetracker.model;

import java.io.Serializable;
import java.time.YearMonth;

/**
 * Model class representing a Budget for a category.
 * Tracks budget limit and alerts for category spending.
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String category;
    private double limit;
    private YearMonth month;
    private double currentSpending;
    private boolean alertEnabled;

    /**
     * Constructor for creating a new Budget.
     */
    public Budget(String category, double limit, YearMonth month) {
        this.category = category;
        this.limit = limit;
        this.month = month;
        this.currentSpending = 0;
        this.alertEnabled = true;
    }

    /**
     * Constructor with all fields.
     */
    public Budget(int id, String category, double limit, YearMonth month, double currentSpending, boolean alertEnabled) {
        this.id = id;
        this.category = category;
        this.limit = limit;
        this.month = month;
        this.currentSpending = currentSpending;
        this.alertEnabled = alertEnabled;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public double getCurrentSpending() {
        return currentSpending;
    }

    public void setCurrentSpending(double currentSpending) {
        this.currentSpending = currentSpending;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    /**
     * Calculate remaining budget
     */
    public double getRemainingBudget() {
        return Math.max(0, limit - currentSpending);
    }

    /**
     * Calculate percentage spent (0-100)
     */
    public double getPercentageSpent() {
        if (limit == 0) return 0;
        return (currentSpending / limit) * 100;
    }

    /**
     * Check if budget is exceeded
     */
    public boolean isBudgetExceeded() {
        return currentSpending > limit;
    }

    /**
     * Check if approaching budget (80% spent)
     */
    public boolean isApproachingLimit() {
        return getPercentageSpent() >= 80 && !isBudgetExceeded();
    }

    @Override
    public String toString() {
        return category + " - $" + String.format("%.2f", limit);
    }
}
