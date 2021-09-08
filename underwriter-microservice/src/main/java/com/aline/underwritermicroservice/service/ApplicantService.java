package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.request.UpdateApplicant;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.dto.response.PaginatedResponse;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.EmailConflictException;
import com.aline.core.exception.conflict.PhoneConflictException;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import com.aline.core.security.annotation.RoleIsManagement;
import com.aline.core.util.SimpleSearchSpecification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * Applicant Service
 * <p>Service methods for manipulating {@link Applicant} entities.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "Applicant Service")
public class ApplicantService {

    private final ApplicantRepository repository;

    private ModelMapper mapper;

    private ModelMapper skipNullMapper;

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setSkipNullMapper(@Qualifier("skipNullModelMapper") ModelMapper mapper) {
        this.skipNullMapper = mapper;
    }

    /**
     * Creates an applicant entity with validation.
     * <p>
     *     Entity must be unique to be saved.
     * </p>
     * <p>
     *     <em>A unique entity contains a unique email, phone number, driver's license, Social Security number.</em>
     * </p>
     * @param createApplicant DTO that contains all of the applicant information.
     * @return Applicant saved by the {@link ApplicantRepository}
     * @throws ConflictException Thrown from <code>validateUniqueness</code> method.
     */
    public ApplicantResponse createApplicant(@Valid CreateApplicant createApplicant) {
        Applicant applicant = mapper.map(createApplicant, Applicant.class);
        validateUniqueness(applicant.getEmail(),
                applicant.getPhone(),
                applicant.getDriversLicense(),
                applicant.getSocialSecurity());
        Applicant saved = repository.save(applicant);
        return mapper.map(saved, ApplicantResponse.class);
    }

    /**
     * Finds an applicant entity by <code>id</code> property.
     * @param id ID of the Applicant being queried.
     * @return Applicant with queried ID.
     * @throws ApplicantNotFoundException If applicant with the queried ID does not exist.
     */
    @PreAuthorize("@applicantAuth.canAccess(#id)")
    public ApplicantResponse getApplicantById(long id) {
        Applicant found = repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        return mapper.map(found, ApplicantResponse.class);
    }

    /**
     * Update applicant entity with specified ID and new values.
     * <p>The values are validated while they are also nullable.</p>
     * @param id ID of the applicant to be updated.
     * @param newValues The new values to modify the applicant information with.
     *
     */
    @PreAuthorize("@applicantAuth.canAccess(#id)")
    public void updateApplicant(long id, @Valid UpdateApplicant newValues) {
        validateUniqueness(newValues.getEmail(),
                newValues.getPhone(),
                newValues.getDriversLicense(),
                newValues.getSocialSecurity());
        Applicant toUpdate = repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        skipNullMapper.map(newValues, toUpdate);
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
    @PreAuthorize("hasAnyAuthority(@roles.management)")
    public void deleteApplicant(long id) {
        Applicant toDelete = repository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        repository.delete(toDelete);
    }


    /**
     * Get paginated applicant response list.
     * @param pageable Pageable object passed from controller.
     * @param search Search term if any. (Must be at least an empty string)
     * @return PaginatedResponse of Applicants.
     */
    @RoleIsManagement
    public PaginatedResponse<ApplicantResponse> getApplicants(@NonNull final Pageable pageable, @NonNull final String search) {
        SimpleSearchSpecification<Applicant> spec = new SimpleSearchSpecification<>(search);
        Page<ApplicantResponse> responsePage = repository.findAll(spec, pageable)
                .map(applicant -> mapper.map(applicant, ApplicantResponse.class));
        return new PaginatedResponse<>(responsePage.getContent(), pageable, responsePage.getTotalElements());
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
