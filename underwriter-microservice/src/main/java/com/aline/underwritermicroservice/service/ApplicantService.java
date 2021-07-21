package com.aline.underwritermicroservice.service;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.dto.UpdateApplicantDTO;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.EmailConflictException;
import com.aline.core.exception.conflict.PhoneConflictException;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
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

    /**
     * Creates an applicant entity with validation.
     * <p>
     *     Entity must be unique to be saved.
     * </p>
     * <p>
     *     <em>A unique entity contains a unique email, phone number, driver's license, Social Security number.</em>
     * </p>
     * @param createApplicantDTO DTO that contains all of the applicant information.
     * @return Applicant saved by the {@link ApplicantRepository}
     */
    public Applicant createApplicant(@Valid CreateApplicantDTO createApplicantDTO) {
        ModelMapper mapper = new ModelMapper();
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

    /**
     * Finds an applicant entity by <code>id</code> property.
     * @param id ID of the Applicant being queried.
     * @return Applicant with queried ID.
     * @throws ApplicantNotFoundException If applicant with the queried ID does not exist.
     */
    public Applicant getApplicantById(long id) {
        return repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
    }

    /**
     * Update applicant entity with specified ID and new values.
     * <p>The values are validated while they are also nullable.</p>
     * @param id ID of the applicant to be updated.
     * @param newValues The new values to modify the applicant information with.
     * @throws ApplicantNotFoundException If applicant with the queried ID does not exist.
     */
    public void updateApplicant(long id, @Valid UpdateApplicantDTO newValues) {
        Applicant toUpdate = repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        ModelMapper mapper = new ModelMapper();
        // Allows for null values in the DTO to not affect the entity
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(newValues, toUpdate);
        repository.save(toUpdate);
    }

}
