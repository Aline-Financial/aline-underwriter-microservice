package com.aline.underwritermicroservice.service;

import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.core.model.ApplicationType;
import com.aline.core.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j(topic = "Application Service Test")
public class ApplicationServiceTest {

    private static final long FOUND = 1;
    private static final long NOT_FOUND = 2;

    @Autowired
    ApplicationService service;

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

        Set<Applicant> applicants = new HashSet<>();
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
        assertEquals("John", response.getPrimaryApplicant().getFirstName());
        assertEquals("Smith", response.getPrimaryApplicant().getLastName());
        assertEquals(2, response.getApplicants().size());
    }

    @Test
    void getApplicationById_throws_applicationNotFoundException() {
        assertThrows(ApplicationNotFoundException.class, () -> service.getApplicationById(NOT_FOUND));
    }



}
