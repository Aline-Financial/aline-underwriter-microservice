package com.aline.underwritermicroservice.authorization;

import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.repository.ApplicationRepository;
import com.aline.core.security.service.AbstractAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("applicationAuth")
@RequiredArgsConstructor
public class ApplicationAuthorizationService extends AbstractAuthorizationService<ApplicationResponse> {

    private final ApplicationRepository repository;

    @Override
    public boolean canAccess(ApplicationResponse returnObject) {
        return false;
    }
}
