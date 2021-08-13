package com.aline.underwritermicroservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.aline")
@EnableJpaRepositories("com.aline.core.repository")
@EntityScan("com.aline.core.model")
@OpenAPIDefinition(info =
        @Info(
                title = "Underwriter Microservice API",
                description = "This API provides operations to either accept or deny an application for an account as well as Applicant and Member creation and management.",
                version = "1.0"
        )
)
public class UnderwriterMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnderwriterMicroserviceApplication.class, args);
    }

}
