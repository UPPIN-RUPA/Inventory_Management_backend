package com.springboot.inventorymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.inventorymanagement.dto.StockMovementResponse;
import com.springboot.inventorymanagement.dto.PaginatedResponse;
import com.springboot.inventorymanagement.mapper.StockMovementMapper;
import com.springboot.inventorymanagement.model.StockMovementType;
import com.springboot.inventorymanagement.service.StockMovementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;

@RestController
@RequestMapping("/api/stock-movements")
@Tag(name = "Stock Movements", description = "View inventory-affecting stock movement history")
public class StockMovementController {

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @GetMapping
    @Operation(summary = "List stock movements", description = "Returns paginated stock movement history with optional filters. Public endpoint. Defaults: page=0, size=20, sort=occurredAt,desc. Maximum size is 100.")
    public PaginatedResponse<StockMovementResponse> getStockMovements(
            @Parameter(description = "Filter by product id")
            @RequestParam(required = false) Long productId,
            @Parameter(description = "Filter by movement type: SALE or RESTOCK")
            @RequestParam(required = false) StockMovementType type,
            @Parameter(description = "Filter from instant (inclusive), ISO-8601 format")
            @RequestParam(required = false) Instant dateFrom,
            @Parameter(description = "Filter to instant (inclusive), ISO-8601 format")
            @RequestParam(required = false) Instant dateTo,
            Pageable pageable) {
        Pageable normalizedPageable = PaginationUtils.normalize(pageable, Sort.by(Sort.Direction.DESC, "occurredAt"));
        Page<StockMovementResponse> responsePage = stockMovementService.getStockMovements(normalizedPageable, productId, type, dateFrom, dateTo)
                .map(stockMovementMapper::toResponse);
        return PaginatedResponse.from(responsePage);
    }
}
