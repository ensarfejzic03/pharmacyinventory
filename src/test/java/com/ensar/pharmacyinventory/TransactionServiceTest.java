package com.ensar.pharmacyinventory;

import com.ensar.pharmacyinventory.entity.Inventory;
import com.ensar.pharmacyinventory.exception.InsufficientStockException;
import com.ensar.pharmacyinventory.exception.InvalidTransactionException;
import com.ensar.pharmacyinventory.exception.InventoryNotFoundException;
import com.ensar.pharmacyinventory.repository.InventoryRepository;
import com.ensar.pharmacyinventory.repository.TransactionRepository;
import com.ensar.pharmacyinventory.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private InventoryRepository inventoryRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        inventoryRepository = Mockito.mock(InventoryRepository.class);

        transactionService = new TransactionService(
                transactionRepository,
                inventoryRepository
        );
    }
    @Test
    void stockOutShouldReduceInventoryQuantity() {

        Inventory inventory = new Inventory();
        inventory.setInventoryID(1);
        inventory.setQuantity(10);

        when(inventoryRepository.findById(1))
                .thenReturn(Optional.of(inventory));

        transactionService.recordTransaction(
                1,
                1,
                "Stock Out",
                4
        );

        assertEquals(6, inventory.getQuantity());
    }
    @Test
    void stockOutShouldThrowExceptionWhenNotEnoughStock() {

        Inventory inventory = new Inventory();
        inventory.setInventoryID(1);
        inventory.setQuantity(10);

        when(inventoryRepository.findById(1))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                InsufficientStockException.class,
                () -> transactionService.recordTransaction(
                        1,
                        1,
                        "Stock Out",
                        20
                )
        );
    }
    @Test
    void transactionShouldThrowExceptionWhenAmountIsZero() {

        assertThrows(
                InvalidTransactionException.class,
                () -> transactionService.recordTransaction(
                        1,
                        1,
                        "Stock In",
                        0
                )
        );
    }
    @Test
    void transactionShouldThrowExceptionForInvalidType() {

        assertThrows(
                InvalidTransactionException.class,
                () -> transactionService.recordTransaction(
                        1,
                        1,
                        "Transfer",
                        5
                )
        );
    }
    @Test
    void transactionShouldThrowExceptionWhenInventoryDoesNotExist() {

        when(inventoryRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> transactionService.recordTransaction(
                        999,
                        1,
                        "Stock In",
                        5
                )
        );
    }
}
