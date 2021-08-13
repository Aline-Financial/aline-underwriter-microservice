package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.dto.response.ApplyAccountResponse;
import com.aline.core.dto.response.ApplyMemberResponse;
import com.aline.core.dto.response.ApplyResponse;
import com.aline.core.dto.response.PaginatedResponse;
import com.aline.core.exception.BadRequestException;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.NotFoundException;
import com.aline.core.exception.conflict.ApplicantConflictException;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.core.model.Member;
import com.aline.core.model.account.Account;
import com.aline.core.repository.ApplicationRepository;
import com.aline.core.util.SearchSpecification;
import com.aline.underwritermicroservice.service.function.ApplyResponseConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.DiscriminatorValue;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
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
@Slf4j(topic = "Application Service")
public class ApplicationService {

    private ModelMapper mapper;
    private final ApplicantService applicantService;
    private final UnderwriterService underwriterService;
    private final MemberService memberService;
    private final AccountService accountService;
    private final ApplicationEmailService emailService;
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
     * @param responseConsumer ApplicationResponseConsumer contains logic to run after response is received
     *                         from the underwriting service and before the ApplicationResponse object
     *                         is returned by the method.
     * @return ApplicationResponse containing the newly created applicants and the application status.
     * @apiNote This method will create all of the CreateApplicant dto objects within
     * the applicants property first. If the applicants cannot be created for any reason, the process
     * will stop and throw an error. However, if the ApplyRequest is flagged with <code>noApplicants</code>
     * then it will use a list of ids of existing applicants and it will create an application with those
     * existing applicants instead. This will allow for a front end to create applicants first to verify
     * correctness and then apply.
     */
    @Transactional(rollbackOn = {
            ConflictException.class,
            NotFoundException.class,
            NullPointerException.class
    })
    public ApplyResponse apply(@Valid ApplyRequest request, ApplyResponseConsumer responseConsumer) {

        log.info("Starting new application: {}", request.getApplicationType());

        Application application;

        if (request.getNoApplicants() == null || !request.getNoApplicants()) {

            log.info("Creating application with new applicants.");

            LinkedHashSet<Applicant> applicants = createApplicants(request.getApplicants());
            Applicant primaryApplicant = applicants.iterator().next(); // First applicant is the primary

            application = Application.builder()
                    .primaryApplicant(primaryApplicant)
                    .applicants(applicants)
                    .applicationType(request.getApplicationType())
                    .applicationStatus(ApplicationStatus.PENDING)
                    .build();

        } else {

            Set<Long> applicantIds = Optional.ofNullable(request.getApplicantIds())
                    .orElseThrow(() -> new BadRequestException("Field 'noApplicants' was set to true but no existing applicant ids were provided."));

            if (applicantIds.isEmpty())
                throw new BadRequestException("Field 'noApplicants' was set to true but field 'applicantIds' is empty.");

            log.info("Creating application with existing applicants.");

            LinkedHashSet<Applicant> applicants = applicantIds.stream()
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

        if (application != null) {

            log.info("Create application and application response.");

            Application savedApplication = repository.save(application);
            ApplyResponse response = mapper.map(savedApplication, ApplyResponse.class);

            underwriterService.underwriteApplication(savedApplication,
                    (status, reason) -> {
                        log.info("Received underwriting status: {}\nAnd reason: {}", status, reason);
                        savedApplication.setApplicationStatus(status);
                        response.setStatus(status);
                        response.setReason(reason);

                        if (status == ApplicationStatus.APPROVED) {
                            log.info("Application was approved... Creating members.");
                            LinkedHashSet<Member> members = savedApplication.getApplicants().stream()
                                    .map(memberService::createMember)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                            Member primaryMember = members.iterator().next();

                            log.info("Creating accounts: {}", request.getApplicationType());
                            Set<Account> accounts = accountService.createAccount(savedApplication.getApplicationType(), primaryMember, members);

                            log.info("Attaching members to accounts...");
                            members.forEach(member -> member.setAccounts(accounts));

                            List<Member> savedMembers = memberService.saveAll(members);

                            Set<ApplyAccountResponse> createdAccounts = accounts.stream()
                                            .map(account -> new ApplyAccountResponse(account.getAccountNumber(),
                                                    account.getClass().getAnnotation(DiscriminatorValue.class).value()))
                                            .collect(Collectors.toSet());

                            List<ApplyMemberResponse> createdMembers = savedMembers.stream()
                                            .map(member -> new ApplyMemberResponse(member.getMembershipId(),
                                                    String.format("%s %s",
                                                            member.getApplicant().getFirstName(),
                                                            member.getApplicant().getLastName())))
                                                    .collect(Collectors.toList());

                            response.setAccountsCreated(true);
                            response.setCreatedAccounts(createdAccounts);
                            response.setMembersCreated(true);
                            response.setCreatedMembers(createdMembers);
                        }
                    });

            // Call onRespond if a responseConsumer was provided.
            if (responseConsumer != null)
                responseConsumer.onRespond(response);

            log.info("Accounts and members successfully created.");
            return response;
        }
        throw new BadRequestException("Application could not be processed.");
    }

    /**
     * Get paginated application response list.
     * @param pageable Pageable object passed from controller.
     * @param search Search term if any. (Must be at least an empty string)
     * @return PaginatedResponse of Applications.
     */
    public PaginatedResponse<ApplicationResponse> getAllApplications(@NotNull   Pageable pageable, @NotNull final String search) {
        SearchSpecification<Application> spec = new SearchSpecification<>(search);
        Page<ApplicationResponse> responsePage = repository.findAll(spec, pageable)
                .map(application -> mapper.map(application, ApplicationResponse.class));

        return new PaginatedResponse<>(responsePage.getContent(), pageable, responsePage.getTotalElements());
    }

    /**
     * Send an email based on the application response status.
     * @param request The ApplyRequest DTO
     * @return An ApplyResponse object.
     */
    public ApplyResponse applyAndSendEmail(ApplyRequest request) {
        return apply(request, emailService::sendEmailBasedOnStatus);
    }

    /**
     * Overloaded method of apply with no consumer.
     */
    @Transactional(rollbackOn = {
            ApplicantConflictException.class,
            ConflictException.class,
            NotFoundException.class,
            NullPointerException.class
    })
    public ApplyResponse apply(@Valid ApplyRequest request) {
        return apply(request, null);
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
