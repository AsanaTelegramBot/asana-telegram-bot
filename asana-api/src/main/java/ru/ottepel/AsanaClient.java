package ru.ottepel;

import com.asana.Client;
import com.asana.OAuthApp;
import com.asana.models.Project;
import com.asana.models.User;
import com.asana.models.Webhook;
import com.asana.requests.ItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

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
        Client client = createClient(accessToken);
        return client.users.me().execute();
    }

    public List<Project> getProjects(String workspaceId, String accessToken) throws IOException {
        Client client = createClient(accessToken);
        return client.projects.findByWorkspace(workspaceId).execute();
    }

    public Webhook subscribe(String id, String url, String accessToken) throws IOException {
        Client client = createClient(accessToken);
        ItemRequest<Webhook> request = client.webhooks.create();
        request.query("resource", id);
        request.query("target", url);
        return request.execute();
    }

    private Client createClient(String accessToken) {
        OAuthApp app = new OAuthApp(
                asanaClientId,
                asanaClientSecret,
                "urn:ietf:wg:oauth:2.0:oob",
                accessToken
        );
        return Client.oauth(app);
    }
}
