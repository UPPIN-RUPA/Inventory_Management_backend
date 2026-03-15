package com.springboot.inventorymanagement.model;


import jakarta.persistence.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = "product_id"),
        indexes = {
                @Index(name = "idx_inventory_product_id", columnList = "product_id"),
                @Index(name = "idx_inventory_items_left", columnList = "items_left")
        })
public class ProductInventory extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int itemsSold;
    private int itemsLeft;

    // Default constructor
    public ProductInventory() {
    }

    // Parameterized constructor
    public ProductInventory(Product product, int itemsSold, int itemsLeft) {
        this.product = product;
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
