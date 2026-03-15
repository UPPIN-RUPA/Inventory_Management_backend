package com.springboot.inventorymanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import com.springboot.inventorymanagement.config.SecurityConfig;
import com.springboot.inventorymanagement.exception.GlobalExceptionHandler;
import com.springboot.inventorymanagement.exception.ResourceNotFoundException;
import com.springboot.inventorymanagement.mapper.ProductMapper;
import com.springboot.inventorymanagement.model.Product;
import com.springboot.inventorymanagement.service.ProductService;

@WebMvcTest(ProductController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class, ProductMapper.class })
@TestPropertySource(properties = {
        "app.security.username=tester",
        "app.security.password=test-pass"
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void getProductsUsesDefaultPaginationAndSort() throws Exception {
        Product product = new Product("Phone", "Electronics", 599.0);
        product.setId(1L);

        when(productService.getAllProducts(PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id")), null, null))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id")), 1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    void getProductsRejectsPageSizeAboveMaximum() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must not be greater than 100"));
    }

    @Test
    void getProductsRejectsNegativePage() throws Exception {
        mockMvc.perform(get("/api/products").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page number must be zero or greater"));
    }

    @Test
    void getProductByIdReturns404WhenMissing() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id 99"));
    }

    @Test
    void postProductRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Phone\",\"category\":\"Electronics\",\"price\":599.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postProductAllowsAuthenticatedUser() throws Exception {
        Product saved = new Product("Phone", "Electronics", 599.0);
        when(productService.saveProduct(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Phone\",\"category\":\"Electronics\",\"price\":599.0}"))
                .andExpect(status().isOk());
    }

    @Test
    void postProductReturns400ForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/products")
                .with(httpBasic("tester", "test-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"category\":\"\",\"price\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Name is required"))
                .andExpect(jsonPath("$.fieldErrors.category").value("Category is required"))
                .andExpect(jsonPath("$.fieldErrors.price").value("Price must be greater than zero"));
    }

    @Test
    void updateProductRequiresAuthentication() throws Exception {
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Phone\",\"category\":\"Electronics\",\"price\":599.0}"))
                .andExpect(status().isUnauthorized());
    }
}
