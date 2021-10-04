package com.aline.underwritermicroservice.service;

import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.underwritermicroservice.service.function.UnderwriterConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Underwriter Service
 * <p>Used to approve or deny applications automatically.</p>
 */
@Service
public class UnderwriterService {

    public static class DenyReasons {
        public static final String INSUFFICIENT_INCOME = "Income is insufficient.";
    }

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

        List<String> reasons = new ArrayList<>();

        checkIncome(application, reasons);

        if (reasons.isEmpty()) { // Income must be over or equal to $15,000.00 annually.
            underwriterConsumer.respond(ApplicationStatus.APPROVED, new String[]{"Application was approved"});
        } else {
            underwriterConsumer.respond(ApplicationStatus.DENIED, reasons.toArray(new String[0]));
        }
    }

    private void checkIncome(Application application, List<String> reasons) {
        Applicant primaryApplicant = application.getPrimaryApplicant();
        checkCondition(primaryApplicant.getIncome() < 1500000,
                        DenyReasons.INSUFFICIENT_INCOME,
                        reasons);
    }

    private void checkCondition(boolean condition, String reason, List<String> reasons) {
        if (condition) {
            reasons.add(reason);
        }
    }

}
