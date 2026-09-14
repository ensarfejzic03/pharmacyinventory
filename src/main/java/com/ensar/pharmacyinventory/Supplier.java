package com.ensar.pharmacyinventory;

import jakarta.persistence.*;

@Entity
@Table(name="suppliers")
public class Supplier {
    @Id
    @GeneratedValue
    @Column(name="supplier_id")
    private int supplierID;
    @Column(name="supplier_name")
    private String supplierName;
    @Column(name="contact_name")
    private String contactName;
    @Column(name="phone")
    private String phone;
    @Column(name="email")
    private String email;
    @Column(name="address")
    private String address;
    public Supplier(){

    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
