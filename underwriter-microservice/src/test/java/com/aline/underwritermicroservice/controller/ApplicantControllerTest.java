package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link ApplicantController}
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Applicant Controller Integration Test")
@Slf4j(topic = "Applicant Controller Integration Test")
class ApplicantControllerTest {

    @Autowired
    MockMvc mock;

    @Autowired
    ApplicantRepository repository;

    /**
     * Object mapper used to map CreateApplicantDTO to a JSON.
     */
    static ObjectMapper mapper;

    /**
     * CreateApplicantDTOBuilder for modification and reuse.
     */
    static CreateApplicantDTO.CreateApplicantDTOBuilder dtoBuilder;

    @BeforeAll
    static void setUpForAll() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getDefault());
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(dateFormat);
        dtoBuilder = CreateApplicantDTO.builder()
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
    }

    @BeforeEach
    void setUp() {
        List<Applicant> applicants = Arrays.asList(
                Applicant.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Smith")
                        .gender("Male")
                        .dateOfBirth(LocalDate.of(1995, 6, 23))
                        .email("johnsmith@email.com")
                        .phone("(222) 222-2222")
                        .socialSecurity("222-22-2222")
                        .driversLicense("DL222222")
                        .address("321 Main St.")
                        .city("Townsville")
                        .state("Maine")
                        .zipcode("12345")
                        .mailingAddress("PO Box 1234")
                        .mailingCity("Townsville")
                        .mailingState("Maine")
                        .mailingZipcode("12345")
                        .income(7500000)
                        .build(),

                Applicant.builder()
                        .id(2L)
                        .firstName("Mary")
                        .lastName("Jane")
                        .gender("Female")
                        .dateOfBirth(LocalDate.of(1998, 2, 12))
                        .email("maryjane@email.com")
                        .phone("(444) 444-4444")
                        .socialSecurity("444-44-4444")
                        .driversLicense("DL444444")
                        .address("888 County Lane")
                        .city("Village City")
                        .state("New Jersey")
                        .zipcode("54321")
                        .mailingAddress("888 County Lane")
                        .mailingCity("Village City")
                        .mailingState("New Jersey")
                        .mailingZipcode("54321")
                        .income(12000000)
                        .build(),

                Applicant.builder()
                        .id(3L)
                        .firstName("Bruce")
                        .lastName("Wayne")
                        .gender("Male")
                        .dateOfBirth(LocalDate.of(1980, 10, 13))
                        .email("iambatman@email.com")
                        .phone("(999) 999-9999")
                        .socialSecurity("999-99-9999")
                        .driversLicense("BAT99999")
                        .address("500 Wayne Manor")
                        .city("Gotham")
                        .state("Illinois")
                        .zipcode("99999")
                        .mailingAddress("PO Box 9999")
                        .mailingCity("Metropolis")
                        .mailingState("New York")
                        .mailingZipcode("54321")
                        .income(100000000)
                        .build()
        );
        repository.saveAll(applicants);
    }

    /**
     * Shortcut for performing a POST to <code>/applicants</code> and expect a bad request.
     * @param invalidApplicantDTO Modified {@link CreateApplicantDTO} to be invalid. {@link ObjectMapper} <code>mapper</code> will convert this into a JSON object.
     * @throws Exception thrown from <code>perform</code>.
     */
    private void expectBadRequest(CreateApplicantDTO invalidApplicantDTO) throws Exception {
        String body = mapper.writeValueAsString(invalidApplicantDTO);
        mock.perform(post("/applicants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getApplicantById_status_is_ok_applicant_id_is_equal_to_request_id_param() throws Exception {
        mock.perform(get("/applicants/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getApplicationById_status_is_notFound_when_applicant_does_not_exist() throws Exception {
        mock.perform(get("/applicants/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(new ApplicantNotFoundException().getMessage()));
    }

    @Test
    void createApplicant_status_is_created_and_location_is_in_header() throws Exception {

        CreateApplicantDTO createApplicantDTO = dtoBuilder.build();
        String body = mapper.writeValueAsString(createApplicantDTO);

        mock.perform(post("/applicants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("location"));
    }

    @Nested
    @DisplayName("createApplicant status is 400 BAD REQUEST")
    class CreateApplicantStatusIsBadRequest {

        @Test
        void when_firstName_is_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.firstName("Name123").build());
        }

        @Test
        void when_firstName_is_null() throws Exception {
            expectBadRequest(dtoBuilder.firstName(null).build());
        }

        @Test
        void when_firstName_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.firstName("").build());
        }

        @Test
        void when_lastName_is_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.firstName("Name123").build());
        }

        @Test
        void when_lastName_is_null() throws Exception {
            expectBadRequest(dtoBuilder.lastName(null).build());
        }

        @Test
        void when_lastName_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.lastName("").build());
        }

        @Test
        void when_dateOfBirth_is_null() throws Exception {
            expectBadRequest(dtoBuilder.dateOfBirth(null).build());
        }

        @Test
        void when_dateOfBirth_age_is_lessThan_18() throws Exception {
            expectBadRequest(dtoBuilder.dateOfBirth(LocalDate.now()).build());
        }

        @Test
        void when_gender_is_null() throws Exception {
            expectBadRequest(dtoBuilder.gender(null).build());
        }

        @Test
        void when_gender_value_is_not_allowed() throws Exception {
            // Allowed gender values: Male, Female, Other, Not Specified
            expectBadRequest(dtoBuilder.gender("InvalidGender").build());
        }

        @Test
        void when_email_is_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.email("invalid@email").build());
        }

        @Test
        void when_email_is_null() throws Exception {
            expectBadRequest(dtoBuilder.email(null).build());
        }

        @Test
        void when_email_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.email("").build());
        }

        @Test
        void when_phone_is_not_formatted_correctly() throws Exception {
            expectBadRequest(dtoBuilder.phone("+1233").build());
        }

        @Test
        void when_phone_is_not_null() throws Exception {
            expectBadRequest(dtoBuilder.phone(null).build());
        }

        @Test
        void when_phone_is_not_blank() throws Exception {
            expectBadRequest(dtoBuilder.phone("").build());
        }

        @Test
        void when_socialSecurity_is_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.socialSecurity("555-5-55555").build());
        }

        @Test
        void when_socialSecurity_is_null() throws Exception {
            expectBadRequest(dtoBuilder.socialSecurity(null).build());
        }

        @Test
        void when_socialSecurity_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.socialSecurity("").build());
        }

        @Test
        void when_driversLicense_is_null() throws Exception {
            expectBadRequest(dtoBuilder.driversLicense(null).build());
        }

        @Test
        void when_driversLicense_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.driversLicense("").build());
        }

        @Test
        void when_income_is_null() throws Exception {
            expectBadRequest(dtoBuilder.income(null).build());
        }

        @Test
        void when_income_is_negative() throws Exception {
            expectBadRequest(dtoBuilder.income(-1).build());
        }

        @Test
        void when_address_is_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.address("123 Address").build());
        }

        @Test
        void when_address_is_null() throws Exception {
            expectBadRequest(dtoBuilder.address(null).build());
        }

        @Test
        void when_address_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.address("").build());
        }

        @Test
        void when_city_is_null() throws Exception {
            expectBadRequest(dtoBuilder.city(null).build());
        }

        @Test
        void when_city_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.city("").build());
        }

        @Test
        void when_state_is_null() throws Exception {
            expectBadRequest(dtoBuilder.state(null).build());
        }

        @Test
        void when_state_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.state("").build());
        }

        @Test
        void when_zipcode_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.zipcode("123-654").build());
        }

        @Test
        void when_zipcode_is_null() throws Exception {
            expectBadRequest(dtoBuilder.zipcode(null).build());
        }

        @Test
        void when_zipcode_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.zipcode("").build());
        }

        @Test
        void when_mailingAddress_is_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.mailingAddress("123 Address").build());
        }

        @Test
        void when_mailingAddress_is_null() throws Exception {
            expectBadRequest(dtoBuilder.mailingAddress(null).build());
        }

        @Test
        void when_mailingAddress_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.mailingAddress("").build());
        }

        @Test
        void when_mailingCity_is_null() throws Exception {
            expectBadRequest(dtoBuilder.mailingCity(null).build());
        }

        @Test
        void when_mailingCity_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.mailingCity("").build());
        }

        @Test
        void when_mailingState_is_null() throws Exception {
            expectBadRequest(dtoBuilder.mailingState(null).build());
        }

        @Test
        void when_mailingState_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.mailingState("").build());
        }

        @Test
        void when_mailingZipcode_not_well_formed() throws Exception {
            expectBadRequest(dtoBuilder.mailingZipcode("123-654").build());
        }

        @Test
        void when_mailingZipcode_is_null() throws Exception {
            expectBadRequest(dtoBuilder.mailingZipcode(null).build());
        }

        @Test
        void when_mailingZipcode_is_blank() throws Exception {
            expectBadRequest(dtoBuilder.mailingZipcode("").build());
        }

    }
}
