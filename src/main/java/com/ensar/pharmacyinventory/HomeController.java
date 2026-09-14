package com.ensar.pharmacyinventory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class HomeController {
    private MedicationRepository medicationRepository;
    private SupplierRepository supplierRepository;
    private UserRepository userRepository;
    private InventoryRepository inventoryRepository;
    private TransactionRepository transactionRepository;

    @GetMapping("/api/medications")
    public List<Medication> getMedications() {
        return medicationRepository.findAll();
    }

    @PostMapping("/api/medications")
    public void createMedication(@RequestBody Medication medication) {
        medicationRepository.save(medication);
    }

    public HomeController(MedicationRepository medicationRepository,SupplierRepository supplierRepository, UserRepository userRepository,InventoryRepository inventoryRepository, TransactionRepository transactionRepository) {
        this.medicationRepository = medicationRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
    }
    @GetMapping("/api/suppliers")
    public List<Supplier> getSuppliers(){
        return supplierRepository.findAll();
    }
    @GetMapping("/api/users")
    public List<User>getUsers(){
        return userRepository.findAll();
    }
    @GetMapping("/api/inventory")
    public List<Inventory> getInventory(){
        return inventoryRepository.findAll();
    }
    @GetMapping("/api/transactions")
    public List<Transaction> getTransactions(){
        return transactionRepository.findAll();
    }
}
