package com.springboot.inventorymanagement.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.springboot.inventorymanagement.dto.StockMovementResponse;
import com.springboot.inventorymanagement.model.StockMovement;

@Component
public class StockMovementMapper {

    public StockMovementResponse toResponse(StockMovement movement) {
        return StockMovementResponse.from(movement);
    }

    public List<StockMovementResponse> toResponseList(List<StockMovement> movements) {
        return movements.stream().map(this::toResponse).toList();
    }
}
