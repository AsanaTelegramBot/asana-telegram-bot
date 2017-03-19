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
    @Value("${host}")
    public String host;

    @Value("${telegram.botName}")
    private String botName;

    @Value("${telegram.botToken}")
    private String botToken;

    private final AsanaClient asanaClient;
    private final TypeAheadSearch searcher;
    private final AbstractStorage storage;

    @Autowired
    public AsanaTelegramBot(AbstractStorage storage, AsanaClient asanaClient, TypeAheadSearch searcher) {
        this.storage = storage;
        this.asanaClient = asanaClient;
        this.searcher = searcher;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasInlineQuery()) {
            try {
                AnswerInlineQuery answer = searcher.search(update);
                answerInlineQuery(answer);
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            if (update.hasCallbackQuery()) {
                try {
                    processCallbackQuery(update.getCallbackQuery());
                } catch (TelegramApiException e) {
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
                storage.saveUser(update.getMessage().getChatId(), tgUser);

                sendMessage(new SendMessage()
                        .setChatId(message.getChatId())
                        .setParseMode("Markdown")
                        .setText(String.format("Hello, *%s*!\nUse /help for more information.", userInfo.name)));
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCallbackQuery(CallbackQuery query) throws TelegramApiException {
        String[] split = query.getData().split(" ");
        String command = split[0];
        String id = split[1];
        String name = "";
        if (split.length == 3) {
            name = split[2];
        }

        TelegramUser user = storage.getUser(query.getFrom().getId());
        SendMessage message = new SendMessage()
                .setChatId(query.getMessage().getChatId());
        String url = host + "/webhooks/" + query.getMessage().getChatId();
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
                try {
                    asanaClient.subscribe(id, url, user.getToken());
                    message.setText("You've subscribed to " + name + "!");
                } catch (IOException e) {
                    message.setText("You're already subscribed!");
                }
                break;
            case "webhookList":
                try {
                    List<Webhook> webhooks = asanaClient.getWebhooks(id, user.getToken());

                    if (webhooks.size() > 0) {
                        message.setText("Choose webhook");
                    } else {
                        message.setText("No subscriptions found");
                    }

                    message.setReplyMarkup(new InlineKeyboardMarkup()
                            .setKeyboard(generateWebhookKeyboard(webhooks)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "webhook":
                try {
                    asanaClient.unsubscribe(id, user.getToken());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message.setText("You've unsubscribed from " + split[2]);
                break;
            case "subscribeAll":
                try {
                    asanaClient.subscribeToAll(id, url, user.getToken());
                    message.setText("You've subscribed to all projects in this workspace");
                } catch (IOException e) {
                    message.setText("You're don't have project in this workspace");
                    e.printStackTrace();
                }
                break;
            case "unsubscribeAll":
                try {
                    asanaClient.unsubscribe(id, user.getToken());
                    message.setText("You've unsubscribed for all projects in " + split[2] + " workspace");
                } catch (IOException e) {
                    message.setText("You're don't have active subscribes in this workspace");
                    e.printStackTrace();
                }
                break;
        }

        sendMessage(message);
    }

    private List<List<InlineKeyboardButton>> generateKeyboard(List<Project> projects) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        for (Project project : projects) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(project.name);
            button.setCallbackData("project " + project.id + " " + project.name);
            keyboardRows.add(Collections.singletonList(button));
        }

        return keyboardRows;
    }

    private List<List<InlineKeyboardButton>> generateWebhookKeyboard(List<Webhook> webhooks) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        for (Webhook webhook : webhooks) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(webhook.resource.name);
            button.setCallbackData("webhook " + webhook.id + " " + webhook.resource.name);
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
