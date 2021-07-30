package com.aline.underwritermicroservice.service.function;

import com.aline.core.dto.response.ApplyResponse;

@FunctionalInterface
public interface ApplyResponseConsumer {

    void onRespond(ApplyResponse response);

}
