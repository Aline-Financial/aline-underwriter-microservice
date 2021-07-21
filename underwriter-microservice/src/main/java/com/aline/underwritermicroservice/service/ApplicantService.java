package com.aline.underwritermicroservice.service;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.dto.UpdateApplicantDTO;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.EmailConflictException;
import com.aline.core.exception.conflict.PhoneConflictException;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * Applicant Service
 * <p>Service methods for manipulating {@link Applicant} entities.</p>
 */
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
     * @throws ConflictException Thrown from <code>validateUniqueness</code> method.
     */
    public Applicant createApplicant(@Valid CreateApplicantDTO createApplicantDTO) {
        ModelMapper mapper = new ModelMapper();
        Applicant applicant = mapper.map(createApplicantDTO, Applicant.class);
        validateUniqueness(applicant.getEmail(),
                applicant.getPhone(),
                applicant.getDriversLicense(),
                applicant.getSocialSecurity());
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
     *
     */
    public void updateApplicant(long id, @Valid UpdateApplicantDTO newValues) {
        validateUniqueness(newValues.getEmail(),
                newValues.getPhone(),
                newValues.getDriversLicense(),
                newValues.getSocialSecurity());
        Applicant toUpdate = repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        ModelMapper mapper = new ModelMapper();
        // Allows for null values in the DTO to not affect the entity
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(newValues, toUpdate);
        repository.save(toUpdate);
    }

    /**
     * Delete applicant entity with specified ID.
     * <p>
     *    <em>Deletes the applicant if the applicant exists.</em>
     * </p>
     * @param id ID of the applicant to be deleted.
     * @throws ApplicantNotFoundException If applicant with the queried ID does not exist.
     */
    public void deleteApplicant(long id) {
        Applicant toDelete = repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        repository.delete(toDelete);
    }

    /**
     * Validate the uniqueness of an applicant.
     * <p>
     *     Use when saving or updating an applicant.
     * </p>
     * @param email Email string to be checked.
     * @param phone Phone string to be checked.
     * @param driversLicense Driver's license to be checked.
     * @param socialSecurity Social Security number to be checked.
     * @throws EmailConflictException If an {@link Applicant} with email already exists.
     * @throws PhoneConflictException If an {@link Applicant} with phone already exists.
     * @throws ConflictException If driver's license or Social Security already exists.
     */
    private void validateUniqueness(
            String email,
            String phone,
            String driversLicense,
            String socialSecurity) {
        if (repository.existsByEmail(email) && email != null)
            throw new EmailConflictException();
        if (repository.existsByPhone(phone) && phone != null)
            throw new PhoneConflictException();
        if (repository.existsByDriversLicense(driversLicense) && driversLicense != null)
            throw new ConflictException("Driver's license already exists.");
        if (repository.existsBySocialSecurity(socialSecurity) && socialSecurity != null)
            throw new ConflictException("Social Security number already exists.");
    }

}
