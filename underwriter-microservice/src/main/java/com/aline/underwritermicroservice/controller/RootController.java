package com.aline.underwritermicroservice.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller
 * <p>Controller endpoints for root location.</p>
 */
@RestController
public class RootController {

    @GetMapping("/health")
    @ApiOperation("Health check endpoint")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Service is healthy.") ,
            @ApiResponse(code = 404, message = "The service is probably not running.")
    })
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }

}
