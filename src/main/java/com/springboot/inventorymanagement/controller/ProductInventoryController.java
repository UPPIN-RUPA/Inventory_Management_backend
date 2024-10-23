package com.springboot.inventorymanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.service.ProductInventoryService;

@RestController
@RequestMapping("/api/inventories")
public class ProductInventoryController {

    @Autowired
    private ProductInventoryService inventoryService;

    // Get all product inventories
    @GetMapping
    public List<ProductInventory> getAllInventories() {
        return inventoryService.getAllInventories();
    }

    // Get product inventory by ID
    @GetMapping("/{id}")
    public ProductInventory getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    // Create a new product inventory
    @PostMapping
    public ProductInventory createInventory(@RequestBody ProductInventory inventory) {
        return inventoryService.createInventory(inventory);
    }

    // Update product inventory by ID
    @PutMapping("/{id}")
    public ProductInventory updateInventory(@PathVariable Long id, @RequestBody ProductInventory inventory) {
        return inventoryService.updateInventory(id, inventory);
    }

    // Delete product inventory by ID
    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
