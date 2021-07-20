package com.aline.underwritermicroservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RootControllerTest {

    @Autowired
    MockMvc mock;

    @Test
    void test_healthCheck_response_code_is_200() throws Exception {
        mock.perform(get("/health"))
                .andExpect(status().isOk());
    }

}
