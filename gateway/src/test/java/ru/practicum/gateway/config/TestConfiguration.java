package ru.practicum.gateway.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class TestConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return mock(RestTemplate.class);
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(RestTemplate restTemplate) {
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);

        when(builder.uriTemplateHandler(any(DefaultUriBuilderFactory.class))).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        return builder;
    }
}
