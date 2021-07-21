package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.CreateApplicantDTO;
import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.matches;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
@Slf4j(topic = "Applicant Controller Integration Test")
class ApplicantControllerTest {

    @Autowired
    MockMvc mock;

    @Autowired
    ApplicantRepository repository;

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

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void test_getApplicantById_status_is_ok_applicant_id_is_equal_to_request_id_param() throws Exception {
        mock.perform(get("/applicants/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void test_createApplicant_status_is_created_and_location_is_in_header() throws Exception {
        CreateApplicantDTO dto = CreateApplicantDTO.builder()
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
                .build();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getDefault());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(dateFormat);
        String body = mapper.writeValueAsString(dto);

        mock.perform(post("/applicants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"));
    }

}
