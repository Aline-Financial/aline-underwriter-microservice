package com.aline.underwritermicroservice.crypto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TestAttributeEncryptor implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
