import com.asana.OAuthApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AsanaClient {

    @Autowired
    private OAuthApp app;

    public String getAuthLink(Long chatId) {
        return app.getAuthorizationUrl(String.valueOf(chatId));
    }

    public String authUserByCode(String code) throws IOException {
        return app.fetchToken(code);
    }
}
