package com.springboot.inventorymanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.springboot.inventorymanagement.config.OpenApiConfig;
import com.springboot.inventorymanagement.config.SecurityConfig;
import com.springboot.inventorymanagement.exception.GlobalExceptionHandler;
import com.springboot.inventorymanagement.exception.InsufficientStockException;
import com.springboot.inventorymanagement.mapper.SaleMapper;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.service.ProductSalesService;
import com.springboot.inventorymanagement.service.ProductService;

@WebMvcTest(ProductSalesController.class)
@Import({ SecurityConfig.class, OpenApiConfig.class, GlobalExceptionHandler.class, SaleMapper.class })
@TestPropertySource(properties = {
        "app.security.username=tester",
        "app.security.password=test-pass"
})
class ProductSalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductSalesService salesService;

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void getSalesUsesDefaultPaginationAndSort() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);
        ProductSales sale = new ProductSales(product, 2, 75000, LocalDate.of(2026, 3, 14));
        sale.setId(1L);

        when(salesService.getAllSales(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "saleDate")), null, null, null))
                .thenReturn(new PageImpl<>(List.of(sale), PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "saleDate")), 1));

        mockMvc.perform(get("/api/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    void getSalesRejectsPageSizeAboveMaximum() throws Exception {
        mockMvc.perform(get("/api/sales").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must not be greater than 100"));
    }

    @Test
    void createSalesReturnsConflictWhenStockIsInsufficient() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);

        when(productService.getProductById(1L)).thenReturn(product);
        when(salesService.createSales(any(ProductSales.class)))
                .thenThrow(new InsufficientStockException("Insufficient stock for product id 1. Available stock: 1"));

        mockMvc.perform(post("/api/sales")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantitySold\":2,\"priceAtSale\":75000,\"saleDate\":\"2026-03-14\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Insufficient stock for product id 1. Available stock: 1"));
    }

    @Test
    void createSalesReturnsCreatedWhenStockIsAvailable() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);
        ProductSales sale = new ProductSales(product, 2, 75000, LocalDate.of(2026, 3, 14));

        when(productService.getProductById(1L)).thenReturn(product);
        when(salesService.createSales(any(ProductSales.class))).thenReturn(sale);

        mockMvc.perform(post("/api/sales")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantitySold\":2,\"priceAtSale\":75000,\"saleDate\":\"2026-03-14\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantitySold").value(2))
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.product.name").value("Laptop"));
    }

    @Test
    void updateSalesReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(put("/api/sales/1")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantitySold\":2,\"priceAtSale\":75000,\"saleDate\":\"2026-03-14\"}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message")
                        .value("Sales records are immutable. Update and delete operations are not supported for sale id 1"));
    }

    @Test
    void deleteSalesReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(delete("/api/sales/1")
                .with(httpBasic("tester", "test-pass")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message")
                        .value("Sales records are immutable. Update and delete operations are not supported for sale id 1"));
    }
}
