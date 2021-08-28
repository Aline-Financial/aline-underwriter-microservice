package com.aline.underwritermicroservice.authorization;

import com.aline.core.model.Applicant;
import com.aline.core.repository.ApplicantRepository;
import com.aline.core.security.service.AbstractAuthorizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component("applicantAuth")
@RequiredArgsConstructor
public class ApplicantAuthorizationService extends AbstractAuthorizationService<Long> {

    private final ApplicantRepository repository;

    @Override
    public boolean canAccess(@NonNull Long id) {
        Optional<Applicant> applicantOptional = repository.findApplicantByUsername(getUsername());
        if (applicantOptional.isPresent()) {
            Applicant applicant = applicantOptional.get();
            return (Objects.equals(applicant.getId(), id) || roleIsManagement());
        }
        return false;
    }
}
