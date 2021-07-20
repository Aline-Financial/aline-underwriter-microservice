package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.model.Applicant;
import com.aline.underwritermicroservice.service.ApplicantService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/applicants")
@RequiredArgsConstructor
@Slf4j(topic = "Applicants")
public class ApplicantController {

    private final ApplicantService service;

    @ApiOperation("Create an Applicant")
    @PostMapping
    public ResponseEntity<Applicant> createApplicant(@RequestBody @Valid CreateApplicantDTO createApplicantDTO) {
        Applicant applicant = service.createApplicant(createApplicantDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(applicant.getId())
                .toUri();
        return ResponseEntity.created(location).body(applicant);
    }

}
