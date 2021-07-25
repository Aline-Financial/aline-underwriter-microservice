package com.aline.underwritermicroservice.controller;

import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.repository.ApplicationRepository;
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


}
