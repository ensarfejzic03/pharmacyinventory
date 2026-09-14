package com.ensar.pharmacyinventory;

import jakarta.persistence.*;

@Entity
@Table(name="medications")
public class Medication {
    @Column(name="medication_name")
    private String name;
    @Column(name = "reorder_threshold")
    private int reorderThreshold;
    @Column(name="category")
    private String category;
    @Column(name="description")
    private String description;
    @Column(name="supplier_id")
    private int supplierID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="medication_id")
    private int medicationID;

    public Medication(){

    }
    public Medication (String name, int reorderThreshold, String category, String description,int supplierID) {
        this.name = name;
        this.reorderThreshold = reorderThreshold;
        this.category = category;
        this.supplierID = supplierID;
        this.description = description;
    }
    @ManyToOne
    @JoinColumn(name="supplier_id",insertable = false,updatable = false)
    private Supplier supplier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getMedicationID() {
        return medicationID;
    }

    public void setMedicationID(int medicationID) {
        this.medicationID = medicationID;
    }

    public Supplier getSupplier()  { return supplier; }
}
