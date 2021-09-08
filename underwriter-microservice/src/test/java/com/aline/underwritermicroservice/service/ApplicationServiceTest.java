package com.aline.underwritermicroservice.service;

import com.aline.core.annotation.test.SpringBootUnitTest;
import com.aline.core.annotation.test.SpringTestProperties;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.core.model.ApplicationType;
import com.aline.core.model.Bank;
import com.aline.core.model.Branch;
import com.aline.core.repository.ApplicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootUnitTest(SpringTestProperties.DISABLE_WEB_SECURITY)
@Slf4j(topic = "Application Service Test")
class ApplicationServiceTest {

    private static final long FOUND = 1;
    private static final long NOT_FOUND = 2;

    @Autowired
    ApplicationService service;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ApplicantService applicantService;

    @MockBean
    MemberService memberService;

    @MockBean
    ApplicationRepository repository;

    @BeforeEach
    void setUp() {
        Applicant primary = Applicant.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .build();

        Applicant authorized = Applicant.builder()
                .id(2L)
                .firstName("Mary")
                .lastName("Smith")
                .build();

        LinkedHashSet<Applicant> applicants = new LinkedHashSet<>();
        applicants.add(primary);
        applicants.add(authorized);

        Application application = Application.builder()
                .id(FOUND)
                .applicationType(ApplicationType.CHECKING)
                .applicationStatus(ApplicationStatus.APPROVED)
                .primaryApplicant(primary)
                .applicants(applicants)
                .build();
        primary.setApplications(Collections.singleton(application));
        primary.setApplications(Collections.singleton(application));

        Bank bank = new Bank();
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setBank(bank);

        when(repository.findById(FOUND)).thenReturn(Optional.of(application));
        when(repository.findById(NOT_FOUND)).thenReturn(Optional.empty());
        when(memberService.getBranch(any())).thenReturn(branch);
    }

    @Test
    void getApplicationById_returns_applicationResponse_with_correct_info() {
        ApplicationResponse response = service.getApplicationResponseById(FOUND);
        assertEquals(1, response.getId());
        assertEquals(ApplicationStatus.APPROVED.name(), response.getApplicationStatus());
        assertEquals(ApplicationType.CHECKING.name(), response.getApplicationType());
        assertEquals(2, response.getApplicants().size());
    }

    @Test
    void getApplicationById_throws_applicationNotFoundException() {
        assertThrows(ApplicationNotFoundException.class, () -> service.getApplicationResponseById(NOT_FOUND));
    }

    @Test
    void deleteApplication_calls_deleteApplication_if_application_exists() {
        service.deleteApplication(FOUND);
        verify(repository).delete(any());
    }

    @Test
    void deleteApplication_throws_applicationNotFoundException() {
        assertThrows(ApplicationNotFoundException.class, () -> service.deleteApplication(NOT_FOUND));
    }

}
