package com.springboot.inventorymanagement.dto;

import com.springboot.inventorymanagement.model.Product;

public class ProductSummaryResponse {
    private Long id;
    private String name;

    public static ProductSummaryResponse from(Product product) {
        ProductSummaryResponse response = new ProductSummaryResponse();
        response.id = product.getId();
        response.name = product.getName();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
