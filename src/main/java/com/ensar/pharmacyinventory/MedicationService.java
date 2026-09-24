package com.ensar.pharmacyinventory;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicationService {
    private final MedicationRepository medicationRepository;
    private final InventoryRepository inventoryRepository;

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
    @Transactional
    public void updateMedication(
            int medicationID,
            String medicationName,
            String category,
            int reorderThreshold,
            int supplierID,
            String description) {

        Medication medication = medicationRepository.findById(medicationID).orElseThrow();

        medication.setName(medicationName);
        medication.setCategory(category);
        medication.setReorderThreshold(reorderThreshold);
        medication.setSupplierID(supplierID);
        medication.setDescription(description);

        medicationRepository.save(medication);
    }
    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }
    public Medication getMedicationById(int id) {
        return medicationRepository.findById(id).orElseThrow();
    }
    public List<Medication> searchMedications(String searchTerm) {
        return medicationRepository.findByNameContainingIgnoreCase(searchTerm);
    }
    public long getMedicationCount() {
        return medicationRepository.count();
    }
}
