package com.expensetracker.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model class representing an Expense entity.
 * This class encapsulates all data related to a single expense entry.
 */
public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private double amount;
    private String category;
    private LocalDate date;
    private String description;

    /**
     * Constructor for creating a new Expense.
     * Used when inserting new expenses (without ID - database generates it).
     */
    public Expense(double amount, String category, LocalDate date, String description) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    /**
     * Constructor for creating an Expense with all fields.
     * Used when fetching expenses from database.
     */
    public Expense(int id, double amount, String category, LocalDate date, String description) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
