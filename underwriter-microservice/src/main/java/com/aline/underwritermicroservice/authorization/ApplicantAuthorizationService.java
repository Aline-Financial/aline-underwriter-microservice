package com.aline.underwritermicroservice.authorization;

import com.aline.core.model.Applicant;
import com.aline.core.model.user.UserRole;
import com.aline.core.security.service.AbstractAuthorizationService;
import com.aline.underwritermicroservice.service.ApplicantService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("applicantAuth")
@RequiredArgsConstructor
public class ApplicantAuthorizationService extends AbstractAuthorizationService<Long> {

    private final ApplicantService service;

    @Override
    public boolean canAccess(@NonNull Long id) {
        Applicant applicant = service.getApplicantByUsername(getUsername());
        return (Objects.equals(applicant.getId(), id) ||
                getRole() == UserRole.ADMINISTRATOR ||
                getRole() == UserRole.EMPLOYEE);
    }
}
