package com.springboot.inventorymanagement.repository;



import com.springboot.inventorymanagement.model.ProductSales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSalesRepository extends JpaRepository<ProductSales, Long>, JpaSpecificationExecutor<ProductSales> {
	
	List<ProductSales> findBySaleDate(LocalDate saleDate);
}
