package com.aline.underwritermicroservice;

import com.aline.core.annotation.test.SpringBootUnitTest;
import com.aline.underwritermicroservice.controller.ApplicantController;
import com.aline.underwritermicroservice.controller.RootController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootUnitTest
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

    @Test
    void usingCorrectVersions() {
        assertNotNull(SpringVersion.getVersion());
        assertTrue(SpringVersion.getVersion().startsWith("5"));
    }

}
