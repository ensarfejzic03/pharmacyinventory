package com.ensar.pharmacyinventory;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getLowStock() {
        return inventoryRepository.findLowStock();
    }

    public List<Inventory> getExpiringSoon(LocalDate date) {
        return inventoryRepository.findExpiringSoon(date);
    }

    public List<Inventory> getAllOrderByExpirationDate() {
        return inventoryRepository.findAllOrderByExpirationDate();
    }
}
