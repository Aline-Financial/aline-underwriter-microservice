package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.model.Application;
import com.aline.underwritermicroservice.service.ApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

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

    @Value("${server.port}")
    private int PORT;

    private final ApplicationService service;

    /**
     * Retrieve an application by it's ID.
     * @param id The id of the application to be retrieved.
     * @return ResponseEntity of an ApplicationResponse.
     */
    @ApiOperation("Get an application by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Application was found."),
            @ApiResponse(code = 404, message = "Application does not exist.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getApplicationById(id));
    }

    /**
     * Apply for a membership with this endpoint.
     * <p>
     *     The apply request object allows for creation of accounts with
     *     either existing applicants or new applicants. These are flags
     *     withing the ApplyRequest dto object.
     * </p>
     * @param request The apply request to dto.
     * @return ResponseEntity of ApplicationResponse with information such as
     * if the accounts and members were created or if there was a reason for them not
     * being created.
     * @see ApplyRequest
     */
    @ApiOperation("Apply for a membership.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Application was successfully created and either approved, denied, or set to pending."),
            @ApiResponse(code = 404, message = "Creating an application with existing applicants and one or more of the existing applicants do not exist."),
            @ApiResponse(code = 409, message = "There was a conflict with creating one or more of the applicants. There is a conflict with the specified unique columns."),
            @ApiResponse(code = 400, message = "Application could not be processed for some reason.")
    })
    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(@RequestBody @Valid ApplyRequest request) {
        ApplicationResponse response = service.apply(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .port(PORT)
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

}
