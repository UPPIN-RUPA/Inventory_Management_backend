package com.springboot.inventorymanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.springboot.inventorymanagement.exception.DuplicateInventoryException;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.repository.ProductInventoryRepository;
import com.springboot.inventorymanagement.repository.StockMovementRepository;

@ExtendWith(MockitoExtension.class)
class ProductInventoryServiceTest {

    @Mock
    private ProductInventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private ProductInventoryService productInventoryService;

    @Test
    void createInventoryRejectsDuplicateProductInventory() {
        Product product = new Product("Mouse", "Electronics", 25.0);
        product.setId(2L);
        ProductInventory inventory = new ProductInventory(product, 0, 20);

        when(inventoryRepository.existsByProductId(2L)).thenReturn(true);

        assertThrows(DuplicateInventoryException.class, () -> productInventoryService.createInventory(inventory));
    }

    @Test
    void restockInventoryIncreasesItemsLeft() {
        Product product = new Product("Mouse", "Electronics", 25.0);
        product.setId(2L);
        ProductInventory existing = new ProductInventory(product, 4, 16);

        when(inventoryRepository.findByProductId(2L)).thenReturn(Optional.of(existing));
        when(inventoryRepository.save(existing)).thenReturn(existing);

        ProductInventory result = productInventoryService.restockInventory(2L, 5, "New shipment");

        assertEquals(21, result.getItemsLeft());
        assertEquals(4, result.getItemsSold());
        verify(inventoryRepository).save(existing);
    }
}
