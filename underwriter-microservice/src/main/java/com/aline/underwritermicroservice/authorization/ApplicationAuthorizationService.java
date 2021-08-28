package com.aline.underwritermicroservice.authorization;

import com.aline.core.model.Application;
import com.aline.core.model.user.MemberUser;
import com.aline.core.repository.ApplicationRepository;
import com.aline.core.security.service.AbstractAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("applicationAuth")
@RequiredArgsConstructor
@Slf4j
public class ApplicationAuthorizationService extends AbstractAuthorizationService<Application> {

    private final ApplicationRepository repository;

    @Override
    public boolean canAccess(Application returnObject) {
        MemberUser user = (MemberUser) getUser();
        log.info("Current user: {}", user);
        return false;
    }
}
