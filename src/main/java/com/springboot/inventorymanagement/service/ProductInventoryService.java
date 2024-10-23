package com.springboot.inventorymanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.repository.ProductInventoryRepository;

@Service
public class ProductInventoryService {

    @Autowired
    private ProductInventoryRepository inventoryRepository;

    // Get all product inventories
    public List<ProductInventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    // Get product inventory by ID
    public ProductInventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id " + id));
    }

    // Create a new product inventory
    public ProductInventory createInventory(ProductInventory inventory) {
        return inventoryRepository.save(inventory);
    }

    // Update product inventory by ID
    public ProductInventory updateInventory(Long id, ProductInventory updatedInventory) {
        ProductInventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id " + id));

        existingInventory.setCategory(updatedInventory.getCategory());
        existingInventory.setItemsSold(updatedInventory.getItemsSold());
        existingInventory.setItemsLeft(updatedInventory.getItemsLeft());

        return inventoryRepository.save(existingInventory);
    }

    // Delete product inventory by ID
    public void deleteInventory(Long id) {
        ProductInventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id " + id));
        inventoryRepository.delete(existingInventory);
    }
}
