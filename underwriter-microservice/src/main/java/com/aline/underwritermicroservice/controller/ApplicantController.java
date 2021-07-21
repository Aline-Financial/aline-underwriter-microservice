package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.model.Applicant;
import com.aline.underwritermicroservice.service.ApplicantService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Applicant Controller
 * <p>CRUD endpoints for {@link Applicant} entity.</p>
 */
@RestController
@RequestMapping("/applicants")
@RequiredArgsConstructor
@Slf4j(topic = "Applicants")
public class ApplicantController {

    private final ApplicantService service;

    /**
     * Create Applicant
     * <p>
     *     <code>POST</code> mapping for <code>/applicants</code> endpoint.
     * </p>
     * @param createApplicantDTO DTO that holds applicant information.
     * @return ResponseEntity with location to the created resource.
     */
    @ApiOperation("Create an Applicant")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Applicant successfully created."),
            @ApiResponse(code = 404, message = "Applicant data is not valid. (Bad request)"),
            @ApiResponse(code = 409, message = "Applicant data conflicts with other applicant data.")
    })
    @PostMapping
    public ResponseEntity<Applicant> createApplicant(@RequestBody @Valid CreateApplicantDTO createApplicantDTO) {
        Applicant applicant = service.createApplicant(createApplicantDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
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
    @ApiOperation("Get an Applicant By ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Applicant was found."),
            @ApiResponse(code = 404, message = "Applicant does not exist.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Applicant> getApplicantById(@PathVariable long id) {
        Applicant applicant = service.getApplicantById(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicant);
    }

}
