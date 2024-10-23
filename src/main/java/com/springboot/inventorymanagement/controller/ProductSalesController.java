package com.springboot.inventorymanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.service.ProductSalesService;

@RestController
@RequestMapping("/api/sales")
public class ProductSalesController {

    @Autowired
    private ProductSalesService salesService;

    // Get all sales
    @GetMapping
    public List<ProductSales> getAllSales() {
        return salesService.getAllSales();
    }

    // Get a sales record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductSales> getSalesById(@PathVariable Long id) {
        ProductSales sales = salesService.getSalesById(id);
        return ResponseEntity.ok(sales);
    }

    // Create a new sales record
    @PostMapping
    public ResponseEntity<ProductSales> createSales(@RequestBody ProductSales sales) {
        ProductSales createdSales = salesService.createSales(sales);
        return new ResponseEntity<>(createdSales, HttpStatus.CREATED);
    }

    // Update sales record by ID
    @PutMapping("/{id}")
    public ResponseEntity<ProductSales> updateSales(@PathVariable Long id, @RequestBody ProductSales updatedSales) {
        ProductSales sales = salesService.updateSales(id, updatedSales);
        return ResponseEntity.ok(sales);
    }

    // Delete sales record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSales(@PathVariable Long id) {
        salesService.deleteSales(id);
        return ResponseEntity.noContent().build();
    }
}
