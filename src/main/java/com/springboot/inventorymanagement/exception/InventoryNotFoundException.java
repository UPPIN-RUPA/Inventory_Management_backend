package com.springboot.inventorymanagement.exception;

public class InventoryNotFoundException extends ResourceNotFoundException {

    public InventoryNotFoundException(String message) {
        super(message);
    }
}
