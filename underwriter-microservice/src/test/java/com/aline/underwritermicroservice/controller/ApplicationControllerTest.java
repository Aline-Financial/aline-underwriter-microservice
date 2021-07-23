package com.aline.underwritermicroservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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

}
