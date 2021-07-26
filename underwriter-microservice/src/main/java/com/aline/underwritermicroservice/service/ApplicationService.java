package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.NotFoundException;
import com.aline.core.exception.conflict.ApplicantConflictException;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.core.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.LinkedHashSet;
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
    private final UnderwriterService underwriterService;
    private final ApplicationRepository repository;

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

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
     * Create new application with all brand new applicants or all existing applicants.
     * @param request ApplyRequest dto with request information.
     * @return ApplicationResponse containing the newly created applicants and the application status.
     * @apiNote This method will create all of the CreateApplicant dto objects within
     * the applicants property first. If the applicants cannot be created for any reason, the process
     * will stop and throw an error. However, if the ApplyRequest is flagged with <code>noApplicants</code>
     * then it will use a list of ids of existing applicants and it will create an application with those
     * existing applicants instead. This will allow for a front end to create applicants first to verify
     * correctness and then apply.
     */
    @Transactional(rollbackOn = {
            ApplicantConflictException.class,
            NotFoundException.class
    })
    public ApplicationResponse apply(@Valid ApplyRequest request) {

        Application application;

        if (request.getNoApplicants() == null || !request.getNoApplicants()) {

            LinkedHashSet<Applicant> applicants = createApplicants(request.getApplicants());
            Applicant primaryApplicant = applicants.iterator().next(); // First applicant is the primary

            application = Application.builder()
                    .primaryApplicant(primaryApplicant)
                    .applicants(applicants)
                    .applicationType(request.getApplicationType())
                    .applicationStatus(ApplicationStatus.PENDING)
                    .build();

        } else {

            LinkedHashSet<Applicant> applicants = request.getApplicantIds().stream()
                    .map(applicantService::getApplicantById)
                    .map(applicantResponse -> mapper.map(applicantResponse, Applicant.class))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            Applicant primaryApplicant = applicants.iterator().next();

            application = Application.builder()
                    .applicationType(request.getApplicationType())
                    .applicants(applicants)
                    .primaryApplicant(primaryApplicant)
                    .applicationStatus(ApplicationStatus.PENDING)
                    .build();
        }

        Application savedApplication = repository.save(application);
        ApplicationResponse response = mapper.map(savedApplication, ApplicationResponse.class);

        underwriterService.underwriteApplication(savedApplication,
                (status, reason) -> {
                    savedApplication.setApplicationStatus(status);
                    response.setStatus(status.name());
                    response.setReason(reason);
                });

        return response;

    }

    /**
     * Create Applicants from a list of applicants
     * @param createApplicants LinkedHashSet of applicants.
     * @return LinkedHashSet of saved applicants.
     */
    private LinkedHashSet<Applicant> createApplicants(Set<CreateApplicant> createApplicants) {
        return createApplicants.stream()
                .map(applicantService::createApplicant)
                .map(applicantResponse -> mapper.map(applicantResponse, Applicant.class))
                .collect(Collectors.toCollection(LinkedHashSet<Applicant>::new));
    }

}
