package ru.ottepel.command;

import com.asana.Client;
import com.asana.OAuthApp;
import com.asana.models.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.AbstractStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by savetisyan on 17/03/17
 */
public class TypeAheadSearch {

    @Value("${asana.clientId:297359328358654}")
    private String asanaClientId;

    @Value("${asana.clientSecret:e49fa13e01a9c03aecf01ad6f5ee417d}")
    private String asanaClientSecret;

    @Autowired
    private AbstractStorage storage;

    private List<String> allowTypes = new ArrayList<>();

    public TypeAheadSearch(AbstractStorage storage) {
        this.storage = storage;
    }

    public AnswerInlineQuery search(Update update) throws IOException {
        InlineQuery inlineQuery = update.getInlineQuery();
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
//      answer.setCacheTime(CACHE_TIME);
        answer.setSwitchPmText("Login in Asana...");
        answer.setSwitchPmParameter(String.valueOf(update.getInlineQuery().getFrom().getId()));

        System.out.println(update.getMessage().getFrom().getId());

        answer.setResults(getResults(update.getMessage().getFrom().getId(), update.getInlineQuery()));
        return answer;
    }

    private List<InlineQueryResult> getResults(int chatId, InlineQuery inlineQuery) throws IOException {
        List<InlineQueryResult> results = new ArrayList<>();
        String queryLine = inlineQuery.getQuery();

        int spaceIndex = queryLine.indexOf(" ");
        String type = queryLine.substring(0, spaceIndex);
        String query = queryLine.substring(spaceIndex + 1, queryLine.length() - 1);

        System.out.println(type);
        System.out.println(query);

        TelegramUser tgUser = storage.getUser(chatId);

        OAuthApp app = new OAuthApp(
                asanaClientId,
                asanaClientSecret,
                "urn:ietf:wg:oauth:2.0:oob",
                tgUser.getToken()
        );

        Client client = Client.oauth(app);

        List<Workspace> workspaces = client.workspaces.findAll().execute();

        for (Workspace workspace : workspaces) {
            List<Workspace> typeAheads = client.workspaces
                    .typeahead(workspace.id)
                    .query("type", type)
                    .query("query", query)
                    .execute();
            if (typeAheads.size() > 0) {
                Workspace typeAhead = typeAheads.get(0);
                System.out.println(typeAhead.id + " | " + typeAhead.name);
            }
        }

        return results;
    }
}
