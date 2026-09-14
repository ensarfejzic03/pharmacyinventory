package com.ensar.pharmacyinventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.quantity < i.medication.reorderThreshold
            ORDER BY i.quantity ASC
            """)
    List<Inventory> findLowStock();

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.expirationDate IS NOT NULL
            AND i.expirationDate >= CURRENT_DATE
            AND i.expirationDate <= :cutoffDate
            ORDER BY i.expirationDate ASC
            """)
    List<Inventory> findExpiringSoon(
            @Param("cutoffDate") LocalDate cutoffDate
    );

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.expirationDate IS NOT NULL
            ORDER BY i.expirationDate ASC
            """)
    List<Inventory> findAllOrderByExpirationDate();
}