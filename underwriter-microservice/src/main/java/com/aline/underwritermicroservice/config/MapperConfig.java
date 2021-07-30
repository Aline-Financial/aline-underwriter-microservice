package com.aline.underwritermicroservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean configuration for {@link ModelMapper}.
 */
@Configuration
public class MapperConfig {

    /**
     * Default {@link ModelMapper}.
     * @return ModelMapper as is.
     */
    @Bean(name = "defaultModelMapper")
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    /**
     * {@link ModelMapper} that skips null values.
     * @return Configured ModelMapper with <code>setSkipNullEnabled</code> set to true.
     */
    @Bean(name = "skipNullModelMapper")
    public ModelMapper skipNulMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }

}
