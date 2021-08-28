package com.aline.underwritermicroservice.config;

import com.aline.core.annotation.WebSecurityConfiguration;
import com.aline.core.security.config.AbstractWebSecurityConfig;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@WebSecurityConfiguration
public class WebSecurityConfig extends AbstractWebSecurityConfig {
    @Override
    protected void configureHttp(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/applications")
                .permitAll();
    }
}
