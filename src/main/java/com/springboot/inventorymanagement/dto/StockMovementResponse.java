package com.springboot.inventorymanagement.dto;

import java.time.Instant;

import com.springboot.inventorymanagement.model.StockMovement;
import com.springboot.inventorymanagement.model.StockMovementType;

public class StockMovementResponse {
    private Long id;
    private ProductSummaryResponse product;
    private StockMovementType type;
    private int quantity;
    private Instant occurredAt;
    private Instant createdAt;
    private String note;
    private Long referenceId;

    public static StockMovementResponse from(StockMovement movement) {
        StockMovementResponse response = new StockMovementResponse();
        response.id = movement.getId();
        response.product = ProductSummaryResponse.from(movement.getProduct());
        response.type = movement.getType();
        response.quantity = movement.getQuantity();
        response.occurredAt = movement.getOccurredAt();
        response.createdAt = movement.getCreatedAt();
        response.note = movement.getNote();
        response.referenceId = movement.getReferenceId();
        return response;
    }

    public Long getId() {
        return id;
    }

    public ProductSummaryResponse getProduct() {
        return product;
    }

    public StockMovementType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getNote() {
        return note;
    }

    public Long getReferenceId() {
        return referenceId;
    }
}
