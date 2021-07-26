package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.core.model.ApplicationType;
import com.aline.core.model.Gender;
import com.aline.core.repository.ApplicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j(topic = "Application Service Test")
public class ApplicationServiceTest {

    private static final long FOUND = 1;
    private static final long NOT_FOUND = 2;

    @Autowired
    ApplicationService service;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ApplicantService applicantService;

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

        when(repository.findById(FOUND)).thenReturn(Optional.of(application));
        when(repository.findById(NOT_FOUND)).thenReturn(Optional.empty());
    }

    @Test
    void getApplicationById_returns_applicationResponse_with_correct_info() {
        ApplicationResponse response = service.getApplicationById(FOUND);
        assertEquals(1, response.getId());
        assertEquals(ApplicationStatus.APPROVED.toString(), response.getStatus());
        assertEquals(ApplicationType.CHECKING.toString(), response.getType());
        assertEquals(2, response.getApplicants().size());
    }

    @Test
    void getApplicationById_throws_applicationNotFoundException() {
        assertThrows(ApplicationNotFoundException.class, () -> service.getApplicationById(NOT_FOUND));
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

    @Test
    void apply_returns_creates_applicants_calls_all_the_correct_methods() throws Exception {
        CreateApplicant createApplicant = CreateApplicant.builder()
                .firstName("John")
                .lastName("Smith")
                .email("johnsmith@email.com")
                .phone("(555) 555-5555")
                .dateOfBirth(LocalDate.of(1990, 8, 9))
                .gender(Gender.MALE)
                .socialSecurity("555-55-5555")
                .driversLicense("ABC123456789")
                .address("123 Address St")
                .city("Townsville")
                .state("Idaho")
                .zipcode("83202")
                .mailingAddress("123 Address St")
                .mailingCity("Townsville")
                .mailingState("Idaho")
                .mailingZipcode("83202")
                .build();
        ApplicantResponse applicantResponse = ApplicantResponse.builder()
                .id(3L)
                .firstName("John")
                .lastName("Smith")
                .email("johnsmith@email.com")
                .phone("(555) 555-5555")
                .dateOfBirth(LocalDate.of(1990, 8, 9))
                .gender("Male")
                .socialSecurity("555-55-5555")
                .driversLicense("ABC123456789")
                .address("123 Address St")
                .city("Townsville")
                .state("Idaho")
                .zipcode("83202")
                .mailingAddress("123 Address St")
                .mailingCity("Townsville")
                .mailingState("Idaho")
                .mailingZipcode("83202")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        Applicant applicant = Applicant.builder()
                .id(3L)
                .firstName("John")
                .lastName("Smith")
                .email("johnsmith@email.com")
                .phone("(555) 555-5555")
                .dateOfBirth(LocalDate.of(1990, 8, 9))
                .gender(Gender.MALE)
                .socialSecurity("555-55-5555")
                .driversLicense("ABC123456789")
                .address("123 Address St")
                .city("Townsville")
                .state("Idaho")
                .zipcode("83202")
                .mailingAddress("123 Address St")
                .mailingCity("Townsville")
                .mailingState("Idaho")
                .mailingZipcode("83202")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();
        LinkedHashSet<CreateApplicant> applicants = new LinkedHashSet<>();
        applicants.add(createApplicant);
        ApplyRequest applyRequest = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .applicants(applicants)
                .build();

        Application application = Application.builder()
                .id(1L)
                .primaryApplicant(applicant)
                .applicants(Collections.singleton(applicant))
                .applicationType(ApplicationType.CHECKING)
                .applicationStatus(ApplicationStatus.APPROVED)
                .build();

        when(applicantService.createApplicant(createApplicant)).thenReturn(applicantResponse);
        when(repository.save(any())).thenReturn(application);

        service.apply(applyRequest);

        // Verify that the applicant service is being called the right amount of times.
        verify(applicantService, times(applyRequest.getApplicants().size())).createApplicant(any());

        // Verify application is created once.
        verify(repository, times(1)).save(any());

    }

}
