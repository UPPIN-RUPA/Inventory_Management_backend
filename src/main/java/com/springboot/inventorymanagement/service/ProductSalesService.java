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
    private ProductSalesRepository productSalesRepository;

    public List<ProductSales> getSalesByDate(LocalDate date) {
        return productSalesRepository.findBySaleDate(date);
    }

    public ProductSales recordSale(Product product, int quantity, double price, LocalDate saleDate) {
        ProductSales sale = new ProductSales();
        sale.setProduct(product);
        sale.setQuantitySold(quantity);
        sale.setPriceAtSale(price);
        sale.setSaleDate(saleDate);
        return productSalesRepository.save(sale);
    }
}
