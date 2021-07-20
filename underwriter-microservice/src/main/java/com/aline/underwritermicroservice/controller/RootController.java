package com.aline.underwritermicroservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    /**
     * Health check endpoint
     * <p><strong>Endpoint: </strong> <code>/health</code></p>
     * <p><code>Status 200 OK</code> if the application is running.</p>
     *
     * @return {@link ResponseEntity<Void>} No content.
     */
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
