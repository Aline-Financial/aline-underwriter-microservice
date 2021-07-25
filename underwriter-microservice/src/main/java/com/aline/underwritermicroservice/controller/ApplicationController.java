package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.model.Application;
import com.aline.underwritermicroservice.service.ApplicationService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Application Controller
 * <p>
 *     CRUD endpoints for {@link Application} entity.
 * </p>
 */
@RestController
@Api("/applications")
@RequestMapping("/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getApplicationById(id));
    }

}
