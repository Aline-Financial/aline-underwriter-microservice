package com.aline.underwritermicroservice.controller;

import com.aline.core.annotation.test.SpringBootIntegrationTest;
import com.aline.core.annotation.test.SpringTestProperties;
import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.ApplicationType;
import com.aline.core.model.Gender;
import com.aline.core.repository.AccountRepository;
import com.aline.core.repository.MemberRepository;
import com.aline.underwritermicroservice.service.ApplicationEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootIntegrationTest(SpringTestProperties.DISABLE_WEB_SECURITY)
@DisplayName("Application Controller Integration Test")
@Slf4j(topic = "Application Controller Integration Test")
@Sql(scripts = {"/scripts/applicants.sql", "/scripts/applications.sql"})
@Transactional
class ApplicationControllerTest {

    @Autowired
    MockMvc mock;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ApplicationEmailService emailService;

    @BeforeEach
    void setUp() {
        // Prevent an HBO Max and don't send emails during integration tests.
        doNothing().when(emailService).sendEmailBasedOnStatus(any());
    }

    @Test
    void getApplicationById_status_is_ok_applicationId_is_equalTo_pathVariable() throws Exception {
        int applicationId = 1;
        mock.perform(get("/applications/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(applicationId));

    }

    @Test
    void getApplicationById_status_is_notFound_when_application_does_not_exist() throws Exception {
        int applicationId = 999;
        mock.perform(get("/applications/{id}", applicationId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(new ApplicationNotFoundException().getMessage()));
    }

    @Test
    void apply_status_is_created_and_location_is_in_header_all_resources_are_created() throws Exception {

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
                .income(4500000)
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

        MvcResult result = mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        String resourceLocation = response.getHeader("location");
        log.info("Location: {}", resourceLocation);

        assertNotNull(resourceLocation);

        mock.perform(get(resourceLocation))
                .andExpect(status().isOk());


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
                .noApplicants(false)
                .build();

        String body = mapper.writeValueAsString(applyRequest);

        mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void apply_status_is_created_when_noApplicants_is_true_and_the_applicantIds_are_provided() throws Exception {

        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        ApplyRequest request = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .noApplicants(true)
                .applicantIds(ids)
                .build();

        String body = mapper.writeValueAsString(request);

        mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"));
    }

    @Test
    void apply_status_is_notFound_when_noApplicants_is_true_and_the_applicantIds_are_provided_but_one_id_does_not_exists() throws Exception {

        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(99L);

        ApplyRequest request = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .noApplicants(true)
                .applicantIds(ids)
                .build();

        String body = mapper.writeValueAsString(request);

        mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void apply_status_is_badRequest_when_noApplicants_is_true_and_the_applicantIds_are_not_provided() throws Exception {
        ApplyRequest request = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .noApplicants(true)
                .build();

        String body = mapper.writeValueAsString(request);

        mock.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        ApplyRequest request2 = ApplyRequest.builder()
                .applicationType(ApplicationType.CHECKING)
                .noApplicants(true)
                .applicantIds(new LinkedHashSet<>())
                .build();

        String body2 = mapper.writeValueAsString(request2);

        mock.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isBadRequest());
    }
}
