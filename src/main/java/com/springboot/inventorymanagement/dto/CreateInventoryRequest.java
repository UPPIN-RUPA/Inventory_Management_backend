package com.springboot.inventorymanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateInventoryRequest {
    @NotNull(message = "Product id is required")
    private Long productId;

    @PositiveOrZero(message = "Initial stock must be zero or greater")
    private int initialStock;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }
}
