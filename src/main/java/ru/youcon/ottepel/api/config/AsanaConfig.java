package ru.youcon.ottepel.api.config;

import com.asana.OAuthApp;
import com.asana.models.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.youcon.ottepel.api.util.json.EventDeserializer;


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

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Event.class, new EventDeserializer());
        mapper.registerModule(simpleModule);
        return mapper;
    }
}

