package com.ganshuai.darkhorse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganshuai.darkhorse.exceptions.ResponseErrorHandlerImp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandlerImp(objectMapper));
        return restTemplate;
    }
}
