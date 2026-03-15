package com.springboot.inventorymanagement.exception;

public class DuplicateInventoryException extends RuntimeException {

    public DuplicateInventoryException(String message) {
        super(message);
    }
}
