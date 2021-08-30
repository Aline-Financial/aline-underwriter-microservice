package com.aline.underwritermicroservice.authorization;

import com.aline.core.model.Applicant;
import com.aline.core.model.user.MemberUser;
import com.aline.core.model.user.UserRole;
import com.aline.core.security.service.AbstractAuthorizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("applicantAuth")
@RequiredArgsConstructor
public class ApplicantAuthorizationService extends AbstractAuthorizationService<Long> {

    @Override
    public boolean canAccess(@NonNull Long id) {
        if (getUser().getUserRole() == UserRole.MEMBER) {
            MemberUser user = (MemberUser) getUser();
            Applicant applicant = user.getMember().getApplicant();
            return Objects.equals(applicant.getId(), id);
        }

        return roleIsManagement();
    }
}
