package com.ensar.pharmacyinventory;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private int transactionID;
    @Column(name="inventory_id")
    private int inventoryID;
    @Column(name="user_id")
    private int userID;
    @Column(name="transaction_type")
    private String transactionType;
    @Column(name="amount_changed")
    private int amountChanged;
    @Column(name="transaction_date")
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "inventory_id",insertable = false,updatable = false)
    private Inventory inventory;

    @ManyToOne
    @JoinColumn(name="user_id",insertable = false,updatable = false)
    private User user;

    public Transaction(){

    }

    public int getTransactionID() {
        return transactionID;
    }

    public int getInventoryID() {

        return inventoryID;
    }

    public int getUserID() {

        return userID;
    }

    public String getTransactionType() {

        return transactionType;
    }

    public int getAmountChanged() {

        return amountChanged;
    }

    public LocalDateTime getTransactionDate() {

        return transactionDate;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setAmountChanged(int amountChanged) {
        this.amountChanged = amountChanged;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public User getUser() {
        return user;
    }
}
