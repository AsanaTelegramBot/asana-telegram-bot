package config;

import com.asana.OAuthApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Config {

    @Value("asana.clientId")
    private static String ASANA_CLIENT_ID;

    @Value("asana.clientSecret")
    private static String ASANA_CLIENT_SECRET;

    @Bean
    public OAuthApp app() {
        return new OAuthApp(
                ASANA_CLIENT_ID,
                ASANA_CLIENT_SECRET,
                OAuthApp.NATIVE_REDIRECT_URI
        );
    }

}

