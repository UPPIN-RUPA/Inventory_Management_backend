package com.springboot.inventorymanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import java.util.List;

import com.springboot.inventorymanagement.config.OpenApiConfig;
import com.springboot.inventorymanagement.config.SecurityConfig;
import com.springboot.inventorymanagement.exception.GlobalExceptionHandler;
import com.springboot.inventorymanagement.mapper.InventoryMapper;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.service.ProductInventoryService;
import com.springboot.inventorymanagement.service.ProductService;

@WebMvcTest(ProductInventoryController.class)
@Import({ SecurityConfig.class, OpenApiConfig.class, GlobalExceptionHandler.class, InventoryMapper.class })
@TestPropertySource(properties = {
        "app.security.username=tester",
        "app.security.password=test-pass"
})
class ProductInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductInventoryService inventoryService;

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void createInventoryUsesInitialStock() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);
        ProductInventory inventory = new ProductInventory(product, 0, 20);

        when(productService.getProductById(1L)).thenReturn(product);
        when(inventoryService.createInventory(org.mockito.ArgumentMatchers.any(ProductInventory.class))).thenReturn(inventory);

        mockMvc.perform(post("/api/inventories")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"initialStock\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.product.name").value("Laptop"))
                .andExpect(jsonPath("$.itemsSold").value(0))
                .andExpect(jsonPath("$.itemsLeft").value(20));
    }

    @Test
    void restockInventoryReturnsUpdatedStock() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);
        ProductInventory inventory = new ProductInventory(product, 2, 30);

        when(inventoryService.restockInventory(1L, 10, "New shipment")).thenReturn(inventory);

        mockMvc.perform(post("/api/inventories/1/restock")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":10,\"reason\":\"New shipment\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemsLeft").value(30));
    }

    @Test
    void getLowStockInventoriesUsesThresholdAndPagination() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);
        ProductInventory inventory = new ProductInventory(product, 2, 5);
        inventory.setId(10L);

        when(inventoryService.getLowStockInventories(PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "itemsLeft")), 10))
                .thenReturn(new PageImpl<>(List.of(inventory), PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "itemsLeft")), 1));

        mockMvc.perform(get("/api/inventories/low-stock")
                .param("threshold", "10")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(10))
                .andExpect(jsonPath("$.items[0].product.id").value(1))
                .andExpect(jsonPath("$.items[0].itemsLeft").value(5))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    void getLowStockInventoriesRejectsNegativeThreshold() throws Exception {
        when(inventoryService.getLowStockInventories(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(-1)))
                .thenThrow(new IllegalArgumentException("Low-stock threshold must be zero or greater"));

        mockMvc.perform(get("/api/inventories/low-stock")
                .param("threshold", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Low-stock threshold must be zero or greater"));
    }

    @Test
    void getOutOfStockInventoriesReturnsPagedResults() throws Exception {
        Product product = new Product("Keyboard", "Electronics", 1000);
        product.setId(2L);
        ProductInventory inventory = new ProductInventory(product, 8, 0);
        inventory.setId(11L);

        when(inventoryService.getOutOfStockInventories(PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(new PageImpl<>(List.of(inventory), PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id")), 1));

        mockMvc.perform(get("/api/inventories/out-of-stock")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(11))
                .andExpect(jsonPath("$.items[0].product.id").value(2))
                .andExpect(jsonPath("$.items[0].itemsLeft").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    void getInventoriesRejectsZeroPageSize() throws Exception {
        mockMvc.perform(get("/api/inventories").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must be greater than zero"));
    }

    @Test
    void updateInventoryReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(put("/api/inventories/1")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message")
                        .value("Inventory records cannot be generically updated. Use restock operations for stock changes on inventory id 1"));
    }
}
