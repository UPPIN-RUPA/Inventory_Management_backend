package com.springboot.inventorymanagement.config;

import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.repository.ProductRepository;
import com.springboot.inventorymanagement.service.ProductInventoryService;
import com.springboot.inventorymanagement.service.ProductSalesService;
import com.springboot.inventorymanagement.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@Configuration
@Profile("dev")
public class DevDataLoader {

    @Bean
    CommandLineRunner seedDevData(
            ProductRepository productRepository,
            ProductService productService,
            ProductInventoryService productInventoryService,
            ProductSalesService productSalesService) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            Product laptop = productService.saveProduct(new Product("MacBook Air M3", "Electronics", 1299.0));
            Product keyboard = productService.saveProduct(new Product("Mechanical Keyboard", "Electronics", 129.0));
            Product chair = productService.saveProduct(new Product("Ergonomic Chair", "Furniture", 349.0));

            productInventoryService.createInventory(new ProductInventory(laptop, 0, 12));
            productInventoryService.createInventory(new ProductInventory(keyboard, 0, 25));
            productInventoryService.createInventory(new ProductInventory(chair, 0, 8));

            productSalesService.createSales(new ProductSales(laptop, 2, 1249.0, LocalDate.now().minusDays(10)));
            productSalesService.createSales(new ProductSales(keyboard, 4, 119.0, LocalDate.now().minusDays(3)));
        };
    }
}
