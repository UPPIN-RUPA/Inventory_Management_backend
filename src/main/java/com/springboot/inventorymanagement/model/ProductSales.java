package com.springboot.inventorymanagement.model;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ProductSales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String category;
    private int quantitySold;
    private double priceAtSale;
    private LocalDate saleDate;

    // Default constructor
    public ProductSales() {
    }

    // Parameterized constructor
    public ProductSales(Product product, String category, int quantitySold, double priceAtSale, LocalDate saleDate) {
        this.product = product;
        this.category = category;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
        this.saleDate = saleDate;
    }

    // Getter and Setter methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
