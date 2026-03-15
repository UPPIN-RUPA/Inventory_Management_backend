package com.springboot.inventorymanagement.exception;

public class SalesMutationNotAllowedException extends RuntimeException {

    public SalesMutationNotAllowedException(String message) {
        super(message);
    }
}
