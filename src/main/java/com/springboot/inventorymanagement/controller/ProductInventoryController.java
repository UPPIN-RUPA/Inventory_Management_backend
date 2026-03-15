package com.springboot.inventorymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.inventorymanagement.dto.CreateInventoryRequest;
import com.springboot.inventorymanagement.dto.InventoryResponse;
import com.springboot.inventorymanagement.dto.PaginatedResponse;
import com.springboot.inventorymanagement.dto.RestockInventoryRequest;
import com.springboot.inventorymanagement.config.OpenApiExamples;
import com.springboot.inventorymanagement.exception.ApiError;
import com.springboot.inventorymanagement.exception.InventoryMutationNotAllowedException;
import com.springboot.inventorymanagement.mapper.InventoryMapper;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductInventory;
import com.springboot.inventorymanagement.service.ProductService;
import com.springboot.inventorymanagement.service.ProductInventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventories")
@Tag(name = "Inventories", description = "Manage inventory records for products")
public class ProductInventoryController {

    @Autowired
    private ProductInventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryMapper inventoryMapper;

    // Get all product inventories
    @GetMapping
    @Operation(summary = "List all inventory records", description = "Returns all inventory records. Public endpoint. Defaults: page=0, size=20, sort=id,asc. Maximum size is 100.")
    public PaginatedResponse<InventoryResponse> getAllInventories(
            @Parameter(description = "Filter by product id")
            @RequestParam(required = false) Long productId,
            Pageable pageable) {
        Pageable normalizedPageable = PaginationUtils.normalize(pageable, Sort.by(Sort.Direction.ASC, "id"));
        Page<InventoryResponse> responsePage = inventoryService.getAllInventories(normalizedPageable, productId)
                .map(inventoryMapper::toResponse);
        return PaginatedResponse.from(responsePage);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "List low-stock inventory records",
            description = "Returns inventory records where items left are less than or equal to the threshold. Public endpoint. Defaults: page=0, size=20, sort=itemsLeft,asc. Maximum size is 100.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Invalid threshold", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR)))
    })
    public PaginatedResponse<InventoryResponse> getLowStockInventories(
            @Parameter(description = "Low-stock threshold. Defaults to 10.")
            @RequestParam(defaultValue = "10") int threshold,
            Pageable pageable) {
        Pageable normalizedPageable = PaginationUtils.normalize(pageable, Sort.by(Sort.Direction.ASC, "itemsLeft"));
        Page<InventoryResponse> responsePage = inventoryService.getLowStockInventories(normalizedPageable, threshold)
                .map(inventoryMapper::toResponse);
        return PaginatedResponse.from(responsePage);
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "List out-of-stock inventory records",
            description = "Returns inventory records where items left are exactly zero. Public endpoint. Defaults: page=0, size=20, sort=id,asc. Maximum size is 100.")
    public PaginatedResponse<InventoryResponse> getOutOfStockInventories(Pageable pageable) {
        Pageable normalizedPageable = PaginationUtils.normalize(pageable, Sort.by(Sort.Direction.ASC, "id"));
        Page<InventoryResponse> responsePage = inventoryService.getOutOfStockInventories(normalizedPageable)
                .map(inventoryMapper::toResponse);
        return PaginatedResponse.from(responsePage);
    }

    // Get product inventory by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get an inventory record by id", description = "Returns a single inventory record. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)))
    })
    public InventoryResponse getInventoryById(@PathVariable Long id) {
        return inventoryMapper.toResponse(inventoryService.getInventoryById(id));
    }

    // Create a new product inventory
    @PostMapping
    @Operation(summary = "Create an inventory record", description = "Creates a new inventory record. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "409", description = "Duplicate inventory", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.CONFLICT_ERROR)))
    })
    public InventoryResponse createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        Product product = productService.getProductById(request.getProductId());
        ProductInventory inventory = inventoryMapper.toEntity(request, product);
        return inventoryMapper.toResponse(inventoryService.createInventory(inventory));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @Operation(summary = "Inventory replacement is not allowed", description = "Inventory records cannot be generically replaced. Use restock to increase stock.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "405", description = "Inventory mutation not allowed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.METHOD_NOT_ALLOWED_ERROR)))
    })
    public ResponseEntity<Void> rejectInventoryUpdate(@PathVariable Long id) {
        throw new InventoryMutationNotAllowedException(
                "Inventory records cannot be generically updated. Use restock operations for stock changes on inventory id "
                        + id);
    }

    @PostMapping("/{productId}/restock")
    @Operation(summary = "Restock inventory", description = "Adds stock to an existing inventory record for a product. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)))
    })
    public InventoryResponse restockInventory(@PathVariable Long productId,
            @Valid @RequestBody RestockInventoryRequest request) {
        return inventoryMapper.toResponse(
                inventoryService.restockInventory(productId, request.getQuantity(), request.getReason()));
    }

    // Delete product inventory by ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an inventory record", description = "Deletes an inventory record by id. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
