package com.springboot.inventorymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.inventorymanagement.dto.CreateSaleRequest;
import com.springboot.inventorymanagement.dto.PaginatedResponse;
import com.springboot.inventorymanagement.dto.SaleResponse;
import com.springboot.inventorymanagement.config.OpenApiExamples;
import com.springboot.inventorymanagement.exception.ApiError;
import com.springboot.inventorymanagement.mapper.SaleMapper;
import com.springboot.inventorymanagement.exception.SalesMutationNotAllowedException;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.model.ProductSales;
import com.springboot.inventorymanagement.service.ProductService;
import com.springboot.inventorymanagement.service.ProductSalesService;

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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "Manage product sales records")
public class ProductSalesController {

    @Autowired
    private ProductSalesService salesService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SaleMapper saleMapper;

    // Get all sales
    @GetMapping
    @Operation(summary = "List all sales", description = "Returns all sales records. Public endpoint. Defaults: page=0, size=20, sort=saleDate,desc. Maximum size is 100.")
    public PaginatedResponse<SaleResponse> getAllSales(
            @Parameter(description = "Filter by product id")
            @RequestParam(required = false) Long productId,
            @Parameter(description = "Filter by sale date from (inclusive), format yyyy-MM-dd")
            @RequestParam(required = false) LocalDate saleDateFrom,
            @Parameter(description = "Filter by sale date to (inclusive), format yyyy-MM-dd")
            @RequestParam(required = false) LocalDate saleDateTo,
            Pageable pageable) {
        Pageable normalizedPageable = PaginationUtils.normalize(pageable, Sort.by(Sort.Direction.DESC, "saleDate"));
        Page<SaleResponse> responsePage = salesService.getAllSales(normalizedPageable, productId, saleDateFrom, saleDateTo)
                .map(saleMapper::toResponse);
        return PaginatedResponse.from(responsePage);
    }

    // Get a sales record by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get a sales record by id", description = "Returns a single sales record. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Sale not found", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)))
    })
    public ResponseEntity<SaleResponse> getSalesById(@PathVariable Long id) {
        ProductSales sales = salesService.getSalesById(id);
        return ResponseEntity.ok(saleMapper.toResponse(sales));
    }

    // Create a new sales record
    @PostMapping
    @Operation(summary = "Create a sales record", description = "Creates a new sales record. Requires HTTP Basic authentication.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "404", description = "Inventory or product not found", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR))),
            @ApiResponse(responseCode = "409", description = "Insufficient stock", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.CONFLICT_ERROR)))
    })
    public ResponseEntity<SaleResponse> createSales(@Valid @RequestBody CreateSaleRequest request) {
        Product product = productService.getProductById(request.getProductId());
        ProductSales sales = saleMapper.toEntity(request, product);
        ProductSales createdSales = salesService.createSales(sales);
        return new ResponseEntity<>(saleMapper.toResponse(createdSales), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}", method = { RequestMethod.PUT, RequestMethod.DELETE })
    @Operation(summary = "Sales mutations are not allowed", description = "Sales are immutable after creation. Use explicit future domain actions such as cancel or return instead.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "405", description = "Sale mutation not allowed", content = @Content(schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(value = OpenApiExamples.METHOD_NOT_ALLOWED_ERROR)))
    })
    public ResponseEntity<Void> rejectSalesMutation(@PathVariable Long id) {
        throw new SalesMutationNotAllowedException(
                "Sales records are immutable. Update and delete operations are not supported for sale id " + id);
    }
}
