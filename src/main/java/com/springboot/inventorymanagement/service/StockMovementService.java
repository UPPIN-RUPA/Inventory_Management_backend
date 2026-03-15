package com.springboot.inventorymanagement.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.springboot.inventorymanagement.model.StockMovement;
import com.springboot.inventorymanagement.model.StockMovementType;
import com.springboot.inventorymanagement.repository.StockMovementRepository;

@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public Page<StockMovement> getStockMovements(Pageable pageable, Long productId, StockMovementType type,
            Instant dateFrom, Instant dateTo) {
        Specification<StockMovement> specification = Specification.where(null);

        if (productId != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("product").get("id"), productId));
        }
        if (type != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("type"), type));
        }
        if (dateFrom != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("occurredAt"), dateFrom));
        }
        if (dateTo != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("occurredAt"), dateTo));
        }

        return stockMovementRepository.findAll(specification, pageable);
    }
}
