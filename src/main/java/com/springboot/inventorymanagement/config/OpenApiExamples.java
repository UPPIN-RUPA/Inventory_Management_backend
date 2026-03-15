package com.springboot.inventorymanagement.config;

public final class OpenApiExamples {
    private OpenApiExamples() {
    }

    public static final String VALIDATION_ERROR = """
            {
              "timestamp": "2026-03-14T12:00:00Z",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "path": "/api/products",
              "fieldErrors": {
                "name": "Name is required"
              }
            }
            """;

    public static final String NOT_FOUND_ERROR = """
            {
              "timestamp": "2026-03-14T12:00:00Z",
              "status": 404,
              "error": "Not Found",
              "message": "Product not found with id 99",
              "path": "/api/products/99",
              "fieldErrors": null
            }
            """;

    public static final String METHOD_NOT_ALLOWED_ERROR = """
            {
              "timestamp": "2026-03-14T12:00:00Z",
              "status": 405,
              "error": "Method Not Allowed",
              "message": "Sales records are immutable. Update and delete operations are not supported for sale id 1",
              "path": "/api/sales/1",
              "fieldErrors": null
            }
            """;

    public static final String CONFLICT_ERROR = """
            {
              "timestamp": "2026-03-14T12:00:00Z",
              "status": 409,
              "error": "Conflict",
              "message": "Insufficient stock for product id 1. Available stock: 1",
              "path": "/api/sales",
              "fieldErrors": null
            }
            """;
}
