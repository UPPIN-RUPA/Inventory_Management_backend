package com.springboot.inventorymanagement.model;


import jakarta.persistence.*;

@Entity
public class ProductInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String category;
    private int itemsSold;
    private int itemsLeft;

    // Default constructor
    public ProductInventory() {
    }

    // Parameterized constructor
    public ProductInventory(Product product, String category, int itemsSold, int itemsLeft) {
        this.product = product;
        this.category = category;
        this.itemsSold = itemsSold;
        this.itemsLeft = itemsLeft;
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

    public int getItemsSold() {
        return itemsSold;
    }

    public void setItemsSold(int itemsSold) {
        this.itemsSold = itemsSold;
    }

    public int getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(int itemsLeft) {
        this.itemsLeft = itemsLeft;
    }
}
