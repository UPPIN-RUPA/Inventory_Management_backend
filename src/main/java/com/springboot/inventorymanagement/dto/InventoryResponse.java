package com.springboot.inventorymanagement.dto;

import java.time.Instant;

import com.springboot.inventorymanagement.model.ProductInventory;

public class InventoryResponse {
    private Long id;
    private ProductSummaryResponse product;
    private int itemsSold;
    private int itemsLeft;
    private Instant createdAt;
    private Instant updatedAt;

    public static InventoryResponse from(ProductInventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.id = inventory.getId();
        response.product = ProductSummaryResponse.from(inventory.getProduct());
        response.itemsSold = inventory.getItemsSold();
        response.itemsLeft = inventory.getItemsLeft();
        response.createdAt = inventory.getCreatedAt();
        response.updatedAt = inventory.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public ProductSummaryResponse getProduct() {
        return product;
    }

    public int getItemsSold() {
        return itemsSold;
    }

    public int getItemsLeft() {
        return itemsLeft;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
