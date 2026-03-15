package com.springboot.inventorymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.inventorymanagement.exception.DuplicateInventoryException;
import com.springboot.inventorymanagement.exception.InventoryNotFoundException;
import com.springboot.inventorymanagement.exception.ResourceNotFoundException;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.model.StockMovement;
import com.springboot.inventorymanagement.model.StockMovementType;
import com.springboot.inventorymanagement.repository.ProductInventoryRepository;
import com.springboot.inventorymanagement.repository.StockMovementRepository;

@Service
public class ProductInventoryService {

    @Autowired
    private ProductInventoryRepository inventoryRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    // Get all product inventories
    public Page<ProductInventory> getAllInventories(Pageable pageable, Long productId) {
        Specification<ProductInventory> specification = Specification.where(null);

        if (productId != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("product").get("id"), productId));
        }

        return inventoryRepository.findAll(specification, pageable);
    }

    public Page<ProductInventory> getLowStockInventories(Pageable pageable, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low-stock threshold must be zero or greater");
        }

        Specification<ProductInventory> specification = (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("itemsLeft"), threshold);

        return inventoryRepository.findAll(specification, pageable);
    }

    public Page<ProductInventory> getOutOfStockInventories(Pageable pageable) {
        Specification<ProductInventory> specification = (root, query, cb) ->
                cb.equal(root.get("itemsLeft"), 0);

        return inventoryRepository.findAll(specification, pageable);
    }

    // Get product inventory by ID
    public ProductInventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id " + id));
    }

    // Create a new product inventory
    public ProductInventory createInventory(ProductInventory inventory) {
        Long productId = inventory.getProduct().getId();
        if (productId != null && inventoryRepository.existsByProductId(productId)) {
            throw new DuplicateInventoryException("Inventory already exists for product id " + productId);
        }
        return inventoryRepository.save(inventory);
    }

    // Delete product inventory by ID
    public void deleteInventory(Long id) {
        ProductInventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id " + id));
        inventoryRepository.delete(existingInventory);
    }

    public ProductInventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product id " + productId));
    }

    @Transactional
    public ProductInventory restockInventory(Long productId, int quantity, String reason) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Restock reason is required");
        }

        ProductInventory inventory = getInventoryByProductId(productId);
        inventory.setItemsLeft(inventory.getItemsLeft() + quantity);
        ProductInventory savedInventory = inventoryRepository.save(inventory);
        stockMovementRepository.save(new StockMovement(savedInventory.getProduct(), StockMovementType.RESTOCK,
                quantity, java.time.Instant.now(), reason, savedInventory.getId()));
        return savedInventory;
    }
}
