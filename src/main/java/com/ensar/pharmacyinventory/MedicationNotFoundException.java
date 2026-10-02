package com.ensar.pharmacyinventory;

public class MedicationNotFoundException extends RuntimeException{
    public MedicationNotFoundException(String message){
        super(message);
    }
}
