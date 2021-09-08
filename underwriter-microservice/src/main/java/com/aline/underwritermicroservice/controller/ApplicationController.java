package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.dto.response.ApplyResponse;
import com.aline.core.model.Application;
import com.aline.underwritermicroservice.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Applications")
@RequestMapping("/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    @Value("${server.port}")
    private int port;

    private final ApplicationService service;

    /**
     * Retrieve an application by its ID.
     * @param id The id of the application to be retrieved.
     * @return ResponseEntity of an ApplicationResponse.
     */
    @Operation(description = "Get an application by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application was found."),
            @ApiResponse(responseCode = "404", description = "Application does not exist.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getApplicationResponseById(id));
    }

    @Operation(description = "Get a paginated response of all applicants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated applications. The content array may be empty which means no applications exist.")
    })
    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> getAllApplications(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
                    Pageable pageable,
            @RequestParam(defaultValue = "") String search) {
        Page<ApplicationResponse> page = service.getAllApplications(pageable, search);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(page);
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
    @Operation(description = "Apply for a membership.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Application was successfully created and either approved, denied, or set to pending."),
            @ApiResponse(responseCode = "404", description = "Creating an application with existing applicants and one or more of the existing applicants do not exist."),
            @ApiResponse(responseCode = "409", description = "There was a conflict with creating one or more of the applicants. There is a conflict with the specified unique columns."),
            @ApiResponse(responseCode = "400", description = "Application could not be processed for some reason."),
            @ApiResponse(responseCode = "502", description = "The application notification email was not sent.")
    })
    @PostMapping
    public ResponseEntity<ApplyResponse> apply(@RequestBody @Valid ApplyRequest request) {
        ApplyResponse applyResponse = service.applyAndSendEmail(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .port(port)
                .buildAndExpand(applyResponse.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(applyResponse);
    }

}
