package com.aline.underwritermicroservice.controller;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.ApplicationType;
import com.aline.core.model.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Application Controller Integration Test")
@Slf4j(topic = "Application Controller Integration Test")
@Sql(scripts = {"/scripts/applicants.sql", "/scripts/applications.sql"})
@Transactional
class ApplicationControllerTest {

    @Autowired
    MockMvc mock;

    @Autowired
    ObjectMapper mapper;

    @Test
    void getApplicationById_status_is_ok_applicationId_is_equalTo_pathVariable() throws Exception {
        int applicationId = 1;
        mock.perform(get("/applications/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(applicationId))
                .andDo(print());

    }

    @Test
    void getApplicationById_status_is_notFound_when_application_does_not_exist() throws Exception {
        int applicationId = 999;
        mock.perform(get("/applications/{id}", applicationId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(new ApplicationNotFoundException().getMessage()));
    }

    @Test
    void apply_status_is_created_and_location_is_in_header() throws Exception {

        CreateApplicant createApplicant = CreateApplicant.builder()
                .firstName("Richard")
                .lastName("Donovan")
                .email("rickdonovan@email.com")
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
        LinkedHashSet<CreateApplicant> applicants = new LinkedHashSet<>();
        applicants.add(createApplicant);
        ApplyRequest applyRequest = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .applicants(applicants)
                .build();

        String body = mapper.writeValueAsString(applyRequest);

        mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andDo(print());
    }

    @Test
    void apply_status_is_conflict_when_an_applicant_already_exists() throws Exception {

        CreateApplicant createApplicant = CreateApplicant.builder()
                .firstName("Richard")
                .lastName("Donovan")
                .email("johnsmith@email.com") // Already exists in DB
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
        LinkedHashSet<CreateApplicant> applicants = new LinkedHashSet<>();
        applicants.add(createApplicant);
        ApplyRequest applyRequest = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .applicants(applicants)
                .build();

        String body = mapper.writeValueAsString(applyRequest);

        mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andDo(print());
    }


}
