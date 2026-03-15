package com.springboot.inventorymanagement.dto;

import java.time.Instant;

import com.springboot.inventorymanagement.model.Product;

public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private double price;
    private Instant createdAt;
    private Instant updatedAt;

    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();
        response.id = product.getId();
        response.name = product.getName();
        response.category = product.getCategory();
        response.price = product.getPrice();
        response.createdAt = product.getCreatedAt();
        response.updatedAt = product.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
