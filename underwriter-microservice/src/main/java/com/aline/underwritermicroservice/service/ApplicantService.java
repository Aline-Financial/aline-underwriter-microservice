package com.aline.underwritermicroservice.service;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final ApplicantRepository repository;
    private final ModelMapper mapper;

    public Applicant createApplicant(CreateApplicantDTO createApplicantDTO) {
        Applicant applicant = mapper.map(createApplicantDTO, Applicant.class);
        return repository.save(applicant);
    }

}
