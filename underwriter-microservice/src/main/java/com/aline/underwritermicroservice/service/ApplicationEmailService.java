package com.aline.underwritermicroservice.service;

import com.aline.core.aws.email.EmailService;
import com.aline.core.config.AppConfig;
import com.aline.core.dto.response.ApplicantResponse;
import com.aline.core.dto.response.ApplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * The application email service sends emails
 * using the {@link EmailService}. It sends approval
 * and denial emails when a user attempts to apply for
 * a bank account.
 */
@Service
@RequiredArgsConstructor
public class ApplicationEmailService {

    private final EmailService emailService;
    private final AppConfig appConfig;

    /**
     * Send an approval email using the application-approved-template.html
     * file in the resources of this project.
     * @param response The ApplyResponse returned by the Underwriter service.
     */
    public void sendApprovalEmail(ApplyResponse response) {
        ApplicantResponse primaryApplicant = response.getApplicants().get(0);
        String email = primaryApplicant.getEmail();
        String name = primaryApplicant.getFirstName();
        String membershipId = response.getCreatedMembers().get(0).getMembershipId();
        String landingPortalUrl = appConfig.getLandingPortal();
        String memberDashboardUrl = appConfig.getMemberDashboard() + "/get-started";

        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("membershipId", membershipId);
        variables.put("landingPortalUrl", landingPortalUrl);
        variables.put("memberDashboardUrl", memberDashboardUrl);

        emailService.sendHtmlEmail("Welcome to Aline Financial", "application/approved-notification", email, variables);

    }

}
