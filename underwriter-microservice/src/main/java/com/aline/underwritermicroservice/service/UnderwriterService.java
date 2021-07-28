package com.aline.underwritermicroservice.service;

import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.underwritermicroservice.service.function.UnderwriterConsumer;
import org.springframework.stereotype.Service;

/**
 * Underwriter Service
 * <p>Used to approve or deny applications automatically.</p>
 */
@Service
public class UnderwriterService {

    /**
     * This method is used to underwrite an application.
     * The logic is not variable but the result can be used
     * within one of the functions in any way that is fit.
     * When the application is approved, the {@link UnderwriterConsumer}
     * will provide an ApplicationStatus object as a parameter in
     * the function that can be used to either apply to the application
     * or for other logic.
     * @param application The application to approve or deny.
     * @param underwriterConsumer Function for approving or denying an application.
     */
    public void underwriteApplication(Application application, UnderwriterConsumer underwriterConsumer) {
        if (!application.getApplicants().isEmpty()) { // As long as there are applicants, we will approve.
            underwriterConsumer.respond(ApplicationStatus.APPROVED, "Application was approved.");
        } else {
            underwriterConsumer.respond(ApplicationStatus.DENIED, "Applicant did not exist.");
        }
    }

}
