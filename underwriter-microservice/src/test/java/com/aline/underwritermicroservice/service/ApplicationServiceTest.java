package com.aline.underwritermicroservice.service;

import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ApplicationServiceTest {

    private static final long FOUND = 1;
    private static final long NOT_FOUND = 2;

    @Autowired
    ApplicationService service;

    @MockBean
    ApplicationRepository repository;

    @BeforeEach
    void setUp() {

        Application application = Application.builder()
                .id(FOUND)
                .primaryApplicant(
                        Applicant.builder()
                                .id(1L)
                                .firstName("John")
                                .lastName("Smith").build()
                )
                .build();

        when(repository.findById(FOUND)).thenReturn(Optional.of(application));
        when(repository.findById(NOT_FOUND)).thenReturn(Optional.empty());
    }

    @Test
    void getById_returns_application_with_correct_information() {
        ApplicationResponse response = service.getById(FOUND);
        assertEquals(response.getId(), FOUND);
        assertEquals(response.getPrimaryApplicant().getFirstName(), "John");
        assertEquals(response.getPrimaryApplicant().getLastName(), "Smith");
    }

    @Test
    void getById_throws_applicationNotFoundException() {
        assertThrows(ApplicationNotFoundException.class, () -> service.getById(NOT_FOUND));
    }

}
