package com.aline.underwritermicroservice.service;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.dto.UpdateApplicantDTO;
import com.aline.core.exception.ConflictException;
import com.aline.core.exception.conflict.EmailConflictException;
import com.aline.core.exception.conflict.PhoneConflictException;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.aline.core.model.Applicant.ApplicantBuilder;
import static com.aline.core.dto.CreateApplicantDTO.CreateApplicantDTOBuilder;
import static com.aline.core.dto.UpdateApplicantDTO.UpdateApplicantDTOBuilder;

@SpringBootTest
class ApplicantServiceTest {

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

    CreateApplicantDTOBuilder createApplicantDTOBuilder;
    UpdateApplicantDTOBuilder updateApplicantDTOBuilder;

    @BeforeEach
    void setUp() {
        createApplicantDTOBuilder = CreateApplicantDTO.builder()
                .firstName("Test")
                .lastName("Boy")
                .gender("Male")
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

        updateApplicantDTOBuilder = UpdateApplicantDTO.builder();

        ApplicantBuilder applicantBuilder = Applicant.builder()
                .id(1L)
                .firstName("Test")
                .lastName("Boy")
                .gender("Male")
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

        when(repository.findById(1L)).thenReturn(Optional.of(applicantBuilder.build()));
        when(repository.findById(2L)).thenReturn(Optional.empty());
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
        Applicant applicant = service.getApplicantById(1L);
        assertEquals(1, applicant.getId());
    }

    @Test
    void getApplicantById_throws_applicantNotFoundException_when_applicant_does_not_exist() {
        assertThrows(ApplicantNotFoundException.class, () -> service.getApplicantById(2L));
    }

    @Test
    void createApplicant_returns_applicant_with_correct_unique_identifiers() {
        CreateApplicantDTO dto = createApplicantDTOBuilder.build();
        Applicant created = service.createApplicant(dto);
        assertEquals(dto.getFirstName(), created.getFirstName());
        assertEquals(dto.getLastName(), created.getLastName());
        assertEquals(dto.getEmail(), created.getEmail());
        assertEquals(dto.getSocialSecurity(), created.getSocialSecurity());
        assertEquals(dto.getDriversLicense(), created.getDriversLicense());
    }

    @Test
    void createApplicant_throws_emailConflictException_when_email_already_exists() {
        CreateApplicantDTO dto = createApplicantDTOBuilder
                .email("already.exists@email.com")
                .build();

        assertThrows(EmailConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void createApplicant_throws_phoneConflictException_when_phoneNumber_already_exists() {
        CreateApplicantDTO dto = createApplicantDTOBuilder
                .phone("(222) 222-2222")
                .build();

        assertThrows(PhoneConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void createApplicant_throws_conflictException_when_driversLicense_already_exists() {
        CreateApplicantDTO dto = createApplicantDTOBuilder
                .driversLicense("ALREADY_EXISTS")
                .build();

        assertThrows(ConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void createApplicant_throws_conflictException_when_socialSecurity_already_exists() {
        CreateApplicantDTO dto = createApplicantDTOBuilder
                .socialSecurity("222-22-2222")
                .build();

        assertThrows(ConflictException.class, () -> service.createApplicant(dto));
    }

    @Test
    void updateApplicant_throws_emailConflictException_when_email_already_exists() {
        UpdateApplicantDTO dto = updateApplicantDTOBuilder
                .email("already.exists@email.com")
                .build();

        assertThrows(EmailConflictException.class, () -> service.updateApplicant(1, dto));
    }

    @Test
    void updateApplicant_throws_phoneConflictException_when_phoneNumber_already_exists() {
        UpdateApplicantDTO dto = updateApplicantDTOBuilder
                .phone("(222) 222-2222")
                .build();

        assertThrows(PhoneConflictException.class, () -> service.updateApplicant(1, dto));
    }

    @Test
    void updateApplicant_throws_conflictException_when_driversLicense_already_exists() {
        UpdateApplicantDTO dto = updateApplicantDTOBuilder
                .driversLicense("ALREADY_EXISTS")
                .build();

        assertThrows(ConflictException.class, () -> service.updateApplicant(1, dto));
    }

    @Test
    void updateApplicant_throws_conflictException_when_socialSecurity_already_exists() {
        UpdateApplicantDTO dto = updateApplicantDTOBuilder
                .socialSecurity("222-22-2222")
                .build();

        assertThrows(ConflictException.class, () -> service.updateApplicant(1, dto));
    }

}
