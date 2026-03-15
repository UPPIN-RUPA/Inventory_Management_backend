package com.springboot.inventorymanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Basic service health check")
public class HealthController {

    @GetMapping
    @Operation(summary = "Get service health", description = "Returns a lightweight health response for uptime checks.")
    public Map<String, String> getHealth() {
        return Map.of("status", "ok");
    }
}
