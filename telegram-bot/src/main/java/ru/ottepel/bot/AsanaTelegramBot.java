package ru.ottepel.bot;

import com.asana.models.Project;
import com.asana.models.User;
import com.asana.models.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.ottepel.api.AsanaClient;
import ru.ottepel.command.TypeAheadSearch;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.AbstractStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by savetisyan on 17/03/17
 */
@Component
public class AsanaTelegramBot extends TelegramLongPollingCommandBot {
    @Value("${telegram.botName}")
    private String botName;

    @Value("${telegram.botToken}")
    private String botToken;

    private final AsanaClient asanaClient;
    private AbstractStorage storage;
    private TypeAheadSearch searcher;

    @Autowired
    public AsanaTelegramBot(AbstractStorage storage, AsanaClient asanaClient) {
        this.storage = storage;
        this.searcher = new TypeAheadSearch(storage);
        this.asanaClient = asanaClient;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasInlineQuery()) {
            AnswerInlineQuery answer = searcher.search(update);
            try {
                answerInlineQuery(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            if (update.hasCallbackQuery()) {
                try {
                    processCallbackQuery(update.getCallbackQuery());
                } catch (IOException | TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }

            try {
                Message message = update.getMessage();
                String token = asanaClient.authUserByCode(message.getText().trim());
                User userInfo = asanaClient.getUserInfo(token);

                TelegramUser tgUser = new TelegramUser();
                tgUser.setId(update.getMessage().getFrom().getId());
                tgUser.setUser(userInfo);
                tgUser.setToken(token);
                storage.saveUser(tgUser);

                sendMessage(new SendMessage()
                        .setChatId(message.getChatId())
                        .setText(String.format("Hello, %s!", userInfo.name)));
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCallbackQuery(CallbackQuery query) throws IOException, TelegramApiException {
        String[] split = query.getData().split(" ");
        String command = split[0];
        String id = split[1];

        TelegramUser user = storage.getUser(query.getFrom().getId());
        SendMessage message = new SendMessage()
                .setChatId(query.getMessage().getChatId());
        switch (command) {
            case "workspace":
                try {
                    List<Project> projects = asanaClient.getProjects(id, user.getToken());
                    message.setText("Choose project");
                    message.setReplyMarkup(new InlineKeyboardMarkup()
                            .setKeyboard(generateKeyboard(projects)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "project":
                Webhook subscribe = asanaClient.subscribe(id, "https://05047605.ngrok.io/webhooks/" + query.getMessage().getChatId(), user.getToken());
                message.setText("You've subscribed to " + id);
                break;
        }

        sendMessage(message);
    }

    private List<List<InlineKeyboardButton>> generateKeyboard(List<Project> projects) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        for (Project project : projects) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(project.name);
            button.setCallbackData("project " + project.id);
            keyboardRows.add(Collections.singletonList(button));
        }

        return keyboardRows;
    }

    public String getBotUsername() {
        return "AsanaOttepelBot";
    }

    public String getBotToken() {
        return "338480209:AAF9L-zDYHfJTso4tpQWMuXDZvdsLrABcH0";
    }

    public void clearWebhook() throws TelegramApiRequestException {
    }
}
