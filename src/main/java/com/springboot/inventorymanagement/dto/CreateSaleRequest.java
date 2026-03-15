package com.springboot.inventorymanagement.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateSaleRequest {
    @NotNull(message = "Product id is required")
    private Long productId;

    @Positive(message = "Quantity sold must be greater than zero")
    private int quantitySold;

    @Positive(message = "Price at sale must be greater than zero")
    private double priceAtSale;

    @NotNull(message = "Sale date is required")
    private LocalDate saleDate;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(double priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
}
