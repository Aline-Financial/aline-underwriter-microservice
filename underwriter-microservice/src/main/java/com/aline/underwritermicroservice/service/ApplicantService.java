package com.aline.underwritermicroservice.service;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.EmailConflictException;
import com.aline.core.exception.conflict.PhoneConflictException;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final ApplicantRepository repository;
    private final ModelMapper mapper;

    public Applicant createApplicant(@Valid CreateApplicantDTO createApplicantDTO) {
        Applicant applicant = mapper.map(createApplicantDTO, Applicant.class);
        if (repository.existsByEmail(applicant.getEmail())) {
            throw new EmailConflictException();
        }
        if (repository.existsByPhone(applicant.getPhone())) {
            throw new PhoneConflictException();
        }
        if (repository.existsByDriversLicense(applicant.getDriversLicense())) {
            throw new ConflictException("Driver's license already exists.");
        }
        if (repository.existsBySocialSecurity(applicant.getSocialSecurity())) {
            throw new ConflictException("Social Security number already exists.");
        }
        return repository.save(applicant);
    }

}
