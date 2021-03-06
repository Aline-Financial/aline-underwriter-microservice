package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.request.UpdateApplicant;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.model.Applicant;
import com.aline.underwritermicroservice.service.ApplicantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

/**
 * Applicant Controller
 * <p>CRUD endpoints for {@link Applicant} entity.</p>
 */
@Api(value = "/applicants")
@RestController
@RequestMapping("/applicants")
@RequiredArgsConstructor
@Slf4j(topic = "Applicants")
public class ApplicantController {

    @Value("${server.port}")
    private int port;

    private final ApplicantService service;

    /**
     * Create Applicant
     * <p>
     *     <code>POST</code> mapping for <code>/applicants</code> endpoint.
     * </p>
     * @param createApplicant DTO that holds applicant information.
     * @return ResponseEntity with location to the created resource.
     * @apiNote Exceptions will be caught by the GlobalExceptionHandler
     */
    @ApiOperation("Create an Applicant")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Applicant successfully created."),
            @ApiResponse(code = 404, message = "Applicant data is not valid. (Bad request)"),
            @ApiResponse(code = 409, message = "Applicant data conflicts with other applicant data.")
    })
    @PostMapping
    public ResponseEntity<ApplicantResponse> createApplicant(@RequestBody @Valid CreateApplicant createApplicant) {
        ApplicantResponse applicant = service.createApplicant(createApplicant);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .port(port)
                .buildAndExpand(applicant.getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicant);
    }

    /**
     * Get Applicant by ID
     * <p>
     *     <code>GET</code> mapping for <code>/applicants</code> endpoint.
     * </p>
     * @param id Long representing the applicant's ID.
     * @return ResponseEntity of the queried applicant if one exists.
     * @apiNote Exceptions will be caught by the GlobalExceptionHandler
     */
    @ApiOperation("Get Applicant by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Applicant was found."),
            @ApiResponse(code = 404, message = "Applicant does not exist.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApplicantResponse> getApplicantById(@PathVariable long id) {
        ApplicantResponse applicant = service.getApplicantById(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicant);
    }

    /**
     * Update applicant by ID
     * @param id The ID of the applicant to update.
     * @param newValues The new values to update the applicant with.
     * @return ResponseEntity with status <code>204 NO CONTENT</code> if update was successful.
     * @apiNote Exceptions will be caught by the GlobalExceptionHandler
     */
    @ApiOperation("Update an Applicant by ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Applicant was successfully updated."),
            @ApiResponse(code = 400, message = "New values were not valid."),
            @ApiResponse(code = 404, message = "Applicant to update was not found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApplicant(@PathVariable long id, @RequestBody @Valid UpdateApplicant newValues) {
        service.updateApplicant(id, newValues);
        return ResponseEntity
                .noContent()
                .build();
    }


    /**
     * Delete applicant by ID
     * @param id The ID of the applicant to delete.
     * @return ResponseEntity with status <code>204 NO CONTENT</code> if applicant was deleted successfully.
     * @apiNote Exceptions will be caught by the GlobalExceptionHandler
     */
    @ApiOperation("Delete an Applicant by ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Applicant was successfully deleted."),
            @ApiResponse(code = 404, message = "Applicant to delete was not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicant(@PathVariable long id) {
        service.deleteApplicant(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * Paginated {@link ApplicantResponse} entity.
     * <p>
     *     The endpoint returns a paginated object with the content consisting of ApplicantResponse DTOs.
     * </p>
     * @param pageable Pageable object that contains the default params for the query.
     * @return ResponseEntity of type Page with generic ApplicantResponse.
     */
    @ApiOperation("Get all Applicants (Paginated)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Retrieve a populated or empty page of applicants.")
    })
    @GetMapping
    public ResponseEntity<Page<ApplicantResponse>> getApplicants(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
                Pageable pageable,
            @RequestParam(defaultValue = "") String search) {
        Page<ApplicantResponse> applicantResponsePage = service.getApplicants(pageable, search);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicantResponsePage);
    }

}
