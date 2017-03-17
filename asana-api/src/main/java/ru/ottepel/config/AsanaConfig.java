package ru.ottepel.config;

import com.asana.OAuthApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AsanaConfig {
    @Value("${asana.clientId:297359328358654}")
    private String asanaClientId;

    @Value("${asana.clientSecret:e49fa13e01a9c03aecf01ad6f5ee417d}")
    private String asanaClientSecret;

    @Bean
    public OAuthApp app() {
        return new OAuthApp(
                asanaClientId,
                asanaClientSecret,
                OAuthApp.NATIVE_REDIRECT_URI
        );
    }
}

