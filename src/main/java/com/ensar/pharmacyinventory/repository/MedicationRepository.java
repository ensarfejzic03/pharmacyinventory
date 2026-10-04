package com.ensar.pharmacyinventory.repository;

import com.ensar.pharmacyinventory.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication,Integer> {
    List<Medication> findByNameContainingIgnoreCase(String name);
}
