package com.aline.underwritermicroservice.controller;

import com.aline.core.annotation.test.SpringBootIntegrationTest;
import com.aline.core.annotation.test.SpringTestProperties;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.dto.request.UpdateApplicant;
import com.aline.core.exception.notfound.ApplicantNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Gender;
import com.aline.core.repository.ApplicantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link ApplicantController}
 */
@SpringBootIntegrationTest(SpringTestProperties.DISABLE_WEB_SECURITY)
@DisplayName("Applicant Controller Integration Test")
@Slf4j(topic = "Applicant Controller Integration Test")
@Sql(scripts = "/scripts/applicants.sql")
@Transactional
class ApplicantControllerTest {

    @Autowired
    ApplicantRepository repository;

    @Autowired
    MockMvc mock;

    /**
     * Object mapper used to map CreateApplicantDTO to a JSON.
     */
    @Autowired
    ObjectMapper mapper;

    /**
     * CreateApplicantDTOBuilder for modification and reuse.
     */
    static CreateApplicant.CreateApplicantBuilder createBuilder;

    @BeforeAll
    static void setUpForAll() {
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

        log.info("CreateApplicantDTOBuilder initialized.");
    }

    /**
     * Shortcut for performing a POST to <code>/applicants</code> and expect a bad request.
     * @param invalidApplicantDTO Modified {@link CreateApplicant} to be invalid. {@link ObjectMapper} <code>mapper</code> will convert this into a JSON object.
     * @throws Exception Thrown by <code>{@link MockMvc#perform(RequestBuilder)}</code>.
     */
    private void expectBadRequest(CreateApplicant invalidApplicantDTO) throws Exception {
        String body = mapper.writeValueAsString(invalidApplicantDTO);
        mock.perform(post("/applicants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Search Applicants
     * <p>
     *     Shortcut for performing a <code>GET</code> on <code>/applicants</code> with a search parameter.
     * </p>
     * <p>
     *     Expects <code>Status 200 OK</code> and Content-Type <code>application/json</code>.
     * </p>
     * @param search Keyword to search for.
     * @return ResultActions of a <code>{@link MockMvc#perform(RequestBuilder)}</code> method.
     * @throws Exception Thrown by <code>{@link MockMvc#perform(RequestBuilder)}</code>.
     */
    private ResultActions searchApplicants(String search) throws Exception {
        return mock.perform(get("/applicants")
                .param("search", search))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getApplicantById_status_is_ok_applicant_id_is_equal_to_request_id_param() throws Exception {
        mock.perform(get("/applicants/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void getApplicationById_status_is_notFound_when_applicant_does_not_exist() throws Exception {
        mock.perform(get("/applicants/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(new ApplicantNotFoundException().getMessage()));
    }

    @Test
    void createApplicant_status_is_created_and_location_is_in_header() throws Exception {

        CreateApplicant createApplicant = createBuilder.build();
        String body = mapper.writeValueAsString(createApplicant);

        mock.perform(post("/applicants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("location"));
    }

    @Test
    void updateApplicant_status_is_noContent_and_info_successfully_updated() throws Exception {
        UpdateApplicant updateApplicant = UpdateApplicant.builder()
                .firstName("Clark")
                .lastName("Kent").build();

        Applicant applicantBeforeUpdate = repository.findById(1L).orElseThrow(ApplicantNotFoundException::new);

        String body = mapper.writeValueAsString(updateApplicant);

        mock.perform(put("/applicants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNoContent());

        Applicant updatedApplicant = repository.findById(1L).orElseThrow(ApplicantNotFoundException::new);
        assertEquals(updateApplicant.getFirstName(), updatedApplicant.getFirstName());
        assertEquals(updateApplicant.getLastName(), updatedApplicant.getLastName());

        // Make sure no other values were updated.
        assertEquals(applicantBeforeUpdate.getEmail(), updatedApplicant.getEmail());
    }

    @Test
    void updateApplicant_status_is_notFound_if_applicant_exists() throws Exception {

        UpdateApplicant updateApplicant = UpdateApplicant.builder()
                .income(100000000).build();

        String body = mapper.writeValueAsString(updateApplicant);

        mock.perform(put("/applicants/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteApplicant_status_is_noContent_when_applicant_is_successfully_deleted() throws Exception {
        mock.perform(delete("/applicants/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteApplicant_status_is_notFound_when_applicant_to_delete_does_not_exists() throws Exception {
        mock.perform(delete("/applicants/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(new ApplicantNotFoundException().getMessage()));
    }

    @Test
    @Sql(scripts = "/scripts/search_applicants.sql")
    void getApplicants_status_is_ok_and_content_is_populated_only_10_elements() throws Exception {
        mock.perform(get("/applicants"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(10))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    void getApplicants_status_is_ok_and_parameters_are_applied() throws Exception {
        mock.perform(get("/applicants")
                .param("page", "1")
                .param("size", "2")
                .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[1].id").value(1));
    }

    @Test
    void getApplicants_status_is_ok_and_search_params_populate_content_with_filtered_list() throws Exception {
        mock.perform(get("/applicants")
                .param("sort", "id,asc")
                .param("search", "metropolis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("3"))
                .andExpect(jsonPath("$.content[1].id").value("4"));
    }

    /**
     * Test suite for searching for applicants using attribute value based API
     */
    @Nested
    @DisplayName("Applicant Search Tests")
    @Sql("/scripts/search_applicants.sql")
    class ApplicantSearchTest {

        @Test
        void test_searchTerm_is_empty_all_entities_are_shown() throws Exception {
            searchApplicants("")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(10))
                    .andExpect(jsonPath("$.totalElements").value(100));
        }

        @Test
        void test_singleSearchTerm_allLowerCase_all_matching_values_show() throws Exception {
            searchApplicants("michigan")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(5));
        }

        @Test
        void test_singleSearchTerm_allUpperCase_all_matching_values_show() throws Exception {
            searchApplicants("MICHIGAN")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(5));
        }

        @Test
        void test_singleSearchTerm_capitalized_all_matching_values_show() throws Exception {
            searchApplicants("Michigan")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(5));
        }

        @Test
        void test_multipleSearchTerm_allLowerCase_all_matching_values_show() throws Exception {
            searchApplicants("los angeles michigan")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(8));
        }

        @Test
        void test_multipleSearchTerm_allUpperCase_all_matching_values_show() throws Exception {
            searchApplicants("LOS ANGELES MICHIGAN")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(8));
        }

        @Test
        void test_multipleSearchTerm_capitalized_all_matching_values_show() throws Exception {
            searchApplicants("los angeles michigan")
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty())
                    .andExpect(jsonPath("$.content.length()").value(8));
        }

    }

    /**
     * Validation tests for CreateApplicant
     * <p>
     *     <em>Same validation for {@link UpdateApplicant} but fields can be nullable.</em>
     * </p>
     */
    @Nested
    @DisplayName("createApplicant status is 400 BAD REQUEST")
    class CreateApplicantStatusIsBadRequest {

        @Test
        void when_firstName_is_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.firstName("Name123").build());
        }

        @Test
        void when_firstName_is_null() throws Exception {
            expectBadRequest(createBuilder.firstName(null).build());
        }

        @Test
        void when_firstName_is_blank() throws Exception {
            expectBadRequest(createBuilder.firstName("").build());
        }

        @Test
        void when_lastName_is_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.firstName("Name123").build());
        }

        @Test
        void when_lastName_is_null() throws Exception {
            expectBadRequest(createBuilder.lastName(null).build());
        }

        @Test
        void when_lastName_is_blank() throws Exception {
            expectBadRequest(createBuilder.lastName("").build());
        }

        @Test
        void when_dateOfBirth_is_null() throws Exception {
            expectBadRequest(createBuilder.dateOfBirth(null).build());
        }

        @Test
        void when_dateOfBirth_age_is_lessThan_18() throws Exception {
            expectBadRequest(createBuilder.dateOfBirth(LocalDate.now()).build());
        }

        @Test
        void when_gender_is_null() throws Exception {
            expectBadRequest(createBuilder.gender(null).build());
        }

        @Test
        void when_email_is_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.email("invalid@email").build());
        }

        @Test
        void when_email_is_null() throws Exception {
            expectBadRequest(createBuilder.email(null).build());
        }

        @Test
        void when_email_is_blank() throws Exception {
            expectBadRequest(createBuilder.email("").build());
        }

        @Test
        void when_phone_is_not_formatted_correctly() throws Exception {
            expectBadRequest(createBuilder.phone("+1233").build());
        }

        @Test
        void when_phone_is_not_null() throws Exception {
            expectBadRequest(createBuilder.phone(null).build());
        }

        @Test
        void when_phone_is_not_blank() throws Exception {
            expectBadRequest(createBuilder.phone("").build());
        }

        @Test
        void when_socialSecurity_is_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.socialSecurity("555-5-55555").build());
        }

        @Test
        void when_socialSecurity_is_null() throws Exception {
            expectBadRequest(createBuilder.socialSecurity(null).build());
        }

        @Test
        void when_socialSecurity_is_blank() throws Exception {
            expectBadRequest(createBuilder.socialSecurity("").build());
        }

        @Test
        void when_driversLicense_is_null() throws Exception {
            expectBadRequest(createBuilder.driversLicense(null).build());
        }

        @Test
        void when_driversLicense_is_blank() throws Exception {
            expectBadRequest(createBuilder.driversLicense("").build());
        }

        @Test
        void when_income_is_null() throws Exception {
            expectBadRequest(createBuilder.income(null).build());
        }

        @Test
        void when_income_is_negative() throws Exception {
            expectBadRequest(createBuilder.income(-1).build());
        }

        @Test
        void when_address_is_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.address("123 Address").build());
        }

        @Test
        void when_address_is_null() throws Exception {
            expectBadRequest(createBuilder.address(null).build());
        }

        @Test
        void when_address_is_blank() throws Exception {
            expectBadRequest(createBuilder.address("").build());
        }

        @Test
        void when_city_is_null() throws Exception {
            expectBadRequest(createBuilder.city(null).build());
        }

        @Test
        void when_city_is_blank() throws Exception {
            expectBadRequest(createBuilder.city("").build());
        }

        @Test
        void when_state_is_null() throws Exception {
            expectBadRequest(createBuilder.state(null).build());
        }

        @Test
        void when_state_is_blank() throws Exception {
            expectBadRequest(createBuilder.state("").build());
        }

        @Test
        void when_zipcode_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.zipcode("123-654").build());
        }

        @Test
        void when_zipcode_is_null() throws Exception {
            expectBadRequest(createBuilder.zipcode(null).build());
        }

        @Test
        void when_zipcode_is_blank() throws Exception {
            expectBadRequest(createBuilder.zipcode("").build());
        }

        @Test
        void when_mailingAddress_is_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.mailingAddress("123 Address").build());
        }

        @Test
        void when_mailingAddress_is_null() throws Exception {
            expectBadRequest(createBuilder.mailingAddress(null).build());
        }

        @Test
        void when_mailingAddress_is_blank() throws Exception {
            expectBadRequest(createBuilder.mailingAddress("").build());
        }

        @Test
        void when_mailingCity_is_null() throws Exception {
            expectBadRequest(createBuilder.mailingCity(null).build());
        }

        @Test
        void when_mailingCity_is_blank() throws Exception {
            expectBadRequest(createBuilder.mailingCity("").build());
        }

        @Test
        void when_mailingState_is_null() throws Exception {
            expectBadRequest(createBuilder.mailingState(null).build());
        }

        @Test
        void when_mailingState_is_blank() throws Exception {
            expectBadRequest(createBuilder.mailingState("").build());
        }

        @Test
        void when_mailingZipcode_not_well_formed() throws Exception {
            expectBadRequest(createBuilder.mailingZipcode("123-654").build());
        }

        @Test
        void when_mailingZipcode_is_null() throws Exception {
            expectBadRequest(createBuilder.mailingZipcode(null).build());
        }

        @Test
        void when_mailingZipcode_is_blank() throws Exception {
            expectBadRequest(createBuilder.mailingZipcode("").build());
        }

    }

}
