package com.springboot.inventorymanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
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
import org.springframework.test.web.servlet.MockMvc;

import com.springboot.inventorymanagement.config.OpenApiConfig;
import com.springboot.inventorymanagement.config.SecurityConfig;
import com.springboot.inventorymanagement.exception.GlobalExceptionHandler;
import com.springboot.inventorymanagement.mapper.StockMovementMapper;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.StockMovement;
import com.springboot.inventorymanagement.model.StockMovementType;
import com.springboot.inventorymanagement.service.StockMovementService;

@WebMvcTest(StockMovementController.class)
@Import({ SecurityConfig.class, OpenApiConfig.class, GlobalExceptionHandler.class, StockMovementMapper.class })
class StockMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockMovementService stockMovementService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void getStockMovementsUsesDefaultPaginationAndSort() throws Exception {
        Product product = new Product("Laptop", "Electronics", 75000);
        product.setId(1L);
        StockMovement movement = new StockMovement(product, StockMovementType.SALE, 2, Instant.parse("2026-03-14T10:15:30Z"),
                "Sale recorded", 1L);
        movement.setId(1L);

        when(stockMovementService.getStockMovements(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "occurredAt")),
                null, null, null, null))
                .thenReturn(new PageImpl<>(List.of(movement),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "occurredAt")), 1));

        mockMvc.perform(get("/api/stock-movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    void getStockMovementsRejectsNegativePage() throws Exception {
        mockMvc.perform(get("/api/stock-movements").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page number must be zero or greater"));
    }
}
