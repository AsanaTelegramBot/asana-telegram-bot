package ru.ottepel;

import com.asana.Client;
import com.asana.OAuthApp;
import com.asana.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AsanaClient {
    private final OAuthApp app;

    @Autowired
    public AsanaClient(OAuthApp app) {
        this.app = app;
    }

    @Value("${asana.clientId:297359328358654}")
    private String asanaClientId;

    @Value("${asana.clientSecret:e49fa13e01a9c03aecf01ad6f5ee417d}")
    private String asanaClientSecret;

    public String getAuthLink(Integer chatId) {
        return app.getAuthorizationUrl(String.valueOf(chatId));
    }

    public String authUserByCode(String code) throws IOException {
        return app.fetchToken(code);
    }

    public User getUserInfo(String accessToken) throws IOException {
        OAuthApp app = new OAuthApp(
                asanaClientId,
                asanaClientSecret,
                "urn:ietf:wg:oauth:2.0:oob",
                accessToken
        );
        Client client = Client.oauth(app);
        return client.users.me().execute();
    }
}
