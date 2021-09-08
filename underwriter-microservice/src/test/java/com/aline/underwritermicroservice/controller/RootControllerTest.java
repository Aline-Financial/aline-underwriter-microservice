package com.aline.underwritermicroservice.controller;

import com.aline.core.annotation.test.SpringBootIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link RootController}
 */
@SpringBootIntegrationTest
class RootControllerTest {

    @Autowired
    MockMvc mock;

    @Test
    void test_healthCheck_response_code_is_200() throws Exception {
        mock.perform(get("/health"))
                .andExpect(status().isOk());
    }

}
