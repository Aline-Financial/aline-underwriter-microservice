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
     * Send an approval email to the primary applicant of the ApplyResponse.
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


    /**
     * Send a denial email to the primary applicant of the ApplyResponse.
     * @param response The ApplyResponse returned by the Underwriter service.
     */
    public void sendDenialEmail(ApplyResponse response) {
        ApplicantResponse primaryApplicant = getPrimaryApplicantResponse(response);
        String email = primaryApplicant.getEmail();
        String name = primaryApplicant.getFirstName();
        String landingPortalUrl = appConfig.getLandingPortal();
        String reason = String.join(", ", response.getReasons());

        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("reason", reason);
        variables.put("landingPortalUrl", landingPortalUrl);

        emailService.sendHtmlEmail("Thank you for applying!", "application/denied-notification", email, variables);
    }

    /**
     * Send a pending email to the primary applicant of the ApplyResponse.
     * @param response The ApplyResponse returned by the Underwriter service.
     */
    public void sendPendingEmail(ApplyResponse response) {
        ApplicantResponse primaryApplicant = getPrimaryApplicantResponse(response);
        String email = primaryApplicant.getEmail();
        String name = primaryApplicant.getFirstName();
        String landingPortalUrl = appConfig.getLandingPortal();
        String reason = String.join(", ", response.getReasons());

        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("reason", reason);
        variables.put("landingPortalUrl", landingPortalUrl);

        emailService.sendHtmlEmail("Thank you for applying!", "application/pending-notification", email, variables);
    }

    private ApplicantResponse getPrimaryApplicantResponse(ApplyResponse response) {
        return response.getApplicants().get(0);
    }

    /**
     * Send an email based on the application status
     * @param response The application response that contains the status
     */
    public void sendEmailBasedOnStatus(ApplyResponse response) {
        switch (response.getStatus()) {
            case APPROVED:
                sendApprovalEmail(response);
                break;
            case PENDING:
                sendPendingEmail(response);
                break;
            case DENIED:
                sendDenialEmail(response);
                break;
        }
    }

}
