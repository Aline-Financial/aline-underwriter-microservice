package com.aline.underwritermicroservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller
 * <p>Controller endpoints for root location.</p>
 */
@RestController
@Tag(name = "Root Controller")
public class RootController {
    @GetMapping("/health")
    @Operation(description = "Health check endpoint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service is healthy.") ,
            @ApiResponse(responseCode = "404", description = "The service is probably not running.")
    })
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
