package com.springboot.inventorymanagement.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.springboot.inventorymanagement.dto.CreateInventoryRequest;
import com.springboot.inventorymanagement.dto.InventoryResponse;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductInventory;

@Component
public class InventoryMapper {

    public ProductInventory toEntity(CreateInventoryRequest request, Product product) {
        return new ProductInventory(product, 0, request.getInitialStock());
    }

    public InventoryResponse toResponse(ProductInventory inventory) {
        return InventoryResponse.from(inventory);
    }

    public List<InventoryResponse> toResponseList(List<ProductInventory> inventories) {
        return inventories.stream().map(this::toResponse).toList();
    }
}
