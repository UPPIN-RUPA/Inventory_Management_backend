package com.springboot.inventorymanagement.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.springboot.inventorymanagement.exception.ResourceNotFoundException;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductByIdThrowsWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void deleteProductDeletesLoadedEntity() {
        Product product = new Product("Phone", "Electronics", 599.0);
        product.setId(5L);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        productService.deleteProduct(5L);

        verify(productRepository).delete(product);
    }

    @Test
    void getProductByIdReturnsEntityWhenPresent() {
        Product product = new Product("Phone", "Electronics", 599.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertSame(product, result);
    }
}
