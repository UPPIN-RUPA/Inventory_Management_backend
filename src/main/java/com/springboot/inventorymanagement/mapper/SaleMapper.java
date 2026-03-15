package com.springboot.inventorymanagement.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.springboot.inventorymanagement.dto.CreateSaleRequest;
import com.springboot.inventorymanagement.dto.SaleResponse;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductSales;

@Component
public class SaleMapper {

    public ProductSales toEntity(CreateSaleRequest request, Product product) {
        return new ProductSales(product, request.getQuantitySold(), request.getPriceAtSale(), request.getSaleDate());
    }

    public SaleResponse toResponse(ProductSales sale) {
        return SaleResponse.from(sale);
    }

    public List<SaleResponse> toResponseList(List<ProductSales> sales) {
        return sales.stream().map(this::toResponse).toList();
    }
}
