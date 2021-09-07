package com.nfx.work.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NbpConfig {

    @Bean
    public RestTemplate template() {
        return new RestTemplate();
    }
}
