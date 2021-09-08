package com.aline.underwritermicroservice.service;

import com.aline.core.annotation.test.SpringBootUnitTest;
import com.aline.core.annotation.test.SpringTestProperties;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.request.UpdateApplicant;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.EmailConflictException;
import com.aline.core.exception.conflict.PhoneConflictException;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Gender;
import com.aline.core.repository.ApplicantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.aline.core.dto.request.CreateApplicant.CreateApplicantBuilder;
import static com.aline.core.dto.request.UpdateApplicant.UpdateApplicantBuilder;
import static com.aline.core.model.Applicant.ApplicantBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootUnitTest(SpringTestProperties.DISABLE_WEB_SECURITY)
class ApplicantServiceTest {

    private final long FOUND = 1;
    private final long NOT_FOUND = 2;

    @Autowired
    ApplicantService service;

    /**
     * Mocked {@link ApplicantRepository}
     * <p>
     *     Mock repository queries.
     * </p>
     */
    @MockBean
    ApplicantRepository repository;

    CreateApplicantBuilder createBuilder;
    UpdateApplicantBuilder updateBuilder;
    Applicant foundApplicant;

    @BeforeEach
    void setUp() {
        createBuilder = CreateApplicant.builder()
                .firstName("Test")
                .lastName("Boy")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1980, 5, 3))
                .email("testboy@test.com")
                .phone("(555) 555-5555")
                .socialSecurity("555-55-5555")
                .driversLicense("DL555555")
                .address("1234 Address St.")
                .city("Townsville")
                .state("Maine")
                .zipcode("12345")
                .mailingAddress("PO Box 1234")
                .mailingCity("Townsville")
                .mailingState("Maine")
                .mailingZipcode("12345")
                .income(4500000);

        updateBuilder = UpdateApplicant.builder();

        ApplicantBuilder applicantBuilder = Applicant.builder()
                .id(1L)
                .firstName("Test")
                .lastName("Boy")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1980, 5, 3))
                .email("testboy@test.com")
                .phone("(555) 555-5555")
                .socialSecurity("555-55-5555")
                .driversLicense("DL555555")
                .address("1234 Address St.")
                .city("Townsville")
                .state("Maine")
                .zipcode("12345")
                .mailingAddress("PO Box 1234")
                .mailingCity("Townsville")
                .mailingState("Maine")
                .mailingZipcode("12345")
                .income(4500000)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now());

        foundApplicant = applicantBuilder.build();

        when(repository.findById(FOUND)).thenReturn(Optional.of(foundApplicant));
        when(repository.findById(NOT_FOUND)).thenReturn(Optional.empty());
        when(repository.save(applicantBuilder
                .id(null)
                .createdAt(null)
                .lastModifiedAt(null)
                .build())).thenReturn(applicantBuilder.build());
        when(repository.existsByEmail("already.exists@email.com")).thenReturn(true);
        when(repository.existsByPhone("(222) 222-2222")).thenReturn(true);
        when(repository.existsByDriversLicense("ALREADY_EXISTS")).thenReturn(true);
        when(repository.existsBySocialSecurity("222-22-2222")).thenReturn(true);
    }

    @Test
    void getApplicantById_returns_applicant_with_correct_id() {
        ApplicantResponse applicant = service.getApplicantById(1L);
        assertEquals(1, applicant.getId());
    }

    @Test
    void getApplicantById_throws_applicantNotFoundException_when_applicant_does_not_exist() {
        assertThrows(ApplicantNotFoundException.class, () -> service.getApplicantById(2L));
    }

    @Test
    void createApplicant_returns_applicant_with_correct_unique_identifiers() {
        CreateApplicant dto = createBuilder.build();
        ApplicantResponse created = service.createApplicant(dto);
        assertEquals(dto.getFirstName(), created.getFirstName());
        assertEquals(dto.getLastName(), created.getLastName());
        assertEquals(dto.getEmail(), created.getEmail());
        assertEquals(dto.getSocialSecurity(), created.getSocialSecurity());
        assertEquals(dto.getDriversLicense(), created.getDriversLicense());
    }

    @Test
    void createApplicant_throws_emailConflictException_when_email_already_exists() {
        CreateApplicant dto = createBuilder
                .email("already.exists@email.com")
                .build();

        assertThrows(EmailConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void createApplicant_throws_phoneConflictException_when_phoneNumber_already_exists() {
        CreateApplicant dto = createBuilder
                .phone("(222) 222-2222")
                .build();

        assertThrows(PhoneConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void createApplicant_throws_conflictException_when_driversLicense_already_exists() {
        CreateApplicant dto = createBuilder
                .driversLicense("ALREADY_EXISTS")
                .build();

        assertThrows(ConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void createApplicant_throws_conflictException_when_socialSecurity_already_exists() {
        CreateApplicant dto = createBuilder
                .socialSecurity("222-22-2222")
                .build();

        assertThrows(ConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void updateApplicant_throws_emailConflictException_when_email_already_exists() {
        UpdateApplicant dto = updateBuilder
                .email("already.exists@email.com")
                .build();

        assertThrows(EmailConflictException.class, () -> service.updateApplicant(FOUND, dto));
    }

    @Test
    void updateApplicant_throws_phoneConflictException_when_phoneNumber_already_exists() {
        UpdateApplicant dto = updateBuilder
                .phone("(222) 222-2222")
                .build();

        assertThrows(PhoneConflictException.class, () -> service.updateApplicant(FOUND, dto));
    }

    @Test
    void updateApplicant_throws_conflictException_when_driversLicense_already_exists() {
        UpdateApplicant dto = updateBuilder
                .driversLicense("ALREADY_EXISTS")
                .build();

        assertThrows(ConflictException.class, () -> service.updateApplicant(FOUND, dto));
    }

    @Test
    void updateApplicant_throws_conflictException_when_socialSecurity_already_exists() {
        UpdateApplicant dto = updateBuilder
                .socialSecurity("222-22-2222")
                .build();

        assertThrows(ConflictException.class, () -> service.updateApplicant(FOUND, dto));
    }

    @Test
    void updateApplicant_throws_applicantNotFoundException_when_applicant_does_not_exist() {
        UpdateApplicant dto = updateBuilder
                .firstName("NewName")
                .build();

        assertThrows(ApplicantNotFoundException.class, () -> service.updateApplicant(NOT_FOUND, dto));
    }

    @Test
    void updateApplicant_calls_repository_save_when_applicant_exists() {
        service.updateApplicant(FOUND, updateBuilder.build());
        verify(repository, times(1)).save(foundApplicant);
    }

    @Test
    void deleteApplicant_throws_applicantNotFoundException_when_applicant_does_not_exist() {
        assertThrows(ApplicantNotFoundException.class, () -> service.deleteApplicant(NOT_FOUND));
    }

    @Test
    void deleteApplicant_calls_repository_delete_when_applicant_exists() {
        service.deleteApplicant(FOUND);
        verify(repository, times(1)).delete(foundApplicant);
    }

}
