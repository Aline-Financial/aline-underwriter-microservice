package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.model.Applicant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/applicants")
@Slf4j(topic = "Applicants")
public class ApplicantController {

    @ApiOperation("Create an Applicant")
    @PostMapping
    public ResponseEntity<Applicant> createApplicant(@RequestBody @Valid CreateApplicantDTO applicant) {
        return ResponseEntity.ok().build();
    }

}
