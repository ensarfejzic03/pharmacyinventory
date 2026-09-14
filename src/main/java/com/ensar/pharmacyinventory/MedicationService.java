package com.ensar.pharmacyinventory;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MedicationService {
    private MedicationRepository medicationRepository;
    private InventoryRepository inventoryRepository;

    public MedicationService(MedicationRepository medicationRepository,
                             InventoryRepository inventoryRepository) {
        this.medicationRepository = medicationRepository;
        this.inventoryRepository = inventoryRepository;
    }
    @Transactional
    public void createMedicationWithInventory(
            String medicationName,
            String category,
            String description,
            int reorderThreshold,
            int supplierID,
            int quantity,
            String expirationDate,
            String location) {
        Medication medication = new Medication(
                medicationName,
                reorderThreshold,
                category,
                description,
                supplierID
        );
        Medication savedMedication = medicationRepository.save(medication);
        Inventory inventory = new Inventory();
        inventory.setMedicationID(savedMedication.getMedicationID());
        inventory.setQuantity(quantity);
        inventory.setExpirationDate(LocalDate.parse(expirationDate));
        inventory.setLocation(location);
        inventoryRepository.save(inventory);
    }
}
