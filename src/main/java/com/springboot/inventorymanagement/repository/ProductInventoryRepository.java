package com.springboot.inventorymanagement.repository;
import com.springboot.inventorymanagement.model.ProductInventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long>,
        JpaSpecificationExecutor<ProductInventory> {
    Optional<ProductInventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);
}
