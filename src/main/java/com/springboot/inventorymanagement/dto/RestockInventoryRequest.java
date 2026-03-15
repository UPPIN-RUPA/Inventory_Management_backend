package com.springboot.inventorymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class RestockInventoryRequest {
    @Positive(message = "Restock quantity must be greater than zero")
    private int quantity;

    @NotBlank(message = "Reason is required")
    private String reason;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
