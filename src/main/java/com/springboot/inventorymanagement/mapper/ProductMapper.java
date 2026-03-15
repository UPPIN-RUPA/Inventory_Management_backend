package com.springboot.inventorymanagement.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.springboot.inventorymanagement.dto.CreateProductRequest;
import com.springboot.inventorymanagement.dto.ProductResponse;
import com.springboot.inventorymanagement.dto.UpdateProductRequest;
import com.springboot.inventorymanagement.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        return new Product(request.getName(), request.getCategory(), request.getPrice());
    }

    public Product toEntity(UpdateProductRequest request) {
        return new Product(request.getName(), request.getCategory(), request.getPrice());
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.from(product);
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream().map(this::toResponse).toList();
    }
}
