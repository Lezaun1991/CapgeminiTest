package com.capgemini.test.code.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguracion {

    @Bean
    public RestTemplate restTemplate() {
            // 👇 Simple fábrica con configuración de timeout sin dependencias externas
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(20000); // 10 segundos para conectar
            factory.setReadTimeout(20000);    // 10 segundos para leer

            return new RestTemplate(factory);
    }
}
