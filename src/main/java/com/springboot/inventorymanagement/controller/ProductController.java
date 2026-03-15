package com.springboot.inventorymanagement.controller;

import com.springboot.inventorymanagement.dto.CreateProductRequest;
import com.springboot.inventorymanagement.dto.PaginatedResponse;
import com.springboot.inventorymanagement.dto.ProductResponse;
import com.springboot.inventorymanagement.dto.UpdateProductRequest;
import com.springboot.inventorymanagement.config.OpenApiExamples;
import com.springboot.inventorymanagement.exception.ApiError;
import com.springboot.inventorymanagement.mapper.ProductMapper;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.service.ProductService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Manage products in the inventory system")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "List all products", description = "Returns all products. Public endpoint. Defaults: page=0, size=20, sort=id,asc. Maximum size is 100.")
    public PaginatedResponse<ProductResponse> getAllProducts(
            @Parameter(description = "Filter by product name")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by exact category")
            @RequestParam(required = false) String category,
            Pageable pageable) {
        Pageable normalizedPageable = PaginationUtils.normalize(pageable, Sort.by(Sort.Direction.ASC, "id"));
        Page<ProductResponse> responsePage = productService.getAllProducts(normalizedPageable, name, category)
                .map(productMapper::toResponse);
        return PaginatedResponse.from(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id", description = "Returns a single product by id. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)))
    })
    public ProductResponse getProductById(@PathVariable Long id) {
        return productMapper.toResponse(productService.getProductById(id));
    }

    @PostMapping
    @Operation(summary = "Create a product", description = "Creates a new product. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR)))
    })
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productService.saveProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Performs a full update of a product. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)))
    })
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by id. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
