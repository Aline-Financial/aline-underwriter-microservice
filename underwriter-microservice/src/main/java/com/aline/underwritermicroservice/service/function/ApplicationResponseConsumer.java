package com.aline.underwritermicroservice.service.function;

import com.aline.core.dto.response.ApplicationResponse;

@FunctionalInterface
public interface ApplicationResponseConsumer {

    void onRespond(ApplicationResponse response);

}
