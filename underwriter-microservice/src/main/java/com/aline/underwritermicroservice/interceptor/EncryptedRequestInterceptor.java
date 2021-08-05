package com.aline.underwritermicroservice.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.crypto.Cipher;
import java.lang.reflect.Type;

@ControllerAdvice
public class EncryptedRequestInterceptor extends RequestBodyAdviceAdapter {

    private Cipher decrypt;

    @Autowired
    public void setDecrypt(@Qualifier("DECRYPT_MODE") Cipher decrypt) {
        this.decrypt = decrypt;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        return false;
    }
}
