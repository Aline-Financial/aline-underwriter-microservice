package com.aline.underwritermicroservice;

import com.aline.underwritermicroservice.controller.ApplicantController;
import com.aline.underwritermicroservice.controller.RootController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SmokeTest {

    @Autowired
    UnderwriterMicroserviceApplication application;

    @Autowired
    RootController rootController;

    @Autowired
    ApplicantController applicantController;

    @Test
    void contextLoads() {
        assertNotNull(application);
        assertNotNull(rootController);
        assertNotNull(applicantController);
    }

}
