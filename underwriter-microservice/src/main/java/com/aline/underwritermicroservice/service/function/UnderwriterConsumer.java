package com.aline.underwritermicroservice.service.function;

import com.aline.core.model.ApplicationStatus;

@FunctionalInterface
public interface UnderwriterConsumer {
    void respond(ApplicationStatus status, String[] reasons);
}
