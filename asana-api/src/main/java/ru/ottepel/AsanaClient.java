package ru.ottepel;

import com.asana.OAuthApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AsanaClient {
    private final OAuthApp app;

    @Autowired
    public AsanaClient(OAuthApp app) {
        this.app = app;
    }

    public String getAuthLink(Integer chatId) {
        return app.getAuthorizationUrl(String.valueOf(chatId));
    }

    public String authUserByCode(String code) throws IOException {
        return app.fetchToken(code);
    }
}
