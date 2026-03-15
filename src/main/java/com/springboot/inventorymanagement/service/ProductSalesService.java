package com.springboot.inventorymanagement.service;

import com.springboot.inventorymanagement.exception.InventoryNotFoundException;
import com.springboot.inventorymanagement.exception.InsufficientStockException;
import com.springboot.inventorymanagement.exception.ResourceNotFoundException;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.model.StockMovement;
import com.springboot.inventorymanagement.model.StockMovementType;
import com.springboot.inventorymanagement.repository.ProductInventoryRepository;
import com.springboot.inventorymanagement.repository.ProductSalesRepository;
import com.springboot.inventorymanagement.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProductSalesService {

    @Autowired
    private ProductSalesRepository salesRepository;

    @Autowired
    private ProductInventoryRepository inventoryRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    // Get all sales
    public Page<ProductSales> getAllSales(Pageable pageable, Long productId, LocalDate saleDateFrom,
            LocalDate saleDateTo) {
        Specification<ProductSales> specification = Specification.where(null);

        if (productId != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("product").get("id"), productId));
        }
        if (saleDateFrom != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("saleDate"), saleDateFrom));
        }
        if (saleDateTo != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("saleDate"), saleDateTo));
        }

        return salesRepository.findAll(specification, pageable);
    }

    // Get sales by ID
    public ProductSales getSalesById(Long id) {
        return salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales record not found with id " + id));
    }

    // Record a new sale
    @Transactional
    public ProductSales createSales(ProductSales sales) {
        Long productId = sales.getProduct().getId();
        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product id " + productId));

        int quantitySold = sales.getQuantitySold();
        if (quantitySold <= 0) {
            throw new IllegalArgumentException("Quantity sold must be greater than zero");
        }
        if (inventory.getItemsLeft() < quantitySold) {
            throw new InsufficientStockException(
                    "Insufficient stock for product id " + productId + ". Available stock: " + inventory.getItemsLeft());
        }

        inventory.setItemsSold(inventory.getItemsSold() + quantitySold);
        inventory.setItemsLeft(inventory.getItemsLeft() - quantitySold);
        inventoryRepository.save(inventory);
        ProductSales savedSale = salesRepository.save(sales);
        stockMovementRepository.save(new StockMovement(savedSale.getProduct(), StockMovementType.SALE, quantitySold,
                Instant.now(), "Sale recorded", savedSale.getId()));
        return savedSale;
    }
}
