package com.aline.underwritermicroservice.controller.advice;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.request.CreateApplicant;
import com.aline.core.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApplyRequestHandler extends RequestBodyAdviceAdapter {

    private Cipher decrypt;

    @Autowired
    private void setDecrypt(@Qualifier("DECRYPT_MODE") Cipher decrypt) {
        this.decrypt = decrypt;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.getParameterType() == ApplyRequest.class;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (body instanceof ApplyRequest) {
            ApplyRequest request = (ApplyRequest) body;

            LinkedHashSet<CreateApplicant> applicants = request.getApplicants().stream()
                    .map(this::decryptApplicant)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            request.setApplicants(applicants);
            return request;
        }
        return body;
    }

    private CreateApplicant decryptApplicant(CreateApplicant createApplicant) {
        try {
            String decryptedDriversLicence = new String(decrypt
                    .doFinal(createApplicant.getDriversLicense()
                            .getBytes(StandardCharsets.UTF_8)));
            String decryptedSocialSecurity = new String(decrypt
                    .doFinal(createApplicant.getSocialSecurity()
                            .getBytes(StandardCharsets.UTF_8)));
            createApplicant.setDriversLicense(decryptedDriversLicence);
            createApplicant.setSocialSecurity(decryptedSocialSecurity);
            return createApplicant;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new BadRequestException("Unable to read encrypted application.");
        }
    }
}
