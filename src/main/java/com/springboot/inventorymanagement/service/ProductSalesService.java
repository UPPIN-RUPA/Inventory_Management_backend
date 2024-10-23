package com.springboot.inventorymanagement.service;



import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.repository.ProductSalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductSalesService {

    @Autowired
    private ProductSalesRepository salesRepository;

    // Get all sales
    public List<ProductSales> getAllSales() {
        return salesRepository.findAll();
    }

    // Get sales by ID
    public ProductSales getSalesById(Long id) {
        return salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales record not found with id " + id));
    }

    // Record a new sale
    public ProductSales createSales(ProductSales sales) {
        return salesRepository.save(sales);
    }

    // Update sales information by ID
    public ProductSales updateSales(Long id, ProductSales updatedSales) {
        ProductSales existingSales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales record not found with id " + id));

        existingSales.setCategory(updatedSales.getCategory());
        existingSales.setPriceAtSale(updatedSales.getPriceAtSale());
        existingSales.setSaleDate(updatedSales.getSaleDate());

        return salesRepository.save(existingSales);
    }

    // Delete sales by ID
    public void deleteSales(Long id) {
        ProductSales existingSales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales record not found with id " + id));
        salesRepository.delete(existingSales);
    }
}
