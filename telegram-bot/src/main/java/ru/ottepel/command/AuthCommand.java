package ru.ottepel.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ottepel.AsanaClient;
import ru.ottepel.storage.AbstractStorage;

/**
 * Created by savetisyan on 17/03/17
 */
@Component
public class AuthCommand extends BotCommand {
    private final AbstractStorage storage;
    private final AsanaClient asanaClient;

    @Autowired
    public AuthCommand(AbstractStorage storage, AsanaClient asanaClient) {
        super("auth", "Authorize in Asana");
        this.storage = storage;
        this.asanaClient = asanaClient;
    }

    @Override
    public void execute(AbsSender sender, User user, Chat chat, String[] strings) {
        try {
            String authLink = asanaClient.getAuthLink(user.getId());
            sender.sendMessage(new SendMessage()
                    .setText(authLink)
                    .setChatId(chat.getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
