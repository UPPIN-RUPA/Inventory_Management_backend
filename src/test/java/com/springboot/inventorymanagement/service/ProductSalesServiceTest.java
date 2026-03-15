package com.springboot.inventorymanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.springboot.inventorymanagement.exception.InsufficientStockException;
import com.springboot.inventorymanagement.exception.InventoryNotFoundException;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.repository.ProductInventoryRepository;
import com.springboot.inventorymanagement.repository.ProductSalesRepository;
import com.springboot.inventorymanagement.repository.StockMovementRepository;

@ExtendWith(MockitoExtension.class)
class ProductSalesServiceTest {

    @Mock
    private ProductSalesRepository salesRepository;

    @Mock
    private ProductInventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private ProductSalesService productSalesService;

    @Test
    void createSalesReducesInventoryAndPersistsSale() {
        Product product = new Product("Keyboard", "Electronics", 45.0);
        product.setId(3L);

        ProductInventory inventory = new ProductInventory(product, 2, 10);
        ProductSales sale = new ProductSales(product, 4, 39.0, LocalDate.of(2026, 2, 1));

        when(inventoryRepository.findByProductId(3L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(salesRepository.save(sale)).thenReturn(sale);

        ProductSales result = productSalesService.createSales(sale);

        assertEquals(sale, result);
        assertEquals(6, inventory.getItemsSold());
        assertEquals(6, inventory.getItemsLeft());
        verify(inventoryRepository).save(inventory);
        verify(salesRepository).save(sale);
    }

    @Test
    void createSalesRejectsOversell() {
        Product product = new Product("Keyboard", "Electronics", 45.0);
        product.setId(3L);

        ProductInventory inventory = new ProductInventory(product, 2, 3);
        ProductSales sale = new ProductSales(product, 4, 39.0, LocalDate.of(2026, 2, 1));

        when(inventoryRepository.findByProductId(3L)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientStockException.class, () -> productSalesService.createSales(sale));

        verify(inventoryRepository, never()).save(inventory);
        verify(salesRepository, never()).save(sale);
    }

    @Test
    void createSalesFailsWhenInventoryMissing() {
        Product product = new Product("Keyboard", "Electronics", 45.0);
        product.setId(3L);
        ProductSales sale = new ProductSales(product, 4, 39.0, LocalDate.of(2026, 2, 1));

        when(inventoryRepository.findByProductId(3L)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> productSalesService.createSales(sale));

        verify(salesRepository, never()).save(sale);
    }
}
