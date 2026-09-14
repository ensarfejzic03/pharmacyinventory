package com.ensar.pharmacyinventory;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    private TransactionRepository transactionRepository;
    private InventoryRepository inventoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              InventoryRepository inventoryRepository){
        this.transactionRepository = transactionRepository;
        this.inventoryRepository = inventoryRepository;
    }
    @Transactional
    public void recordTransaction(int inventoryID,
                                int userID,
                                String transactionType,
                                int amountChanged){
        Inventory inventory = inventoryRepository.findById(inventoryID).orElseThrow();
        if (transactionType.equals("Stock In")) {
            inventory.setQuantity(inventory.getQuantity() + amountChanged);
        } else if (transactionType.equals("Stock Out")) {
            if (inventory.getQuantity() < amountChanged) {
                throw new IllegalArgumentException("Not enough stock available");
            }

            inventory.setQuantity(inventory.getQuantity() - amountChanged);
        }
        inventoryRepository.save(inventory);

        Transaction transaction = new Transaction();
        transaction.setInventoryID(inventoryID);
        transaction.setUserID(userID);
        transaction.setTransactionType(transactionType);
        transaction.setAmountChanged(amountChanged);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
    }
}
