package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.NotFoundException;
import com.aline.core.exception.conflict.ApplicantConflictException;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import com.aline.core.model.Application.ApplicationBuilder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Application Service
 * <p>
 *     Service methods for manipulating {@link Application} entities.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private ModelMapper mapper;
    private final ApplicantService applicantService;

    private final ApplicationRepository repository;

    /**
     * Get Application By ID
     * @param id Id of the retrieved application.
     * @return ApplicationResponse DTO
     * @throws ApplicationNotFoundException If application with the provided ID does not exist.
     */
    public ApplicationResponse getApplicationById(long id) {
        Application application = repository.findById(id).orElseThrow(ApplicationNotFoundException::new);
        return mapper.map(application, ApplicationResponse.class);
    }

    /**
     * Delete an application by ID
     * @param id ID of the application to be deleted.
     * @throws ApplicationNotFoundException If application with the provided ID does not exist.
     */
    public void deleteApplication(long id) {
        Application toDelete = repository.findById(id).orElseThrow(ApplicationNotFoundException::new);
        repository.delete(toDelete);
    }

    /**
     * Create new application with all brand new applicants.
     * @param request ApplyRequest dto with all new applicants.
     * @return ApplicationResponse containing the newly created applicants and the application status.
     * @apiNote This method will create all of the CreateApplicant dto objects within
     * the applicants property first. If the applicants cannot be created for any reason, the process
     * will stop and throw an error.
     */
    @Transactional(rollbackOn = {
            ApplicantConflictException.class,
            NotFoundException.class
    })
    public ApplicationResponse apply(ApplyRequest request) {

        Set<Applicant> applicants;
        try {
            applicants = request.getApplicants()
                    .stream().map(createApplicant -> applicantService.createApplicant(createApplicant))
                    .map(applicantResponse -> mapper.map(applicantResponse, Applicant.class))
                    .collect(Collectors.toSet());
        } catch (ConflictException e) {
            throw new ApplicantConflictException();
        }

        Applicant primaryApplicant = applicants.stream()
                .findFirst().orElseThrow(() -> new NotFoundException("Primary applicant not found."));
        ApplicationBuilder builder = Application.builder()
                .applicants(applicants)
                .applicationType(request.getApplicationType())
                .primaryApplicant(primaryApplicant);

        Application savedApplication = repository.save(builder.build());

        return mapper.map(savedApplication, ApplicationResponse.class);
     }

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

}
