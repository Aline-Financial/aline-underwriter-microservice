package com.aline.underwritermicroservice.service.function;

import com.aline.core.dto.response.ApplyResponse;

@FunctionalInterface
public interface ApplicationResponseConsumer {

    void onRespond(ApplyResponse response);

}
