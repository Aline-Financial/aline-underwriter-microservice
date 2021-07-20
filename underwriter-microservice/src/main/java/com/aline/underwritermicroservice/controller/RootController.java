package com.aline.underwritermicroservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    /**
     * <strong>Health Check endpoint</strong>
     * <p>Endpoint: <code>/health</code></p>
     * <p>Status <code>200 OK</code> if the application is running.</p>
     *
     * @return {@link ResponseEntity<Void>} No content.
     */
    @GetMapping("/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }

}
