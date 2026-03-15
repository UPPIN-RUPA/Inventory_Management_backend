package com.springboot.inventorymanagement.exception;

public class InventoryMutationNotAllowedException extends RuntimeException {

    public InventoryMutationNotAllowedException(String message) {
        super(message);
    }
}
