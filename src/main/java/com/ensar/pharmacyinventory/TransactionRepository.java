package com.ensar.pharmacyinventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    @Query("""
            SELECT t.inventory.medication.name,
                   COUNT(t),
                   SUM(t.amountChanged)
            FROM Transaction t
            WHERE t.transactionType = 'Stock Out'
            GROUP BY t.inventory.medication.name
            ORDER BY SUM(t.amountChanged) DESC
            """)
    List<Object[]> findMostUsedMedications();

    @Query("""
            SELECT t.inventory.medication.name,
                   YEAR(t.transactionDate),
                   MONTH(t.transactionDate),
                   SUM(t.amountChanged)
            FROM Transaction t
            WHERE t.transactionType = 'Stock Out'
            GROUP BY t.inventory.medication.name,
                     YEAR(t.transactionDate),
                     MONTH(t.transactionDate)
            ORDER BY YEAR(t.transactionDate) DESC,
                     MONTH(t.transactionDate) DESC,
                     t.inventory.medication.name ASC
            """)
    List<Object[]> findMonthlyUsageTrends();

    @Query("""
            SELECT t.inventory.medication.name,
                   t.inventory.quantity,
                   t.inventory.medication.reorderThreshold,
                   COUNT(t),
                   SUM(t.amountChanged)
            FROM Transaction t
            WHERE t.transactionType = 'Stock Out'
            AND t.inventory.quantity
                < t.inventory.medication.reorderThreshold
            GROUP BY t.inventory.inventoryID,
                     t.inventory.medication.name,
                     t.inventory.quantity,
                     t.inventory.medication.reorderThreshold
            ORDER BY SUM(t.amountChanged) DESC
            """)
    List<Object[]> findHighRiskMedications();
}