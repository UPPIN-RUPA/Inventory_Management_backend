package com.springboot.inventorymanagement.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.springboot.inventorymanagement.model.ProductSales;

public class SaleResponse {
    private Long id;
    private ProductSummaryResponse product;
    private int quantitySold;
    private double priceAtSale;
    private LocalDate saleDate;
    private Instant createdAt;

    public static SaleResponse from(ProductSales sale) {
        SaleResponse response = new SaleResponse();
        response.id = sale.getId();
        response.product = ProductSummaryResponse.from(sale.getProduct());
        response.quantitySold = sale.getQuantitySold();
        response.priceAtSale = sale.getPriceAtSale();
        response.saleDate = sale.getSaleDate();
        response.createdAt = sale.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public ProductSummaryResponse getProduct() {
        return product;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getPriceAtSale() {
        return priceAtSale;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
