package com.aline.underwritermicroservice.authorization;

import com.aline.core.model.Applicant;
import com.aline.core.model.Application;
import com.aline.core.model.user.UserRole;
import com.aline.core.repository.ApplicantRepository;
import com.aline.core.security.service.AbstractAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("applicationAuth")
@RequiredArgsConstructor
@Slf4j
public class ApplicationAuthorizationService extends AbstractAuthorizationService<Application> {

    private final ApplicantRepository applicantRepository;

    @Override
    public boolean canAccess(Application returnObject) {
        Optional<Applicant> optionalApplicant = applicantRepository
                .findApplicantByUsername(getUsername());
        if (getRole() == UserRole.MEMBER && optionalApplicant.isPresent()) {
            Applicant userApplicant = optionalApplicant.get();
            return returnObject.getApplicants().contains(userApplicant);
        }
        return roleIsManagement();
    }
}
