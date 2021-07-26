package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.ApplicantConflictException;
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
    private ApplicantService applicantService;

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
    @Transactional(rollbackOn = ApplicantConflictException.class)
    public ApplicationResponse apply(ApplyRequest request) {

        // Create all applicants
        // If not all applicants can be created throw an error.
        try {
            request.getApplicants().forEach(applicantService::createApplicant);
        } catch (ConflictException e) {
            throw new ApplicantConflictException();
        }

        // If all applicants are created
        // Create an application with the request type
        mapper.map(request, Application.class);

        // Insert the primary applicant by querying PII
        // Attach the created applicants to the application
        // Persist the application to the database

        // NOTE: This will all be transactional.
        // Rollback on any possible exceptions.

        return null;
     }

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

}
