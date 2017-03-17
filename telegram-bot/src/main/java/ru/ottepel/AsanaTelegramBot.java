package ru.ottepel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.ottepel.command.TypeAheadSearch;
import ru.ottepel.storage.AbstractStorage;

import java.io.IOException;

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
            try {
                Message message = update.getMessage();
                String token = asanaClient.authUserByCode(message.getText().trim());
                sendMessage(new SendMessage()
                        .setChatId(message.getChatId())
                        .setText(token));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }

    public void clearWebhook() throws TelegramApiRequestException {
    }
}
