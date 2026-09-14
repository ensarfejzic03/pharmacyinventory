package com.ensar.pharmacyinventory;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="inventory")
public class Inventory {
    @Column(name="inventory_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int inventoryID;
    @Column(name="medication_id")
    private int medicationID;
    @Column(name="quantity")
    private int quantity;
    @Column(name="expiration_date")
    private LocalDate expirationDate;
    @Column(name="location")
    private String location;
    @Column(name="last_updated")
    private LocalDateTime lastUpdated;
    public Inventory(){

    }
    @ManyToOne
    @JoinColumn(name="medication_id",insertable = false,updatable = false)
    private Medication medication;

    public Medication getMedication(){
        return medication;
    }

    public int getInventoryID() {
        return inventoryID;
    }

    public int getMedicationID() {
        return medicationID;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {

        return expirationDate;
    }

    public String getLocation() {

        return location;
    }

    public LocalDateTime getLastUpdated() {

        return lastUpdated;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public void setMedicationID(int medicationID) {
        this.medicationID = medicationID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
