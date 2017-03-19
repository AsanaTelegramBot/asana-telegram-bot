package ru.ottepel.command;

import com.asana.Client;
import com.asana.OAuthApp;
import com.asana.models.Project;
import com.asana.models.Task;
import com.asana.models.User;
import com.asana.models.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle;
import ru.ottepel.api.AsanaClient;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.AbstractStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 17/03/17
 */
@Component
public class TypeAheadSearch {
    @Value("${asana.clientId:297359328358654}")
    private String asanaClientId;

    @Value("${asana.clientSecret:e49fa13e01a9c03aecf01ad6f5ee417d}")
    private String asanaClientSecret;

    private final AbstractStorage storage;
    private final AsanaClient asanaClient;

    @Autowired
    public TypeAheadSearch(AbstractStorage storage, AsanaClient client) {
        this.storage = storage;
        this.asanaClient = client;
    }

    public AnswerInlineQuery search(Update update) throws IOException {
        InlineQuery inlineQuery = update.getInlineQuery();
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
//      answer.setCacheTime(CACHE_TIME);
        answer.setSwitchPmText("Login in Asana...");

        Integer id = update.getInlineQuery().getFrom().getId();
        answer.setSwitchPmParameter(String.valueOf(id));
        answer.setResults(getResults(id, update.getInlineQuery()));
        return answer;
    }

    private List<InlineQueryResult> getResults(int chatId, InlineQuery inlineQuery) throws IOException {
        List<InlineQueryResult> results = new ArrayList<>();
        String queryLine = inlineQuery.getQuery();

        int spaceIndex = queryLine.indexOf(" ");
        if (spaceIndex == -1) {
            return results;
        }

        String type = queryLine.substring(0, spaceIndex);
        String query = queryLine.substring(spaceIndex + 1, queryLine.length());

        TelegramUser tgUser = storage.getUser(chatId);
        if (tgUser == null) {
            return results;
        }

        OAuthApp app = new OAuthApp(
                asanaClientId,
                asanaClientSecret,
                "urn:ietf:wg:oauth:2.0:oob",
                tgUser.getToken()
        );

        Client client = Client.oauth(app);
        List<Workspace> workspaces = client.workspaces.findAll().execute();
        int id = 0;
        for (Workspace workspace : workspaces) {
            List<Workspace> typeAheads = client.workspaces
                    .typeahead(workspace.id)
                    .query("type", type)
                    .query("query", query)
                    .execute();

            if (typeAheads.size() > 0) {
                Workspace typeAhead = typeAheads.get(0);
                System.out.println(typeAhead.id + " | " + typeAhead.name);

                StringBuilder description = new StringBuilder();
                String photo = null;
                switch (type) {
                    case "project":
                        Project project = asanaClient.getProject(typeAhead.id, tgUser.getToken());
                        Collection<User> members = project.members;
                        String team = String.join(", ", members.stream().map(x -> x.name).collect(Collectors.toList()));
                        description
                                .append("*Name:*").append(project.name)
                                .append("\n")
                                .append("*Owner:* ").append(project.owner != null ? project.owner.name : "No ownner")
                                .append("\n")
                                .append("*Members:* ").append(team);
                        break;
                    case "task":
                        Task task = asanaClient.getTask(typeAhead.id, tgUser.getToken());
                        description
                                .append("*Name:*").append(task.name)
                                .append("\n")
                                .append("*Assignee:* ").append(task.assignee != null ? task.assignee.name : "No ownner")
                                .append("\n")
                                .append("*Tags:*").append(task.tags)
                                .append("\n")
                                .append("*Completed:* ").append(task.completed);
                        break;
                    case "user":
                        User user = asanaClient.getUser(typeAhead.id, tgUser.getToken());
                        description
                                .append("*Name:*").append(user.name)
                                .append("\n")
                                .append("*Email:* ").append(user.email);
                        photo = user.photo != null ? user.photo.image_128x128 : null;
                        break;
                }

                InputTextMessageContent messageContent = new InputTextMessageContent();
                messageContent.disableWebPagePreview();
                messageContent.enableMarkdown(true);
                messageContent.setMessageText(description.toString());

                InlineQueryResultArticle inlineQueryResult = new InlineQueryResultArticle();
                inlineQueryResult.setTitle(typeAhead.name);
                inlineQueryResult.setId(String.valueOf(id++));
                inlineQueryResult.setInputMessageContent(messageContent);

                if (photo != null) {
                    inlineQueryResult.setUrl(photo);
                    inlineQueryResult.setThumbWidth(128);
                    inlineQueryResult.setThumbHeight(128);
                }

                results.add(inlineQueryResult);
            }
        }

        return results;
    }
}
