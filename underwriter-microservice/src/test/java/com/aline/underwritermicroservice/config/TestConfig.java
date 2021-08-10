package com.aline.underwritermicroservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.Convert;
import javax.persistence.Converter;

@ActiveProfiles("test")
@Configuration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {Converter.class, Convert.class}
        )
})
public class TestConfig {
}
